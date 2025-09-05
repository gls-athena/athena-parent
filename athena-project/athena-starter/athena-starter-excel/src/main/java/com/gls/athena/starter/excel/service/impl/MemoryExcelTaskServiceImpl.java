package com.gls.athena.starter.excel.service.impl;

import com.gls.athena.starter.excel.service.ExcelTaskService;
import com.gls.athena.starter.excel.support.ExcelAsyncTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * 基于内存的Excel异步任务管理服务实现
 *
 * @author george
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "athena.excel.task-storage.type", havingValue = "memory", matchIfMissing = true)
public class MemoryExcelTaskServiceImpl implements ExcelTaskService {

    /**
     * 任务存储映射
     */
    private final ConcurrentMap<String, ExcelAsyncTask> taskMap = new ConcurrentHashMap<>();

    /**
     * 创建异步Excel导出任务
     *
     * @param taskId      任务ID，用于唯一标识一个导出任务
     * @param filename    导出的Excel文件名
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
                .setStatus(ExcelAsyncTask.TaskStatus.WAITING)
                .setCreateTime(LocalDateTime.now())
                .setProgress(0);

        // 将任务存储到任务映射表中并记录日志
        taskMap.put(taskId, task);
        log.info("创建异步Excel导出任务: {}, 文件名: {}", taskId, filename);
        return task;
    }

    /**
     * 根据任务ID获取Excel异步任务对象
     *
     * @param taskId 任务ID
     * @return 对应的Excel异步任务对象，如果不存在则返回null
     */
    @Override
    public ExcelAsyncTask getTask(String taskId) {
        return taskMap.get(taskId);
    }

    /**
     * 更新指定任务的状态
     *
     * @param taskId 任务ID
     * @param status 新的任务状态
     */
    @Override
    public void updateTaskStatus(String taskId, ExcelAsyncTask.TaskStatus status) {
        ExcelAsyncTask task = taskMap.get(taskId);
        if (task != null) {
            task.setStatus(status);
            // 根据任务状态设置相应的时间戳
            if (status == ExcelAsyncTask.TaskStatus.PROCESSING) {
                task.setStartTime(LocalDateTime.now());
            } else if (status == ExcelAsyncTask.TaskStatus.COMPLETED ||
                    status == ExcelAsyncTask.TaskStatus.FAILED ||
                    status == ExcelAsyncTask.TaskStatus.CANCELLED) {
                task.setFinishTime(LocalDateTime.now());
            }
            log.info("更新任务状态: {} -> {}", taskId, status.getDescription());
        }
    }

    /**
     * 更新指定任务的进度
     *
     * @param taskId   任务ID，用于标识需要更新进度的任务
     * @param progress 任务进度值，表示任务完成的百分比
     */
    @Override
    public void updateTaskProgress(String taskId, Integer progress) {
        // 获取指定ID的任务对象
        ExcelAsyncTask task = taskMap.get(taskId);
        if (task != null) {
            // 更新任务进度并记录日志
            task.setProgress(progress);
            log.debug("更新任务进度: {} -> {}%", taskId, progress);
        }
    }

    /**
     * 完成指定的任务
     *
     * @param taskId   任务ID
     * @param filePath 文件路径
     */
    @Override
    public void completeTask(String taskId, String filePath) {
        // 获取任务对象
        ExcelAsyncTask task = taskMap.get(taskId);
        if (task != null) {
            // 更新任务状态为已完成，并设置相关属性
            task.setStatus(ExcelAsyncTask.TaskStatus.COMPLETED)
                    .setFilePath(filePath)
                    .setProgress(100)
                    .setFinishTime(LocalDateTime.now());
            log.info("任务完成: {}, 文件路径: {}", taskId, filePath);
        }
    }

