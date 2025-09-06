package com.gls.athena.starter.excel.web.controller;

import com.gls.athena.common.bean.result.Result;
import com.gls.athena.starter.excel.web.domain.ExcelAsyncTask;
import com.gls.athena.starter.excel.web.domain.TaskStatus;
import com.gls.athena.starter.excel.web.service.ExcelFileService;
import com.gls.athena.starter.excel.web.service.ExcelTaskService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Excel异步导出任务管理控制器
 *
 * @author george
 */
@Slf4j
@RestController
@RequestMapping("/excel/async")
public class ExcelAsyncController {

    @Resource
    private ExcelTaskService excelTaskService;
    @Resource
    private ExcelFileService excelFileService;

    /**
     * 查询任务状态
     *
     * @param taskId 任务ID
     * @return 任务状态信息
     */
    @GetMapping("/task/{taskId}")
    public Result<ExcelAsyncTask> getTaskStatus(@PathVariable String taskId) {
        ExcelAsyncTask task = excelTaskService.getTask(taskId);
        if (task == null) {
            return Result.error("任务不存在");
        }
        return Result.success(task);
    }

    /**
     * 获取所有任务列表
     *
     * @return 任务列表
     */
    @GetMapping("/tasks")
    public Result<Map<String, ExcelAsyncTask>> getAllTasks() {
        return Result.success(excelTaskService.getAllTasks());
    }

    /**
     * 下载导出的文件
     *
     * @param taskId 任务ID
     * @return 文件下载响应
     */
    @GetMapping("/download/{taskId}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String taskId) {
        // 获取任务信息
        ExcelAsyncTask task = excelTaskService.getTask(taskId);

        if (task == null) {
            return ResponseEntity.notFound().build();
        }

        // 检查任务状态是否为已完成
        if (task.getStatus() != TaskStatus.COMPLETED) {
            return ResponseEntity.badRequest().build();
        }

        String filePath = task.getFilePath();
        if (filePath == null || filePath.trim().isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // 检查文件是否存在
        if (!excelFileService.fileExists(filePath)) {
            log.warn("文件不存在: {}", filePath);
            return ResponseEntity.notFound().build();
        }

        try {
            // 构造文件下载响应
            InputStream inputStream = excelFileService.getFileInputStream(filePath);
            InputStreamResource resource = new InputStreamResource(inputStream);
            String encodedFilename = URLEncoder.encode(task.getFilename(), StandardCharsets.UTF_8);
            long fileSize = excelFileService.getFileSize(filePath);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + encodedFilename + "\"; filename*=UTF-8''" + encodedFilename)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(fileSize)
                    .body(resource);

        } catch (Exception e) {
            log.error("下载文件失败: taskId={}, filePath={}", taskId, filePath, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 取消任务
     *
     * @param taskId 任务ID
     * @return 操作结果
     */
    @PostMapping("/cancel/{taskId}")
    public Result<String> cancelTask(@PathVariable String taskId) {
        // 获取任务信息
        ExcelAsyncTask task = excelTaskService.getTask(taskId);
        if (task == null) {
            return Result.error("任务不存在");
        }

        // 检查任务状态，只有未完成的任务才能取消
        if (task.getStatus() == TaskStatus.COMPLETED ||
                task.getStatus() == TaskStatus.FAILED ||
                task.getStatus() == TaskStatus.CANCELLED) {
            return Result.error("任务已完成，无法取消");
        }

        // 更新任务状态为已取消
        excelTaskService.updateTaskStatus(taskId, TaskStatus.CANCELLED);
        return Result.success("任务已取消");
    }

    /**
     * 删除任务
     *
     * @param taskId 任务ID
     * @return 操作结果
     */
    @DeleteMapping("/task/{taskId}")
    public Result<String> deleteTask(@PathVariable String taskId) {
        ExcelAsyncTask task = excelTaskService.getTask(taskId);
        if (task == null) {
            return Result.error("任务不存在");
        }

        // 删除文件（如果存在）
        if (task.getFilePath() != null) {
            boolean deleted = excelFileService.deleteFile(task.getFilePath());
            if (!deleted) {
                log.warn("删除文件失败: {}", task.getFilePath());
            }
        }

        excelTaskService.removeTask(taskId);
        return Result.success("任务已删除");
    }

}
