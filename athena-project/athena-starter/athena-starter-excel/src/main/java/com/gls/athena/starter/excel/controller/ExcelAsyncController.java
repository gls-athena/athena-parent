package com.gls.athena.starter.excel.controller;

import com.gls.athena.common.bean.result.Result;
import com.gls.athena.starter.excel.service.ExcelFileService;
import com.gls.athena.starter.excel.service.ExcelTaskService;
import com.gls.athena.starter.excel.support.ExcelAsyncTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
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
@RequiredArgsConstructor
public class ExcelAsyncController {

    private final ExcelTaskService taskService;
    private final ExcelFileService fileService;

    /**
     * 查询任务状态
     *
     * @param taskId 任务ID
     * @return 任务状态信息
     */
    @GetMapping("/task/{taskId}")
    public Result<ExcelAsyncTask> getTaskStatus(@PathVariable String taskId) {
        ExcelAsyncTask task = taskService.getTask(taskId);
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
        return Result.success(taskService.getAllTasks());
    }

    /**
     * 下载导出的文件
     *
     * @param taskId 任务ID
     * @return 文件下载响应
     */
    @GetMapping("/download/{taskId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String taskId) {
        ExcelAsyncTask task = taskService.getTask(taskId);

        if (task == null) {
            return ResponseEntity.notFound().build();
        }

        if (task.getStatus() != ExcelAsyncTask.TaskStatus.COMPLETED) {
            return ResponseEntity.badRequest().build();
        }

        String filePath = task.getFilePath();
        if (filePath == null || filePath.trim().isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (!fileService.fileExists(filePath)) {
            log.warn("文件不存在: {}", filePath);
            return ResponseEntity.notFound().build();
        }

        try {
            InputStream inputStream = fileService.getFileInputStream(filePath);
            Resource resource = new InputStreamResource(inputStream);
            String encodedFilename = URLEncoder.encode(task.getFilename(), StandardCharsets.UTF_8);
            long fileSize = fileService.getFileSize(filePath);

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
        ExcelAsyncTask task = taskService.getTask(taskId);
        if (task == null) {
            return Result.error("任务不存在");
        }

        if (task.getStatus() == ExcelAsyncTask.TaskStatus.COMPLETED ||
                task.getStatus() == ExcelAsyncTask.TaskStatus.FAILED ||
                task.getStatus() == ExcelAsyncTask.TaskStatus.CANCELLED) {
            return Result.error("任务已完成，无法取消");
        }

        taskService.updateTaskStatus(taskId, ExcelAsyncTask.TaskStatus.CANCELLED);
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
        ExcelAsyncTask task = taskService.getTask(taskId);
        if (task == null) {
            return Result.error("任务不存在");
        }

        // 删除文件（如果存在）
        if (task.getFilePath() != null) {
            boolean deleted = fileService.deleteFile(task.getFilePath());
            if (!deleted) {
                log.warn("删除文件失败: {}", task.getFilePath());
            }
        }

        taskService.removeTask(taskId);
        return Result.success("任务已删除");
    }
}