    /**
     * 将指定的任务标记为失败状态
     *
     * @param taskId       任务ID，用于标识需要标记为失败的具体任务
     * @param errorMessage 错误信息，描述任务失败的具体原因
     */
    @Override
    public void failTask(String taskId, String errorMessage) {
        // 获取任务对象并更新任务状态为失败
        ExcelAsyncTask task = taskMap.get(taskId);
        if (task != null) {
            task.setStatus(ExcelAsyncTask.TaskStatus.FAILED)
                    .setErrorMessage(errorMessage)
                    .setFinishTime(LocalDateTime.now());
            log.error("任务失败: {}, 错误信息: {}", taskId, errorMessage);
        }
    }

    /**
     * 移除指定ID的任务
     *
     * @param taskId 任务ID，用于标识要移除的任务
     */
    @Override
    public void removeTask(String taskId) {
        // 从任务映射中移除指定ID的任务
        ExcelAsyncTask removedTask = taskMap.remove(taskId);
        if (removedTask != null) {
            log.info("移除任务: {}", taskId);
        }
    }

    /**
     * 获取所有任务的副本
     *
     * @return 包含所有任务的线程安全Map副本，key为任务ID，value为Excel异步任务对象
     */
    @Override
    public Map<String, ExcelAsyncTask> getAllTasks() {
        return new ConcurrentHashMap<>(taskMap);
    }

    /**
     * 根据任务状态获取Excel异步任务列表
     *
     * @param status 任务状态，用于筛选符合条件的任务
     * @return 返回符合指定状态的Excel异步任务列表
     */
    @Override
    public List<ExcelAsyncTask> getTasksByStatus(ExcelAsyncTask.TaskStatus status) {
        // 从任务映射中筛选出指定状态的任务并返回列表
        return taskMap.values().stream()
                .filter(task -> task.getStatus() == status)
                .collect(Collectors.toList());
    }

    /**
     * 获取已过期的任务列表
     *
     * @param timeoutMinutes 超时时间阈值（分钟），用于判断任务是否过期
     * @return 返回状态为PROCESSING且开始时间早于超时阈值的任务列表
     */
    @Override
    public List<ExcelAsyncTask> getExpiredTasks(int timeoutMinutes) {
        // 计算超时时间阈值
        LocalDateTime timeoutThreshold = LocalDateTime.now().minusMinutes(timeoutMinutes);

        // 筛选状态为PROCESSING且开始时间早于超时阈值的任务
        return taskMap.values().stream()
                .filter(task -> task.getStatus() == ExcelAsyncTask.TaskStatus.PROCESSING)
                .filter(task -> task.getStartTime() != null && task.getStartTime().isBefore(timeoutThreshold))
                .collect(Collectors.toList());
    }

    /**
     * 获取需要清理的Excel异步任务列表
     *
     * @param retentionDays 任务保留天数，超过此天数的任务将被清理
     * @return 需要清理的Excel异步任务列表
     */
    @Override
    public List<ExcelAsyncTask> getTasksToCleanup(int retentionDays) {
        // 计算清理阈值时间，早于该时间创建的任务需要被清理
        LocalDateTime cleanupThreshold = LocalDateTime.now().minusDays(retentionDays);

        // 筛选出创建时间早于清理阈值的任务
        return taskMap.values().stream()
                .filter(task -> task.getCreateTime().isBefore(cleanupThreshold))
                .collect(Collectors.toList());
    }

    /**
     * 批量更新任务状态
     *
     * @param taskIds 任务ID列表，用于标识需要更新状态的任务
     * @param status  目标任务状态，将把指定任务更新为此状态
     */
    @Override
    public void batchUpdateTaskStatus(List<String> taskIds, ExcelAsyncTask.TaskStatus status) {
        // 遍历任务ID列表，逐个更新任务状态
        taskIds.forEach(taskId -> updateTaskStatus(taskId, status));
    }

    /**
     * 批量移除任务
     *
     * @param taskIds 需要移除的任务ID列表，不能为空
     */
    @Override
    public void batchRemoveTasks(List<String> taskIds) {
        // 遍历任务ID列表，逐个移除任务
        taskIds.forEach(this::removeTask);
    }

}
