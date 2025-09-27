package com.gls.athena.starter.async.manager;

import com.gls.athena.starter.async.domain.AsyncTask;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认异步任务管理器实现类
 * 基于内存存储的异步任务管理器，提供任务的基本CRUD操作
 *
 * @author george
 */
@Slf4j
public class DefaultAsyncTaskManager implements IAsyncTaskManager {

    /**
     * 内存存储，使用ConcurrentHashMap保证线程安全
     */
    private final ConcurrentHashMap<String, AsyncTask> taskStorage = new ConcurrentHashMap<>();

    /**
     * 插入一个新的异步任务
     *
     * @param task 要插入的异步任务对象
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
     *
     * @param task 要更新的异步任务对象
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

}
