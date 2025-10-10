package com.gls.athena.starter.async.manager;

import com.gls.athena.starter.async.domain.AsyncTask;
import com.gls.athena.starter.data.redis.support.RedisUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;

/**
 * 基于Redis的异步任务管理器实现类
 * 该类提供了异步任务的增删改查功能，使用Redis作为数据存储
 *
 * @author george
 */
@Slf4j
public class RedisAsyncTaskManager implements IAsyncTaskManager {

    private static final String ASYNC_TASK = "async:task";

    /**
     * 插入异步任务到Redis中
     *
     * @param task 需要插入的异步任务对象
     * @return 插入后的异步任务对象
     */
    @Override
    public AsyncTask insert(AsyncTask task) {
        RedisUtil.setCacheTableRow(ASYNC_TASK, task.getTaskId(), task);
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
        return RedisUtil.getCacheTableRow(ASYNC_TASK, taskId, AsyncTask.class);
    }

    /**
     * 更新Redis中的异步任务信息
     *
     * @param task 需要更新的异步任务对象
     */
    @Override
    public void update(AsyncTask task) {
        RedisUtil.setCacheTableRow(ASYNC_TASK, task.getTaskId(), task);
    }

    /**
     * 根据任务ID删除Redis中的异步任务记录
     *
     * @param taskId 需要删除的任务唯一标识符
     */
    @Override
    public void delete(String taskId) {
        RedisUtil.deleteCacheTableRow(ASYNC_TASK, taskId);
    }

    /**
     * 获取所有开始时间早于指定时间的异步任务列表
     *
     * @param startTime 指定的时间点
     * @return 开始时间早于指定时间的所有异步任务列表
     */
    @Override
    public List<AsyncTask> getAllTasksBeforeStartTime(Date startTime) {
        // 获取所有异步任务
        List<AsyncTask> tasks = RedisUtil.getCacheTableRows(ASYNC_TASK, AsyncTask.class);
        // 过滤出开始时间早于指定时间的任务
        return tasks.stream()
                .filter(task -> task.getStartTime() != null && task.getStartTime().before(startTime))
                .toList();
    }
}

