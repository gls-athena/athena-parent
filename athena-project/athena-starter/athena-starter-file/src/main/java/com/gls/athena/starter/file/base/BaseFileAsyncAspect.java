package com.gls.athena.starter.file.base;

import cn.hutool.core.util.IdUtil;
import com.gls.athena.common.bean.result.Result;
import com.gls.athena.common.core.constant.FileTypeEnums;
import com.gls.athena.starter.async.domain.AsyncTaskStatus;
import com.gls.athena.starter.async.manager.IAsyncTaskManager;
import com.gls.athena.starter.async.util.AopUtil;
import com.gls.athena.starter.file.domain.FileAsyncRequest;
import com.gls.athena.starter.file.generator.FileGenerator;
import com.gls.athena.starter.file.manager.IFileManager;
import com.gls.athena.starter.web.util.WebUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;

import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * 文件异步处理切面接口，用于拦截带有特定响应注解的方法并实现异步逻辑处理
 *
 * @param <Response> 响应注解类型，必须是 Annotation 的子类型
 * @author lizy19
 */
@Slf4j
@RequiredArgsConstructor
public abstract class BaseFileAsyncAspect<Generator extends FileGenerator<Response>, Response extends Annotation> {

    private final List<Generator> generators;

    private final IAsyncTaskManager<?> asyncTaskManager;

    private final IFileManager fileManager;

    private final Executor executor;

    /**
     * 环绕通知方法，用于处理异步响应逻辑。
     * 若检测到当前请求为异步处理模式，则启动后台任务进行文件生成，并立即返回任务ID给前端；
     * 否则按正常流程执行原方法。
     *
     * @param joinPoint 连接点对象，包含被拦截方法的信息
     * @param response  响应对象，用于判断是否需要异步处理
     * @return 如果是同步处理则返回原方法执行结果；如果是异步处理则返回null（表示响应已由本方法处理）
     * @throws Throwable 方法执行过程中抛出的异常
     */
    public Object around(ProceedingJoinPoint joinPoint, Response response) throws Throwable {
        // 如果响应对象为空或不是异步响应，则直接执行原方法
        if (response == null || !isAsync(response)) {
            return joinPoint.proceed();
        }

        // 生成唯一的任务ID
        String taskId = IdUtil.randomUUID();

        // 创建异步请求对象并设置相关属性
        FileAsyncRequest<Response> fileAsyncRequest = new FileAsyncRequest<>();
        fileAsyncRequest.setTaskId(taskId);
        fileAsyncRequest.setResponse(response);
        fileAsyncRequest.setJoinPoint(joinPoint);

        CompletableFuture.runAsync(() -> handleFileAsync(fileAsyncRequest), executor);
        // 发布异步请求事件
        log.info("异步文件导出任务已提交，任务ID: {}, 方法: {}", taskId, joinPoint.getSignature().getName());

        // 立即响应客户端任务ID
        Result<String> result = Result.success("任务已提交，请稍后查看", taskId);
        WebUtil.writeJson(result);

        // 返回null，表示响应已经处理完成
        return null;
    }

    /**
     * 异步处理文件导出任务的核心逻辑。
     * 包括创建任务、调用业务方法获取数据、生成文件以及更新任务状态等操作。
     *
     * @param request 包含任务信息的异步请求对象
     */
    private void handleFileAsync(FileAsyncRequest<Response> request) {
        String taskId = request.getTaskId();
        ProceedingJoinPoint joinPoint = request.getJoinPoint();
        String filename = getFilename(request.getResponse());
        Map<String, Object> params = AopUtil.getParams(joinPoint);
        params.put("filename", filename);

        // 创建任务并更新状态为处理中
        asyncTaskManager.createTask(taskId, getCode(), getName(), getDescription(), params);
        asyncTaskManager.updateTaskStatus(taskId, AsyncTaskStatus.PROCESSING);

        try {
            // 更新进度至20%
            asyncTaskManager.updateTaskProgress(taskId, 20);

            // 执行被拦截的方法获取数据
            Object data = joinPoint.proceed();
            asyncTaskManager.updateTaskProgress(taskId, 40);

            // 获取文件输出流
            String type = getFileType(request.getResponse()).getCode();
            String filePath = fileManager.generateFilePath(type, filename);
            OutputStream outputWrapper = fileManager.getFileOutputStream(filePath);
            asyncTaskManager.updateTaskProgress(taskId, 60);

            // 生成文件
            try (OutputStream outputStream = outputWrapper) {
                generators.stream()
                        .filter(generator -> generator.supports(request.getResponse()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("不支持的文件类型: " + getFileType(request.getResponse()).getExtension()))
                        .generate(data, request.getResponse(), outputStream);
                asyncTaskManager.updateTaskProgress(taskId, 80);
            }

            // 验证文件是否生成成功
            if (fileManager.exists(filePath) && fileManager.getFileSize(filePath) > 0) {
                asyncTaskManager.completeTask(taskId, Map.of("filePath", filePath));
                log.info("异步文件导出完成: taskId={}, filePath={}", taskId, filePath);
            } else {
                asyncTaskManager.failTask(taskId, "文件生成失败或文件为空");
            }
        } catch (Throwable e) {
            log.error("异步文件导出失败: taskId={}", taskId, e);
            asyncTaskManager.failTask(taskId, e.getMessage());
        }
    }

    /**
     * 获取任务编码
     *
     * @return 任务编码字符串
     */
    protected abstract String getCode();

    /**
     * 获取任务名称
     *
     * @return 任务名称字符串
     */
    protected abstract String getName();

    /**
     * 获取任务描述
     *
     * @return 任务描述字符串
     */
    protected abstract String getDescription();

    /**
     * 根据响应注解获取文件名
     *
     * @param response 响应注解对象
     * @return 文件名字符串
     */
    protected abstract String getFilename(Response response);

    /**
     * 根据响应注解获取文件类型枚举
     *
     * @param response 响应注解对象
     * @return 文件类型枚举
     */
    protected abstract FileTypeEnums getFileType(Response response);

    /**
     * 判断当前响应是否为异步处理模式
     *
     * @param response 响应注解对象
     * @return true 表示需要异步处理，false 表示同步处理
     */
    protected abstract boolean isAsync(Response response);

}
