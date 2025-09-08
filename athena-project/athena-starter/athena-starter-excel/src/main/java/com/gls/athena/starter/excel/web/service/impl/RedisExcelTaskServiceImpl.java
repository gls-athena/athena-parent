package com.gls.athena.starter.excel.web.service.impl;

import com.gls.athena.starter.data.redis.support.RedisUtil;
import com.gls.athena.starter.excel.web.domain.ExcelAsyncTask;
import com.gls.athena.starter.excel.web.domain.TaskStatus;
import com.gls.athena.starter.excel.web.service.ExcelTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 基于Redis实现的Excel异步任务服务类。
 * 提供任务的创建、查询、更新状态、进度、完成、失败、删除等操作。
 *
 * @author lizy19
 */
@Slf4j
@Service
@ConditionalOnClass(RedisUtil.class)
public class RedisExcelTaskServiceImpl implements ExcelTaskService {

    private static final String KEY_PREFIX = "excel-task";

    /**
     * 创建一个新的Excel异步任务，并将其保存到Redis中。
     *
     * @param taskId      任务ID，唯一标识一个任务
     * @param filename    文件名
     * @param description 任务描述信息
     * @return 创建的Excel异步任务对象
     */
    @Override
    public ExcelAsyncTask createTask(String taskId, String filename, String description) {
        // 初始化Excel异步任务对象，设置任务基本信息和初始状态
        ExcelAsyncTask task = new ExcelAsyncTask()
                .setTaskId(taskId)
                .setFilename(filename)
                .setDescription(description)
                .setStatus(TaskStatus.WAITING)
                .setCreateTime(LocalDateTime.now())
                .setProgress(0);
        RedisUtil.setCacheTableRow(KEY_PREFIX, taskId, task);
        return task;
    }

    /**
     * 根据任务ID获取Excel异步任务信息。
     *
     * @param taskId 任务ID
     * @return Excel异步任务对象，如果不存在则返回null
     */
    @Override
    public ExcelAsyncTask getTask(String taskId) {
        return RedisUtil.getCacheTableRow(KEY_PREFIX, taskId, ExcelAsyncTask.class);
    }

    /**
     * 更新指定任务的状态。
     *
     * @param taskId 任务ID
     * @param status 新的任务状态
     */
    @Override
    public void updateTaskStatus(String taskId, TaskStatus status) {
        ExcelAsyncTask task = getTask(taskId);
        if (task != null) {
            task.setStatus(status);
            RedisUtil.setCacheTableRow(KEY_PREFIX, taskId, task);
        }
    }

    /**
     * 更新指定任务的进度。
     *
     * @param taskId   任务ID
     * @param progress 新的进度值（0-100）
     */
    @Override
    public void updateTaskProgress(String taskId, Integer progress) {
        ExcelAsyncTask task = getTask(taskId);
        if (task != null) {
            task.setProgress(progress);
            RedisUtil.setCacheTableRow(KEY_PREFIX, taskId, task);
        }

    }

    /**
     * 标记任务为已完成，并设置文件路径。
     *
     * @param taskId   任务ID
     * @param filePath 生成的Excel文件路径
     */
    @Override
    public void completeTask(String taskId, String filePath) {
        ExcelAsyncTask task = getTask(taskId);
        if (task != null) {
            task.setStatus(TaskStatus.COMPLETED);
            task.setFilePath(filePath);
            task.setProgress(100);
            RedisUtil.setCacheTableRow(KEY_PREFIX, taskId, task);
        }

    }

    /**
     * 标记任务为失败，并记录错误信息。
     *
     * @param taskId       任务ID
     * @param errorMessage 错误信息
     */
    @Override
    public void failTask(String taskId, String errorMessage) {
        ExcelAsyncTask task = getTask(taskId);
        if (task != null) {
            task.setStatus(TaskStatus.FAILED);
            task.setErrorMessage(errorMessage);
            RedisUtil.setCacheTableRow(KEY_PREFIX, taskId, task);
        }

    }

    /**
     * 删除指定的任务。
     *
     * @param taskId 任务ID
     */
    @Override
    public void removeTask(String taskId) {
        RedisUtil.deleteCacheTableRow(KEY_PREFIX, taskId);
    }

    /**
     * 获取所有Excel异步任务。
     *
     * @return 包含所有任务的Map，key为任务ID，value为任务对象
     */
    @Override
    public Map<String, ExcelAsyncTask> getAllTasks() {
        return RedisUtil.getCacheTable(KEY_PREFIX, ExcelAsyncTask.class);
    }

    /**
     * 根据任务状态筛选任务列表。
     *
     * @param status 指定的任务状态
     * @return 符合状态条件的任务列表
     */
    @Override
    public List<ExcelAsyncTask> getTasksByStatus(TaskStatus status) {
        List<ExcelAsyncTask> allTasks = RedisUtil.getCacheTableValues(KEY_PREFIX, ExcelAsyncTask.class);
        return allTasks.stream()
                .filter(task -> task.getStatus() == status)
                .toList();
    }

    /**
     * 获取超时未完成的任务列表。
     *
     * @param timeoutMinutes 超时分钟数
     * @return 超时且状态为PROCESSING的任务列表
     */
    @Override
    public List<ExcelAsyncTask> getExpiredTasks(int timeoutMinutes) {
        List<ExcelAsyncTask> allTasks = RedisUtil.getCacheTableValues(KEY_PREFIX, ExcelAsyncTask.class);
        // 计算超时时间阈值
        LocalDateTime timeoutThreshold = LocalDateTime.now().minusMinutes(timeoutMinutes);
        return allTasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.PROCESSING)
                .filter(task -> task.getStartTime() != null && task.getStartTime().isBefore(timeoutThreshold))
                .toList();
    }

    /**
     * 获取需要清理的过期任务列表。
     *
     * @param retentionDays 保留天数，早于该天数创建的任务将被清理
     * @return 需要清理的任务列表
     */
    @Override
    public List<ExcelAsyncTask> getTasksToCleanup(int retentionDays) {
        List<ExcelAsyncTask> allTasks = RedisUtil.getCacheTableValues(KEY_PREFIX, ExcelAsyncTask.class);
        // 计算清理阈值时间，早于该时间创建的任务需要被清理
        LocalDateTime cleanupThreshold = LocalDateTime.now().minusDays(retentionDays);
        // 筛选出创建时间早于清理阈值的任务
        return allTasks.stream()
                .filter(task -> task.getCreateTime().isBefore(cleanupThreshold))
                .collect(Collectors.toList());
    }

}
