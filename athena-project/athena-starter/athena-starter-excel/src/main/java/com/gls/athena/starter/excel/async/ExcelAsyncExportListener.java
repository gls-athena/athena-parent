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
    private ExcelGeneratorManager generatorManager;

    @Resource
    private ExcelTaskService taskService;

    @Resource
    private ExcelFileService fileService;

    /**
     * 处理异步Excel导出请求
     *
     * @param request 异步导出请求
     */
    @Async("excelAsyncExecutor")
    @EventListener
    public void handleAsyncExport(ExcelAsyncRequest request) {
        String taskId = request.getTaskId();
        String filename = request.getExcelResponse().filename();

        // 创建任务并更新状态为处理中
        ExcelAsyncTask task = taskService.createTask(taskId, filename, "Excel异步导出");
        taskService.updateTaskStatus(taskId, TaskStatus.PROCESSING);

        try {
            // 更新进度
            taskService.updateTaskProgress(taskId, 20);

            // 获取文件输出流
            FileOutputWrapper outputWrapper = fileService.getFileOutputStream(filename);
            String filePath = outputWrapper.getFilePath();

            taskService.updateTaskProgress(taskId, 50);

            // 生成Excel文件
            try (OutputStream outputStream = outputWrapper.getOutputStream()) {
                generatorManager.generate(request.getData(), request.getExcelResponse(), outputStream);
                taskService.updateTaskProgress(taskId, 90);
            }

            // 验证文件是否生成成功
            if (fileService.fileExists(filePath) && fileService.getFileSize(filePath) > 0) {
                taskService.completeTask(taskId, filePath);
                log.info("异步Excel导出完成: taskId={}, filePath={}", taskId, filePath);
            } else {
                taskService.failTask(taskId, "文件生成失败或文件为空");
            }

        } catch (Exception e) {
            log.error("异步Excel导出失败: taskId={}", taskId, e);
            taskService.failTask(taskId, e.getMessage());
        }
    }
}
