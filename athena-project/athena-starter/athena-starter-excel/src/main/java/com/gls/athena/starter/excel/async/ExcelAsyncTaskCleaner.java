package com.gls.athena.starter.excel.async;

import com.gls.athena.starter.excel.config.ExcelProperties;
import com.gls.athena.starter.excel.web.domain.ExcelAsyncTask;
import com.gls.athena.starter.excel.web.domain.TaskStatus;
import com.gls.athena.starter.excel.web.service.ExcelFileService;
import com.gls.athena.starter.excel.web.service.ExcelTaskService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Excel异步任务清理器
 * 定期清理过期的任务和文件
 *
 * @author george
 */
@Slf4j
@Component
public class ExcelAsyncTaskCleaner {

    @Resource
    private ExcelTaskService taskService;
    @Resource
    private ExcelFileService fileService;
    @Resource
    private ExcelProperties excelProperties;

    /**
     * 定时清理过期任务和文件
     * 每小时执行一次
     */
    @Scheduled(fixedRateString = "#{${athena.excel.taskCleanupIntervalMinutes:60} * 60 * 1000}")
    public void cleanupExpiredTasks() {
        log.info("开始清理过期的Excel异步任务");

        int timeoutMinutes = excelProperties.getAsyncTimeoutMinutes();
        int retentionDays = excelProperties.getFileRetentionDays();

        // 处理超时任务
        List<ExcelAsyncTask> expiredTasks = taskService.getExpiredTasks(timeoutMinutes);
        if (!expiredTasks.isEmpty()) {
            List<String> expiredTaskIds = expiredTasks.stream()
                    .map(ExcelAsyncTask::getTaskId)
                    .collect(Collectors.toList());

            taskService.batchUpdateTaskStatus(expiredTaskIds, TaskStatus.FAILED);

            // 更新错误信息
            expiredTasks.forEach(task ->
                    taskService.failTask(task.getTaskId(), "任务处理超时"));

            log.warn("处理超时任务数量: {}", expiredTasks.size());
        }

        // 清理过期任务和文件
        List<ExcelAsyncTask> tasksToCleanup = taskService.getTasksToCleanup(retentionDays);

        int cleanedTaskCount = 0;
        int cleanedFileCount = 0;

        for (ExcelAsyncTask task : tasksToCleanup) {
            // 删除文件
            if (task.getFilePath() != null) {
                boolean deleted = fileService.deleteFile(task.getFilePath());
                if (deleted) {
                    cleanedFileCount++;
                    log.info("清理过期文件: {}", task.getFilePath());
                } else {
                    log.warn("清理文件失败: {}", task.getFilePath());
                }
            }

            // 删除任务
            taskService.removeTask(task.getTaskId());
            cleanedTaskCount++;
            log.info("清理过期任务: taskId={}, createTime={}",
                    task.getTaskId(), task.getCreateTime());
        }

        log.info("清理完成，清理任务数: {}, 清理文件数: {}", cleanedTaskCount, cleanedFileCount);
    }
}
