package com.gls.athena.starter.file.task;

import com.gls.athena.starter.file.config.FileProperties;
import com.gls.athena.starter.file.manager.IFileStorageManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * 文件清理定时任务
 * 定期清理过期的临时文件，释放存储空间
 *
 * @author george
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "athena.file.cleanup", name = "enabled", havingValue = "true")
public class FileCleanupTask {

    /**
     * 文件保留天数，默认7天
     */
    private static final int DEFAULT_RETENTION_DAYS = 7;
    private final FileProperties fileProperties;
    private final IFileStorageManager fileStorageManager;

    /**
     * 定时清理过期文件
     * 每天凌晨2点执行
     */
    @Scheduled(cron = "${athena.file.cleanup.cron:0 0 2 * * ?}")
    public void cleanupExpiredFiles() {
        log.info("开始执行文件清理任务");

        String basePath = fileProperties.getPath();
        int retentionDays = getRetentionDays();
        Instant cutoffTime = Instant.now().minus(retentionDays, ChronoUnit.DAYS);

        AtomicInteger deletedCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        try {
            Path baseDir = Paths.get(basePath);
            if (!Files.exists(baseDir)) {
                log.warn("文件基础路径不存在: {}", basePath);
                return;
            }

            try (Stream<Path> paths = Files.walk(baseDir)) {
                paths.filter(Files::isRegularFile)
                        .forEach(path -> {
                            try {
                                BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
                                Instant lastModified = attrs.lastModifiedTime().toInstant();

                                if (lastModified.isBefore(cutoffTime)) {
                                    fileStorageManager.deleteFile(path.toString());
                                    deletedCount.incrementAndGet();
                                    log.debug("删除过期文件: {}", path);
                                }
                            } catch (Exception e) {
                                errorCount.incrementAndGet();
                                log.error("删除文件失败: {}", path, e);
                            }
                        });
            }

            log.info("文件清理任务完成 - 删除文件数: {}, 失败数: {}", deletedCount.get(), errorCount.get());

            // 清理空目录
            cleanupEmptyDirectories(baseDir);

        } catch (Exception e) {
            log.error("文件清理任务执行失败", e);
        }
    }

    /**
     * 清理空目录
     *
     * @param baseDir 基础目录
     */
    private void cleanupEmptyDirectories(Path baseDir) {
        try (Stream<Path> paths = Files.walk(baseDir)) {
            paths.filter(Files::isDirectory)
                    .filter(path -> !path.equals(baseDir))
                    .sorted((p1, p2) -> p2.getNameCount() - p1.getNameCount()) // 从深层目录开始
                    .forEach(path -> {
                        try {
                            File dir = path.toFile();
                            String[] children = dir.list();
                            if (children != null && children.length == 0) {
                                Files.delete(path);
                                log.debug("删除空目录: {}", path);
                            }
                        } catch (Exception e) {
                            log.error("删除空目录失败: {}", path, e);
                        }
                    });
        } catch (Exception e) {
            log.error("清理空目录失败", e);
        }
    }

    /**
     * 获取文件保留天数配置
     *
     * @return 保留天数
     */
    private int getRetentionDays() {
        // 可以从配置中读取，这里使用默认值
        return DEFAULT_RETENTION_DAYS;
    }
}
