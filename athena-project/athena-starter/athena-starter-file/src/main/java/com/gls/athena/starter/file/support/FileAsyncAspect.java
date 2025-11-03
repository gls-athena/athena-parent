package com.gls.athena.starter.file.support;

import cn.hutool.core.util.IdUtil;
import com.gls.athena.common.bean.result.Result;
import com.gls.athena.common.core.constant.FileTypeEnums;
import com.gls.athena.starter.async.domain.AsyncTaskStatus;
import com.gls.athena.starter.async.manager.IAsyncTaskManager;
import com.gls.athena.starter.async.util.AopUtil;
import com.gls.athena.starter.file.domain.FileAsyncContext;
import com.gls.athena.starter.file.domain.FileInfo;
import com.gls.athena.starter.file.domain.FileResponseWrapper;
import com.gls.athena.starter.file.generator.FileGeneratorManager;
import com.gls.athena.starter.file.manager.FileManager;
import com.gls.athena.starter.web.util.WebUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * 文件异步处理切面接口，用于拦截带有特定响应注解的方法并实现异步逻辑处理
 *
 * @author george
 */
@Slf4j
@Aspect
@Component
public class FileAsyncAspect {

    /**
     * 进度百分比
     */
    private static final int PROGRESS_TASK_CREATED = 20;
    private static final int PROGRESS_DATA_RETRIEVED = 40;
    private static final int PROGRESS_FILE_PATH_PREPARED = 60;
    private static final int PROGRESS_FILE_GENERATED = 80;
    private static final int PROGRESS_COMPLETED = 100;

    @Resource
    private FileGeneratorManager fileGeneratorManager;
    @Resource
    private IAsyncTaskManager asyncTaskManager;
    @Resource
    private FileManager fileManager;
    @Resource
    private Executor executor;

    /**
     * 环绕通知方法，用于处理控制器层的异步文件导出请求
     * 该方法会拦截所有controller包下的方法执行，判断是否需要异步处理文件导出任务
     *
     * @param joinPoint 连接点对象，包含被拦截方法的信息和执行上下文
     * @return Object 原方法的返回值，对于异步处理的情况返回null
     * @throws Throwable 方法执行过程中可能抛出的异常
     */
    @Around("execution(* *..controller..*(..))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        FileResponseWrapper<?> responseWrapper = FileResponseWrapper.withMethod(AopUtil.getMethod(joinPoint));
        // 早期返回：如果不是异步响应，直接执行原方法
        if (responseWrapper == null || !responseWrapper.async()) {
            return joinPoint.proceed();
        }

        // 异步处理逻辑
        String taskId = IdUtil.randomUUID();
        FileAsyncContext fileAsyncContext = new FileAsyncContext(taskId, responseWrapper, joinPoint);

        // 提交异步任务
        CompletableFuture.runAsync(() -> handleFileAsync(fileAsyncContext), executor)
                .exceptionally(throwable -> {
                    log.error("异步任务提交失败: taskId={}", taskId, throwable);
                    asyncTaskManager.failTask(taskId, "任务提交失败: " + throwable.getMessage());
                    return null;
                });

        log.info("异步文件导出任务已提交，任务ID: {}, 方法: {}", taskId, joinPoint.getSignature().getName());

        // 立即响应客户端任务ID
        writeSuccessResponse(taskId);
        return null;
    }

    /**
     * 异步处理文件导出任务的核心逻辑。
     * 包括创建任务、调用业务方法获取数据、生成文件以及更新任务状态等操作。
     *
     * @param context 包含任务信息的异步请求对象
     */
    private void handleFileAsync(FileAsyncContext context) {
        String taskId = context.taskId();
        FileResponseWrapper<?> wrapper = context.responseWrapper();
        ProceedingJoinPoint joinPoint = context.joinPoint();

        try {
            // 1. 初始化任务
            initializeTask(taskId, wrapper, joinPoint);

            // 2. 执行业务逻辑获取数据
            Object data = executeBusinessLogic(taskId, joinPoint);

            // 3. 生成文件
            FileInfo fileInfo = generateFile(taskId, wrapper, data);

            // 4. 完成任务
            completeTask(taskId, fileInfo);

        } catch (Throwable e) {
            // 处理异步任务执行过程中的异常
            handleAsyncException(taskId, e);
        }
    }

