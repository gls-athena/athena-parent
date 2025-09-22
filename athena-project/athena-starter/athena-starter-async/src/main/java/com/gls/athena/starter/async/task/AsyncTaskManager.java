package com.gls.athena.starter.async.task;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 异步任务管理器，用于创建、查询、更新和删除异步任务。
 *
 * @author lizy19
 */
@Component
public class AsyncTaskManager {

    @Resource
    private AsyncTaskRepository taskRepository;

    /**
     * 创建异步任务
     *
     * @param taskId      任务ID
     * @param type        任务类型
     * @param code        任务编码
     * @param name        任务名称
     * @param description 任务描述
     * @param params      任务参数
     * @return 创建的异步任务对象
     */
    public AsyncTask createTask(String taskId, String type, String code, String name, String description, Map<String, Object> params) {
        // 构建异步任务对象并设置初始状态
        AsyncTask task = new AsyncTask()
                .setTaskId(taskId)
                .setType(type)
                .setCode(code)
                .setName(name)
                .setDescription(description)
                .setParams(params)
                .setCreateTime(new Date())
                .setStatus(AsyncTaskStatus.PENDING)
                .setProgress(0);
        // 保存任务到数据库
        taskRepository.insert(task);
        return task;
    }

    /**
     * 根据任务ID获取异步任务对象
     *
     * @param taskId 任务的唯一标识符
     * @return 对应的异步任务对象，如果未找到则返回null
     */
    public AsyncTask getTask(String taskId) {
        return taskRepository.findByTaskId(taskId);
    }

    /**
     * 更新异步任务的状态
     *
     * @param taskId 任务ID，用于标识需要更新的任务
     * @param status 新的任务状态
     */
    public void updateTaskStatus(String taskId, AsyncTaskStatus status) {
        // 查找指定ID的任务
        AsyncTask task = taskRepository.findByTaskId(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Task not found: " + taskId);
        }
        task.setStatus(status);

        // 根据任务状态设置相应的开始或结束时间
        if (status == AsyncTaskStatus.COMPLETED
                || status == AsyncTaskStatus.FAILED
                || status == AsyncTaskStatus.CANCELED) {
            task.setEndTime(new Date());
        } else if (status == AsyncTaskStatus.PROCESSING) {
            task.setStartTime(new Date());
        }
        taskRepository.update(task);

    }

    /**
     * 更新异步任务的进度
     *
     * @param taskId   任务ID
     * @param progress 进度百分比，范围0-100
     */
    public void updateTaskProgress(String taskId, Integer progress) {
        AsyncTask task = taskRepository.findByTaskId(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Task not found: " + taskId);
        }
        task.setProgress(progress);
        taskRepository.update(task);
    }

    /**
     * 设置任务完成信息
     *
     * @param taskId 任务ID
     * @param result 任务结果数据
     */
    public void completeTask(String taskId, Map<String, Object> result) {
        AsyncTask task = taskRepository.findByTaskId(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Task not found: " + taskId);
        }
        task.setResult(result);
        task.setStatus(AsyncTaskStatus.COMPLETED);
        task.setProgress(100);
        task.setEndTime(new Date());
        taskRepository.update(task);
    }

    /**
     * 设置任务失败信息
     *
     * @param taskId       任务ID
     * @param errorMessage 错误信息
     */
    public void failTask(String taskId, String errorMessage) {
        AsyncTask task = taskRepository.findByTaskId(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Task not found: " + taskId);
        }
        task.setErrorMessage(errorMessage);
        task.setStatus(AsyncTaskStatus.FAILED);
        task.setProgress(0);
        task.setEndTime(new Date());
        taskRepository.update(task);
    }

    /**
     * 删除任务
     *
     * @param taskId 删除任务的唯一标识符
     */
    public void removeTask(String taskId) {
        taskRepository.deleteByTaskId(taskId);
    }

    /**
     * 获取所有任务
     *
     * @return 任务映射
     */
    public List<AsyncTask> getAllTasks() {
        return taskRepository.findAll();
    }

    /**
     * 获取指定状态的任务
     *
     * @param status 任务状态
     * @return 满足指定状态的任务列表
     */
    public List<AsyncTask> getTasksByStatus(AsyncTaskStatus status) {
        return taskRepository.getList(new AsyncTask().setStatus(status));
    }

    /**
     * 获取过期的任务
     *
     * @param timeoutMinutes 超时时间（分钟）
     * @return 过期的任务列表
     */
    public List<AsyncTask> getExpiredTasks(int timeoutMinutes) {
        Date expirationTime = new Date(System.currentTimeMillis() - timeoutMinutes * 60 * 1000L);
        List<AsyncTask> tasks = taskRepository.findBeforeCreateTime(expirationTime);
        return tasks.stream().filter(task ->
                task.getStatus() == AsyncTaskStatus.PENDING || task.getStatus() == AsyncTaskStatus.PROCESSING
        ).toList();
    }

    /**
     * 获取任务清理列表
     *
     * @param retentionDays 保留天数
     * @return 任务清理列表
     */
    public List<AsyncTask> getTasksToCleanup(int retentionDays) {
        Date cleanupTime = new Date(System.currentTimeMillis() - retentionDays * 24 * 60 * 60 * 1000L);
        return taskRepository.findBeforeCreateTime(cleanupTime);
    }

    /**
     * 批量更新任务状态
     *
     * @param taskIds 任务ID列表
     * @param status  新的任务状态
     */
    public void batchUpdateTaskStatus(List<String> taskIds, AsyncTaskStatus status) {
        taskRepository.batchUpdateTaskStatus(taskIds, status);
    }

    /**
     * 批量删除任务
     *
     * @param taskIds 任务ID列表
     */
    public void batchRemoveTasks(List<String> taskIds) {
        taskRepository.batchRemoveTasks(taskIds);
    }

}
