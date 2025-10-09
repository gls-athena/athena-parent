package com.gls.athena.starter.file.support;

import com.gls.athena.starter.async.domain.AsyncTask;
import com.gls.athena.starter.async.manager.IAsyncTaskManager;
import com.gls.athena.starter.file.config.FileProperties;
import com.gls.athena.starter.file.manager.FileManager;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 文件异步清理任务类
 * <p>
 * 该类用于定期清理过期的异步任务及其对应的文件。根据配置的保留天数，
 * 清理 startTime 早于当前时间减去保留天数的任务和文件。
 * </p>
 *
 * @author george
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "athena.file.cleanup", name = "enabled", havingValue = "true")
public class FileAsyncCleanup {
    @Resource
    private FileProperties fileProperties;
    @Resource
    private IAsyncTaskManager asyncTaskManager;
    @Resource
    private FileManager fileManager;

    /**
     * 定时执行文件清理任务，根据配置的保留天数清理过期的异步任务和对应的文件
     * 该方法通过cron表达式定时触发，清理startTime早于保留天数的异步任务
     * 清理过程先删除文件再删除任务记录，保证数据一致性
     */
    @Scheduled(cron = "${athena.file.cleanup.cron:0 0 2 * * ?}")
    public void cleanup() {
        log.info("执行文件异步清理任务");

        // 验证配置值
        int retentionDays = fileProperties.getCleanup().getRetentionDays();
        if (retentionDays <= 0) {
            log.warn("文件保留天数配置无效: {}", retentionDays);
            return;
        }

        // 使用安全的时间计算方式
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -retentionDays);
        Date cutoffDate = calendar.getTime();

        List<AsyncTask> tasks = asyncTaskManager.getAllTasksBeforeStartTime(cutoffDate);
        log.info("找到 {} 个需要清理的任务", tasks.size());

        int successCount = 0;
        int failureCount = 0;

        // 遍历清理过期任务，先删除文件再删除任务记录
        for (AsyncTask task : tasks) {
            if (task.getFileId() != null) {
                try {
                    // 先删除文件，再删除任务记录（保证数据一致性）
                    fileManager.deleteFile(task.getFileId());
                    asyncTaskManager.deleteTask(task.getTaskId());
                    successCount++;
                } catch (Exception e) {
                    failureCount++;
                    log.error("清理任务失败，taskId: {}, fileId: {}", task.getTaskId(), task.getFileId(), e);
                    // 继续处理下一个任务，不中断整个清理过程
                }
            }
        }

        log.info("文件清理任务完成，成功: {} 个，失败: {} 个", successCount, failureCount);
    }

}
