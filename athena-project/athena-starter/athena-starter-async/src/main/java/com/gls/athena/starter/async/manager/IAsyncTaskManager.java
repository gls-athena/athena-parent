package com.gls.athena.starter.async.manager;

import com.gls.athena.common.core.base.IService;
import com.gls.athena.starter.async.domain.AsyncTask;
import com.gls.athena.starter.async.domain.AsyncTaskStatus;

import java.util.Date;
import java.util.Map;

/**
 * 异步任务信息服务接口，提供对异步任务信息的创建、查询、更新等操作。
 * 继承自通用服务接口 IService<AsyncTask>。
 *
 * @author george
 */
public interface IAsyncTaskManager<V extends AsyncTask> extends IService<V> {

    /**
     * 创建一个新的异步任务。
     *
     * @param taskId      任务ID，唯一标识一个任务
     * @param code        任务编码，用于区分任务类型
     * @param name        任务名称
     * @param description 任务描述
     * @param params      任务参数，以键值对形式存储
     * @return 创建后的异步任务对象
     */
    default V createTask(String taskId, String code, String name, String description, Map<String, Object> params) {
        return null;
    }

    Class<V> getClassType();

    /**
     * 根据任务ID获取任务信息。
     *
     * @param taskId 任务ID
     * @return 对应的任务信息对象，若不存在则返回null
     */
    V getTask(String taskId);

    /**
     * 更新指定任务的状态。
     * 根据状态自动设置开始时间或结束时间，并在完成、失败或取消时将进度设为100。
     *
     * @param taskId 任务ID
     * @param status 新的任务状态
     * @throws IllegalArgumentException 当任务不存在时抛出异常
     */
    default void updateTaskStatus(String taskId, AsyncTaskStatus status) {
        V task = this.getTask(taskId);
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
     * 更新指定任务的进度。
     *
     * @param taskId   任务ID
     * @param progress 新的进度值（0-100）
     * @throws IllegalArgumentException 当任务不存在时抛出异常
     */
    default void updateTaskProgress(String taskId, Integer progress) {
        V task = this.getTask(taskId);
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
        V task = this.getTask(taskId);
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
        V task = this.getTask(taskId);
        if (task == null) {
            throw new IllegalArgumentException("任务不存在: " + taskId);
        }
        task.setErrorMessage(errorMessage);
        task.setStatus(AsyncTaskStatus.FAILED);
        task.setProgress(100);
        task.setEndTime(new Date());
        this.update(task);
    }
}
