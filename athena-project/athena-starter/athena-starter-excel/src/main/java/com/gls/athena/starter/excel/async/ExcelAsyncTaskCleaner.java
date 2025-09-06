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
    private ExcelTaskService excelTaskService;
    @Resource
    private ExcelFileService excelFileService;
    @Resource
    private ExcelProperties excelProperties;

    /**
     * 定时清理过期任务和文件
     * 每小时执行一次
     * <p>
     * 该方法会：
     * 1. 查找并处理超时未完成的任务，将其状态更新为失败；
     * 2. 查找并删除已过保留期的任务记录及其关联的文件。
     * </p>
     */
    @Scheduled(fixedRateString = "#{${athena.excel.taskCleanupIntervalMinutes:60} * 60 * 1000}")
    public void cleanupExpiredTasks() {
        log.info("开始清理过期的Excel异步任务");

        int timeoutMinutes = excelProperties.getAsyncTimeoutMinutes();
        int retentionDays = excelProperties.getFileRetentionDays();

        // 处理超时任务：将超时未完成的任务标记为失败
        List<ExcelAsyncTask> expiredTasks = excelTaskService.getExpiredTasks(timeoutMinutes);
        if (!expiredTasks.isEmpty()) {
            List<String> expiredTaskIds = expiredTasks.stream()
                    .map(ExcelAsyncTask::getTaskId)
                    .collect(Collectors.toList());

            excelTaskService.batchUpdateTaskStatus(expiredTaskIds, TaskStatus.FAILED);

            // 更新每个任务的错误信息
            expiredTasks.forEach(task ->
                    excelTaskService.failTask(task.getTaskId(), "任务处理超时"));

            log.warn("处理超时任务数量: {}", expiredTasks.size());
        }

        // 清理过期任务和文件：删除超过保留天数的任务及对应文件
        List<ExcelAsyncTask> tasksToCleanup = excelTaskService.getTasksToCleanup(retentionDays);

        int cleanedTaskCount = 0;
        int cleanedFileCount = 0;

        for (ExcelAsyncTask task : tasksToCleanup) {
            // 删除任务对应的文件（如果存在）
            if (task.getFilePath() != null) {
                boolean deleted = excelFileService.deleteFile(task.getFilePath());
                if (deleted) {
                    cleanedFileCount++;
                    log.info("清理过期文件: {}", task.getFilePath());
                } else {
                    log.warn("清理文件失败: {}", task.getFilePath());
                }
            }

            // 删除任务记录
            excelTaskService.removeTask(task.getTaskId());
            cleanedTaskCount++;
            log.info("清理过期任务: taskId={}, createTime={}",
                    task.getTaskId(), task.getCreateTime());
        }

        log.info("清理完成，清理任务数: {}, 清理文件数: {}", cleanedTaskCount, cleanedFileCount);
    }
}
