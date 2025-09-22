package com.gls.athena.starter.async.task;

import java.util.Date;
import java.util.List;

/**
 * 异步任务仓库接口
 * 定义了异步任务的存储和检索操作
 *
 * @author george
 */
public interface AsyncTaskRepository {
    /**
     * 插入异步任务
     *
     * @param task 异步任务对象
     */
    void insert(AsyncTask task);

    /**
     * 根据任务ID查找异步任务
     *
     * @param taskId 任务ID
     * @return 异步任务对象
     */
    AsyncTask findByTaskId(String taskId);

    /**
     * 更新异步任务
     *
     * @param task 异步任务对象
     */
    void update(AsyncTask task);

    /**
     * 根据任务ID删除异步任务
     *
     * @param taskId 任务ID
     */
    void deleteByTaskId(String taskId);

    /**
     * 查找所有异步任务
     *
     * @return 异步任务列表
     */
    List<AsyncTask> findAll();

    /**
     * 根据异步任务条件查询任务列表
     *
     * @param asyncTask 异步任务查询条件
     * @return 符合条件的异步任务列表
     */
    List<AsyncTask> getList(AsyncTask asyncTask);

    /**
     * 查找创建时间在指定时间之前的异步任务
     *
     * @param createTime 时间条件
     * @return 创建时间在指定时间之前的异步任务列表
     */
    List<AsyncTask> findBeforeCreateTime(Date createTime);

    /**
     * 批量更新任务状态
     *
     * @param taskIds 任务ID列表
     * @param status  目标状态
     */
    void batchUpdateTaskStatus(List<String> taskIds, AsyncTaskStatus status);

    /**
     * 批量删除任务
     *
     * @param taskIds 要删除的任务ID列表
     */
    void batchRemoveTasks(List<String> taskIds);
}

