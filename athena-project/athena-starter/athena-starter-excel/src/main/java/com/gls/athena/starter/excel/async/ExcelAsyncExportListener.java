package com.gls.athena.starter.excel.async;

import com.gls.athena.common.core.util.AspectUtil;
import com.gls.athena.starter.async.config.AsyncConstants;
import com.gls.athena.starter.async.web.domain.AsyncTaskStatus;
import com.gls.athena.starter.async.web.service.IAsyncTaskInfoService;
import com.gls.athena.starter.excel.generator.ExcelGeneratorManager;
import com.gls.athena.starter.file.web.service.IFileManager;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.util.Map;

/**
 * Excel异步导出事件监听器
 * <p>
 * 监听并处理Excel异步导出请求，负责任务状态管理、文件生成和进度更新。
 *
 * @author george
 */
@Slf4j
@Component
public class ExcelAsyncExportListener {

    @Resource
    private ExcelGeneratorManager excelGeneratorManager;

    @Resource
    private IAsyncTaskInfoService asyncTaskInfoService;

    @Resource
    private IFileManager fileManager;

    /**
     * 处理异步Excel导出请求
     * <p>
     * 接收一个异步导出请求，创建任务记录，并执行Excel文件的生成流程。包括：
     * - 更新任务状态为处理中
     * - 分阶段更新任务进度
     * - 生成Excel文件到指定输出流
     * - 验证文件是否生成成功并更新最终状态
     *
     * @param request 异步导出请求对象，包含任务ID、数据源、Excel响应配置等信息
     */
    @Async(AsyncConstants.DEFAULT_THREAD_POOL_NAME)
    @EventListener
    public void handleAsyncExport(ExcelAsyncRequest request) {
        String taskId = request.getTaskId();
        ProceedingJoinPoint joinPoint = request.getJoinPoint();
        String filename = request.getExcelResponse().filename() + request.getExcelResponse().excelType().getValue();
        Map<String, Object> params = AspectUtil.getParams(joinPoint);
        params.put("filename", filename);
        // 创建任务并更新状态为处理中
        asyncTaskInfoService.createTask(taskId, "EXCEL_EXPORT", "Excel导出任务", "异步生成Excel文件", params);
        asyncTaskInfoService.updateTaskStatus(taskId, AsyncTaskStatus.PROCESSING);

        try {
            // 更新进度至20%
            asyncTaskInfoService.updateTaskProgress(taskId, 20);

            // 执行被拦截的方法获取数据
            Object data = joinPoint.proceed();
            asyncTaskInfoService.updateTaskProgress(taskId, 40);

            // 获取文件输出流
            String filePath = fileManager.generateFilePath("excel", filename);
            OutputStream outputWrapper = fileManager.getFileOutputStream(filePath);
            asyncTaskInfoService.updateTaskProgress(taskId, 60);

            // 生成Excel文件
            try (OutputStream outputStream = outputWrapper) {
                excelGeneratorManager.generate(data, request.getExcelResponse(), outputStream);
                asyncTaskInfoService.updateTaskProgress(taskId, 80);
            }

            // 验证文件是否生成成功
            if (fileManager.exists(filePath) && fileManager.getFileSize(filePath) > 0) {
                asyncTaskInfoService.completeTask(taskId, Map.of("filePath", filePath));
                log.info("异步Excel导出完成: taskId={}, filePath={}", taskId, filePath);
            } else {
                asyncTaskInfoService.failTask(taskId, "文件生成失败或文件为空");
            }
        } catch (Throwable e) {
            log.error("异步Excel导出失败: taskId={}", taskId, e);
            asyncTaskInfoService.failTask(taskId, e.getMessage());
        }
    }

}
