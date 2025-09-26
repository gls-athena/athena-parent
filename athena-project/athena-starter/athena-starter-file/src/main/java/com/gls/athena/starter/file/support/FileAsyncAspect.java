package com.gls.athena.starter.file.support;

import cn.hutool.core.util.IdUtil;
import com.gls.athena.common.bean.result.Result;
import com.gls.athena.starter.async.domain.AsyncTaskStatus;
import com.gls.athena.starter.async.manager.IAsyncTaskManager;
import com.gls.athena.starter.async.util.AopUtil;
import com.gls.athena.starter.file.generator.FileGenerator;
import com.gls.athena.starter.file.manager.IFileManager;
import com.gls.athena.starter.web.util.WebUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;

import java.io.IOException;
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
 * @author george
 */
@Slf4j
@RequiredArgsConstructor
public class FileAsyncAspect<Generator extends FileGenerator<Response>, Response extends Annotation> {

    /**
     * 进度百分比
     */
    private static final int PROGRESS_TASK_CREATED = 20;
    private static final int PROGRESS_DATA_RETRIEVED = 40;
    private static final int PROGRESS_FILE_PATH_PREPARED = 60;
    private static final int PROGRESS_FILE_GENERATED = 80;
    private static final int PROGRESS_COMPLETED = 100;

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
        // 早期返回：如果响应对象为空，直接执行原方法
        if (response == null) {
            return joinPoint.proceed();
        }

        FileResponseWrapper<Response> responseWrapper = new FileResponseWrapper<>(response);
        // 早期返回：如果不是异步响应，直接执行原方法
        if (!responseWrapper.isAsync()) {
            return joinPoint.proceed();
        }

        // 异步处理逻辑
        String taskId = IdUtil.randomUUID();
        FileAsyncContext<Response> fileAsyncContext = new FileAsyncContext<>(taskId, responseWrapper, joinPoint);

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
    private void handleFileAsync(FileAsyncContext<Response> context) {
        String taskId = context.getTaskId();
        FileResponseWrapper<Response> wrapper = context.getResponseWrapper();
        ProceedingJoinPoint joinPoint = context.getJoinPoint();

        try {
            // 1. 初始化任务
            initializeTask(taskId, wrapper, joinPoint);

            // 2. 执行业务逻辑获取数据
            Object data = executeBusinessLogic(taskId, joinPoint);

            // 3. 生成文件
            String filePath = generateFile(taskId, wrapper, data);

            // 4. 完成任务
            completeTask(taskId, filePath);

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
    private void initializeTask(String taskId, FileResponseWrapper<Response> wrapper, ProceedingJoinPoint joinPoint) {
        // 获取切入点参数并添加文件名
        Map<String, Object> params = AopUtil.getParams(joinPoint);
        params.put("filename", wrapper.getFilename());

        // 创建异步任务并更新任务状态和进度
        asyncTaskManager.createTask(taskId, wrapper.getCode(), wrapper.getName(), wrapper.getDescription(), params);
        asyncTaskManager.updateTaskStatus(taskId, AsyncTaskStatus.PROCESSING);
        asyncTaskManager.updateTaskProgress(taskId, PROGRESS_TASK_CREATED);
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
    private String generateFile(String taskId, FileResponseWrapper<Response> wrapper, Object data) throws Exception {
        // 获取文件类型和文件名，生成文件路径
        String type = wrapper.getFileType().getCode();
        String filename = wrapper.getFilename();
        String filePath = fileManager.generateFilePath(type, filename);
        asyncTaskManager.updateTaskProgress(taskId, PROGRESS_FILE_PATH_PREPARED);

        // 查找支持的文件生成器并执行文件生成
        Generator generator = findSupportedGenerator(wrapper);

        try (OutputStream outputStream = fileManager.getFileOutputStream(filePath)) {
            generator.generate(data, wrapper.getResponse(), outputStream);
            asyncTaskManager.updateTaskProgress(taskId, PROGRESS_FILE_GENERATED);
        }

        // 验证生成的文件并返回文件路径
        validateGeneratedFile(filePath);
        return filePath;
    }

    /**
     * 查找支持的文件生成器
     *
     * @param wrapper 响应对象，用于判断支持的生成器类型
     * @return 支持该响应的生成器实例
     * @throws IllegalArgumentException 当找不到支持的生成器时抛出异常
     */
    private Generator findSupportedGenerator(FileResponseWrapper<Response> wrapper) {
        // 从生成器列表中查找第一个支持该响应的生成器，如果找不到则抛出异常
        return generators.stream()
                .filter(generator -> wrapper.isSupport(generator) || generator.supports(wrapper.getResponse()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持的文件类型，无可用的生成器"));
    }

    /**
     * 验证生成的文件
     *
     * @param filePath 文件路径
     * @throws IOException 当文件不存在或文件为空时抛出异常
     */
    private void validateGeneratedFile(String filePath) throws IOException {
        // 验证文件是否存在
        if (!fileManager.exists(filePath)) {
            throw new IOException("文件生成失败：文件不存在");
        }
        // 验证文件是否为空
        if (fileManager.getFileSize(filePath) <= 0) {
            throw new IOException("文件生成失败：文件为空");
        }
    }

    /**
     * 完成任务
     *
     * @param taskId   任务ID
     * @param filePath 文件路径
     */
    private void completeTask(String taskId, String filePath) {
        // 通知任务管理器任务完成并更新任务进度
        asyncTaskManager.completeTask(taskId, Map.of("filePath", filePath));
        asyncTaskManager.updateTaskProgress(taskId, PROGRESS_COMPLETED);
        log.info("异步文件导出完成: taskId={}, filePath={}", taskId, filePath);
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
