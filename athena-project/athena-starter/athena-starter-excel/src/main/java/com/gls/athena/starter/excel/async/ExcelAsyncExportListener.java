package com.gls.athena.starter.excel.async;

import com.gls.athena.starter.excel.generator.ExcelGeneratorManager;
import com.gls.athena.starter.excel.web.domain.ExcelAsyncRequest;
import com.gls.athena.starter.excel.web.domain.ExcelAsyncTask;
import com.gls.athena.starter.excel.web.domain.FileOutputWrapper;
import com.gls.athena.starter.excel.web.domain.TaskStatus;
import com.gls.athena.starter.excel.web.service.ExcelFileService;
import com.gls.athena.starter.excel.web.service.ExcelTaskService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.OutputStream;

/**
 * Excel异步导出事件监听器
 *
 * @author george
 */
@Slf4j
@Component
public class ExcelAsyncExportListener {

    @Resource
    private ExcelGeneratorManager excelGeneratorManager;

    @Resource
    private ExcelTaskService excelTaskService;

    @Resource
    private ExcelFileService excelFileService;

    /**
     * 处理异步Excel导出请求
     *
     * @param request 异步导出请求，包含任务ID、数据、Excel响应配置等信息
     */
    @Async("excelAsyncExecutor")
    @EventListener
    public void handleAsyncExport(ExcelAsyncRequest request) {
        String taskId = request.getTaskId();
        String filename = request.getExcelResponse().filename() + request.getExcelResponse().excelType().getValue();

        // 创建任务并更新状态为处理中
        ExcelAsyncTask task = excelTaskService.createTask(taskId, filename, "Excel异步导出");
        excelTaskService.updateTaskStatus(taskId, TaskStatus.PROCESSING);

        try {
            // 更新进度
            excelTaskService.updateTaskProgress(taskId, 20);

            // 获取文件输出流
            FileOutputWrapper outputWrapper = excelFileService.getFileOutputStream(filename);
            String filePath = outputWrapper.getFilePath();

            excelTaskService.updateTaskProgress(taskId, 50);

            // 生成Excel文件
            try (OutputStream outputStream = outputWrapper.getOutputStream()) {
                excelGeneratorManager.generate(request.getData(), request.getExcelResponse(), outputStream);
                excelTaskService.updateTaskProgress(taskId, 90);
            }

            // 验证文件是否生成成功
            if (excelFileService.fileExists(filePath) && excelFileService.getFileSize(filePath) > 0) {
                excelTaskService.completeTask(taskId, filePath);
                log.info("异步Excel导出完成: taskId={}, filePath={}", taskId, filePath);
            } else {
                excelTaskService.failTask(taskId, "文件生成失败或文件为空");
            }

        } catch (Exception e) {
            log.error("异步Excel导出失败: taskId={}", taskId, e);
            excelTaskService.failTask(taskId, e.getMessage());
        }
    }

}