    /**
     * 初始化异步任务
     *
     * @param taskId    任务ID
     * @param wrapper   文件响应包装器
     * @param joinPoint 切入点对象
     */
    private void initializeTask(String taskId, FileResponseWrapper<?> wrapper, ProceedingJoinPoint joinPoint) {
        // 获取切入点参数并添加文件名
        Map<String, Object> params = AopUtil.getParams(joinPoint);
        params.put("filename", wrapper.filename());

        // 创建异步任务并更新任务状态和进度
        asyncTaskManager.createTask(taskId, "file_export", wrapper.code(), wrapper.name(), wrapper.description(), params);
        asyncTaskManager.updateTaskStatus(taskId, AsyncTaskStatus.PROCESSING);
        asyncTaskManager.updateTaskProgress(taskId, PROGRESS_TASK_CREATED);

        log.debug("异步任务已初始化: taskId={}, filename={}", taskId, wrapper.filename());
    }

    /**
     * 执行业务逻辑获取数据
     *
     * @param taskId    任务ID，用于标识和更新任务进度
     * @param joinPoint 连接点对象，用于执行目标方法获取业务数据
     * @return 业务逻辑执行返回的数据对象
     * @throws Throwable 业务逻辑执行过程中可能抛出的异常
     */
    private Object executeBusinessLogic(String taskId, ProceedingJoinPoint joinPoint) throws Throwable {
        // 执行目标方法获取业务数据
        Object data = joinPoint.proceed();
        // 更新任务进度为数据已获取状态
        asyncTaskManager.updateTaskProgress(taskId, PROGRESS_DATA_RETRIEVED);
        log.debug("业务数据已获取: taskId={}", taskId);
        return data;
    }

    /**
     * 生成文件
     *
     * @param taskId  任务ID，用于跟踪和更新任务进度
     * @param wrapper 文件响应包装器，包含文件类型、文件名和响应信息
     * @param data    文件生成所需的数据对象
     * @return 生成文件的完整路径
     * @throws Exception 文件生成过程中可能抛出的异常
     */
    private FileInfo generateFile(String taskId, FileResponseWrapper<?> wrapper, Object data) throws Exception {
        // 获取文件类型和文件名，生成文件路径
        FileTypeEnums type = wrapper.fileType();
        String filename = wrapper.filename();
        FileInfo fileInfo = fileManager.generateFileInfo(type, filename);
        asyncTaskManager.updateTaskProgress(taskId, PROGRESS_FILE_PATH_PREPARED, fileInfo.getFileId());

        // 查找支持的文件生成器并执行文件生成

        try (OutputStream outputStream = fileManager.getOutputStream(fileInfo.getFileId())) {
            fileGeneratorManager.generate(data, wrapper, outputStream);
            asyncTaskManager.updateTaskProgress(taskId, PROGRESS_FILE_GENERATED);
            log.debug("文件已生成: taskId={}, fileId={}", taskId, fileInfo.getFileId());
        } catch (Exception e) {
            log.error("文件生成失败: taskId={}, fileId={}", taskId, fileInfo.getFileId(), e);
            asyncTaskManager.failTask(taskId, "文件生成失败: " + e.getMessage());
        }

        // 验证生成的文件并返回文件路径
        fileManager.validateGeneratedFile(fileInfo.getFileId());
        return fileInfo;
    }

    /**
     * 完成任务
     *
     * @param taskId   任务ID
     * @param fileInfo 生成的文件信息
     */
    private void completeTask(String taskId, FileInfo fileInfo) {
        // 通知任务管理器任务完成并更新任务进度
        asyncTaskManager.completeTask(taskId, Map.of("fileInfo", fileInfo));
        asyncTaskManager.updateTaskProgress(taskId, PROGRESS_COMPLETED);
        log.info("异步文件导出完成: taskId={}, fileInfo={}", taskId, fileInfo);
    }

    /**
     * 处理异步执行过程中的异常
     * <p>
     * 当异步任务执行过程中发生异常时，该方法负责记录错误日志并更新任务状态为失败状态。
     *
     * @param taskId 异步任务的唯一标识符
     * @param e      异常对象，包含异常信息和堆栈跟踪
     */
    private void handleAsyncException(String taskId, Throwable e) {
        // 提取异常信息用于任务状态更新
        String errorMessage = e.getMessage();
        if (errorMessage == null || errorMessage.isEmpty()) {
            errorMessage = e.getClass().getSimpleName();
        }
        // 记录详细的错误日志，包含任务ID和异常信息
        log.error("异步文件导出失败: taskId={}, error={}", taskId, errorMessage, e);
        // 更新任务管理器中的任务状态为失败，并保存错误信息
        asyncTaskManager.failTask(taskId, errorMessage);
    }

    /**
     * 写入成功响应
     *
     * @param taskId 任务ID，用于标识已提交的任务
     */
    private void writeSuccessResponse(String taskId) {
        // 构造成功响应结果，包含提示信息和任务ID
        Result<String> result = Result.success("任务已提交，请稍后查看", taskId);
        // 将结果以JSON格式写入响应
        WebUtil.writeJson(result);
    }

}
