package com.gls.athena.starter.async.manager;

import com.gls.athena.starter.async.domain.AsyncTask;
import com.gls.athena.starter.data.redis.support.RedisUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 基于Redis的异步任务管理器实现类
 * 该类提供了异步任务的增删改查功能，使用Redis作为数据存储
 *
 * @author george
 */
@Slf4j
public class RedisAsyncTaskManager implements IAsyncTaskManager {

    private static final String ASYNC_TASK_KEY_PREFIX = "async_task:";

    /**
     * 插入异步任务到Redis中
     *
     * @param task 需要插入的异步任务对象
     * @return 插入后的异步任务对象
     */
    @Override
    public AsyncTask insert(AsyncTask task) {
        RedisUtil.setCacheValue(ASYNC_TASK_KEY_PREFIX + task.getTaskId(), task);
        return task;
    }

    /**
     * 根据任务ID从Redis中获取异步任务
     *
     * @param taskId 任务唯一标识符
     * @return 对应的异步任务对象，如果不存在则返回null
     */
    @Override
    public AsyncTask getTask(String taskId) {
        return RedisUtil.getCacheValue(ASYNC_TASK_KEY_PREFIX + taskId, AsyncTask.class);
    }

    /**
     * 更新Redis中的异步任务信息
     *
     * @param task 需要更新的异步任务对象
     */
    @Override
    public void update(AsyncTask task) {
        RedisUtil.setCacheValue(ASYNC_TASK_KEY_PREFIX + task.getTaskId(), task);
    }
}

