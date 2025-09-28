package com.gls.athena.starter.async.manager;

import com.gls.athena.starter.async.domain.AsyncTask;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存异步任务管理器
 * <p>
 * 提供基于内存的异步任务管理功能，包括任务的增删改查以及根据开始时间筛选任务。
 * 使用ConcurrentHashMap作为存储结构以确保线程安全性。
 *
 * @author george
 */
@Slf4j
public class InMemoryAsyncTaskManager implements IAsyncTaskManager {

    /**
     * 内存存储，使用ConcurrentHashMap保证线程安全
     */
    private final ConcurrentHashMap<String, AsyncTask> taskStorage = new ConcurrentHashMap<>();

    /**
     * 插入一个新的异步任务
     * <p>
     * 若任务ID已存在或传入的任务对象非法（如为null、任务ID为空），将抛出异常。
     *
     * @param task 要插入的异步任务对象，不能为null且必须包含有效的任务ID
     * @return 插入成功的异步任务对象
     * @throws IllegalArgumentException 当任务对象或任务ID为空，或任务ID已存在时抛出
     */
    @Override
    public AsyncTask insert(AsyncTask task) {
        if (task == null || task.getTaskId() == null) {
            throw new IllegalArgumentException("任务对象或任务ID不能为空");
        }

        // 检查任务是否已存在
        if (taskStorage.containsKey(task.getTaskId())) {
            throw new IllegalArgumentException("任务ID已存在: " + task.getTaskId());
        }

        taskStorage.put(task.getTaskId(), task);
        log.debug("异步任务已创建: taskId={}, name={}", task.getTaskId(), task.getName());
        return task;
    }

    /**
     * 根据任务ID获取异步任务
     * <p>
     * 如果任务ID为空或找不到对应任务，则返回null。
     *
     * @param taskId 任务ID
     * @return 对应的异步任务对象，如果未找到或ID为空则返回null
     */
    @Override
    public AsyncTask getTask(String taskId) {
        if (taskId == null || taskId.trim().isEmpty()) {
            return null;
        }
        return taskStorage.get(taskId);
    }

    /**
     * 更新异步任务信息
     * <p>
     * 若任务不存在或传入的任务对象非法（如为null、任务ID为空），将抛出异常。
     *
     * @param task 要更新的异步任务对象，不能为null且必须包含有效的任务ID
     * @throws IllegalArgumentException 当任务对象或任务ID为空，或任务不存在时抛出
     */
    @Override
    public void update(AsyncTask task) {
        if (task == null || task.getTaskId() == null) {
            throw new IllegalArgumentException("任务对象或任务ID不能为空");
        }

        // 检查任务是否存在
        if (!taskStorage.containsKey(task.getTaskId())) {
            throw new IllegalArgumentException("任务不存在: " + task.getTaskId());
        }

        taskStorage.put(task.getTaskId(), task);
        log.debug("异步任务已更新: taskId={}, status={}, progress={}",
                task.getTaskId(), task.getStatus(), task.getProgress());
    }

    /**
     * 删除指定任务ID对应的异步任务
     *
     * @param taskId 需要删除的任务ID
     */
    @Override
    public void delete(String taskId) {
        taskStorage.remove(taskId);
    }

    /**
     * 获取所有开始时间早于给定时间的异步任务列表
     * <p>
     * 忽略开始时间为null的任务。
     *
     * @param startTime 时间阈值，用于过滤开始时间在此之前的任务
     * @return 符合条件的异步任务列表
     */
    @Override
    public List<AsyncTask> getAllTasksBeforeStartTime(Date startTime) {
        return taskStorage.values().stream()
                .filter(task -> task.getStartTime() != null && task.getStartTime().before(startTime))
                .toList();
    }

}
