package com.gls.athena.starter.async.manager;

import com.gls.athena.starter.async.domain.AsyncTask;
import com.gls.athena.starter.async.domain.AsyncTaskStatus;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 异步任务信息服务接口，提供对异步任务信息的创建、查询、更新等操作。
 * 继承自通用服务接口 IService。
 *
 * @author george
 */
public interface IAsyncTaskManager {

    /**
     * 创建一个新的异步任务。
     *
     * @param taskId      任务ID，唯一标识一个任务
     * @param type        任务类型
     * @param code        任务编码，用于区分任务类型
     * @param name        任务名称
     * @param description 任务描述
     * @param params      任务参数，以键值对形式存储
     * @return 创建后的异步任务对象
     */
    default AsyncTask createTask(String taskId, String type, String code, String name, String description, Map<String, Object> params) {
        AsyncTask task = new AsyncTask();
        task.setTaskId(taskId);
        task.setType(type);
        task.setCode(code);
        task.setName(name);
        task.setDescription(description);
        task.setParams(params);
        task.setStatus(AsyncTaskStatus.PENDING);
        task.setStartTime(new Date());
        task.setProgress(0);
        return this.insert(task);
    }

    /**
     * 插入任务信息
     *
     * @param task 任务对象
     * @return 插入后的任务对象
     */
    AsyncTask insert(AsyncTask task);

    /**
     * 根据任务ID获取任务信息。
     *
     * @param taskId 任务ID
     * @return 对应的任务信息对象，若不存在则返回null
     */
    AsyncTask getTask(String taskId);

    /**
     * 更新指定任务的状态。
     * 根据状态自动设置开始时间或结束时间，并在完成、失败或取消时将进度设为100。
     *
     * @param taskId 任务ID
     * @param status 新的任务状态
     * @throws IllegalArgumentException 当任务不存在时抛出异常
     */
    default void updateTaskStatus(String taskId, AsyncTaskStatus status) {
        AsyncTask task = this.getTask(taskId);
        if (task == null) {
            throw new IllegalArgumentException("任务不存在: " + taskId);
        }
        task.setStatus(status);
        // 如果是完成、失败或取消状态，则设置进度为100并记录结束时间
        if (status == AsyncTaskStatus.COMPLETED
                || status == AsyncTaskStatus.FAILED
                || status == AsyncTaskStatus.CANCELED) {
            task.setProgress(100);
            task.setEndTime(new Date());
            // 如果是处理中状态，则设置进度为0并记录开始时间
        } else if (status == AsyncTaskStatus.PROCESSING) {
            task.setProgress(0);
            task.setStartTime(new Date());
        }
        this.update(task);
    }

    /**
     * 更新任务信息
     *
     * @param task 任务对象
     */
    void update(AsyncTask task);

    /**
     * 更新任务进度
     *
     * @param taskId   任务ID，用于标识需要更新的任务
     * @param progress 任务进度值，表示任务完成的百分比
     * @param fileId   文件ID，与任务关联的文件标识
     */
    default void updateTaskProgress(String taskId, Integer progress, String fileId) {
        // 获取指定ID的任务对象
        AsyncTask task = this.getTask(taskId);
        if (task == null) {
            throw new IllegalArgumentException("任务不存在: " + taskId);
        }
        // 更新任务的进度和文件ID信息
        task.setProgress(progress);
        task.setFileId(fileId);
        // 保存更新后的任务信息
        this.update(task);
    }

    /**
     * 更新指定任务的进度。
     *
     * @param taskId   任务ID
     * @param progress 新的进度值（0-100）
     * @throws IllegalArgumentException 当任务不存在时抛出异常
     */
    default void updateTaskProgress(String taskId, Integer progress) {
        AsyncTask task = this.getTask(taskId);
        if (task == null) {
            throw new IllegalArgumentException("任务不存在: " + taskId);
        }
        task.setProgress(progress);
        this.update(task);
    }

    /**
     * 将指定任务标记为已完成，并设置结果信息。
     *
     * @param taskId 任务ID
     * @param result 任务执行结果数据
     * @throws IllegalArgumentException 当任务不存在时抛出异常
     */
    default void completeTask(String taskId, Map<String, Object> result) {
        AsyncTask task = this.getTask(taskId);
        if (task == null) {
            throw new IllegalArgumentException("任务不存在: " + taskId);
        }
        task.setResult(result);
        task.setStatus(AsyncTaskStatus.COMPLETED);
        task.setProgress(100);
        task.setEndTime(new Date());
        this.update(task);
    }

    /**
     * 将指定任务标记为失败，并设置错误信息。
     *
     * @param taskId       任务ID
     * @param errorMessage 错误信息描述
     * @throws IllegalArgumentException 当任务不存在时抛出异常
     */
    default void failTask(String taskId, String errorMessage) {
        AsyncTask task = this.getTask(taskId);
        if (task == null) {
            throw new IllegalArgumentException("任务不存在: " + taskId);
        }
        task.setErrorMessage(errorMessage);
        task.setStatus(AsyncTaskStatus.FAILED);
        task.setProgress(100);
        task.setEndTime(new Date());
        this.update(task);
    }

    /**
     * 删除指定任务。
     * 删除任务时，会删除该任务相关的所有数据。
     *
     * @param taskId 任务ID
     * @throws IllegalArgumentException 当任务不存在时抛出异常
     */
    default void deleteTask(String taskId) {
        AsyncTask task = this.getTask(taskId);
        if (task == null) {
            throw new IllegalArgumentException("任务不存在: " + taskId);
        }
        this.delete(taskId);
    }

    /**
     * 删除指定任务。
     * 删除任务时，会删除该任务相关的所有数据。
     *
     * @param taskId 删除的任务ID
     */
    void delete(String taskId);

    /**
     * 清理所有过期的任务。
     * 过期任务是指已经完成、失败或取消的任务，且已经超过一定时间间隔。
     * 默认的过期时间间隔为7天。
     *
     * @param expireTime 过期时间间隔（单位：天）
     */
    default void clearExpiredTasks(int expireTime) {
        Date expireDate = new Date(System.currentTimeMillis() - expireTime * 24L * 60 * 60 * 1000);
        List<AsyncTask> allTasks = this.getAllTasksBeforeStartTime(expireDate);
        for (AsyncTask task : allTasks) {
            if (task.getStatus() == AsyncTaskStatus.COMPLETED
                    || task.getStatus() == AsyncTaskStatus.FAILED
                    || task.getStatus() == AsyncTaskStatus.CANCELED) {
                this.delete(task.getTaskId());
            }
        }
    }

    /**
     * 获取指定开始时间之前的所有任务列表。
     *
     * @param startTime 指定的时间点
     * @return 符合条件的任务列表
     */
    List<AsyncTask> getAllTasksBeforeStartTime(Date startTime);

}
