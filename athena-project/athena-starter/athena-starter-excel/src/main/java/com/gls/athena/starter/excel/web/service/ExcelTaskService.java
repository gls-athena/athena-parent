package com.gls.athena.starter.excel.web.service;

import com.gls.athena.starter.excel.web.domain.ExcelAsyncTask;
import com.gls.athena.starter.excel.web.domain.TaskStatus;

import java.util.List;
import java.util.Map;

/**
 * Excel异步任务管理服务接口
 * 支持内存存储和数据库存储等多种方式
 *
 * @author george
 */
public interface ExcelTaskService {

    /**
     * 创建新的异步任务
     *
     * @param taskId      任务ID
     * @param filename    文件名
     * @param description 任务描述
     * @return 异步任务对象
     */
    ExcelAsyncTask createTask(String taskId, String filename, String description);

    /**
     * 获取任务信息
     *
     * @param taskId 任务ID
     * @return 任务信息，如果不存在返回null
     */
    ExcelAsyncTask getTask(String taskId);

    /**
     * 更新任务状态
     *
     * @param taskId 任务ID
     * @param status 新状态
     */
    void updateTaskStatus(String taskId, TaskStatus status);

    /**
     * 更新任务进度
     *
     * @param taskId   任务ID
     * @param progress 进度百分比 (0-100)
     */
    void updateTaskProgress(String taskId, Integer progress);

    /**
     * 设置任务完成信息
     *
     * @param taskId   任务ID
     * @param filePath 生成的文件路径
     */
    void completeTask(String taskId, String filePath);

    /**
     * 设置任务失败信息
     *
     * @param taskId       任务ID
     * @param errorMessage 错误信息
     */
    void failTask(String taskId, String errorMessage);

    /**
     * 移除任务
     *
     * @param taskId 任务ID
     */
    void removeTask(String taskId);

    /**
     * 获取所有任务
     *
     * @return 任务映射
     */
    Map<String, ExcelAsyncTask> getAllTasks();

    /**
     * 根据状态获取任务列表
     *
     * @param status 任务状态
     * @return 任务列表
     */
    List<ExcelAsyncTask> getTasksByStatus(TaskStatus status);

    /**
     * 获取过期任务列表
     *
     * @param timeoutMinutes 超时时间（分钟）
     * @return 过期任务列表
     */
    List<ExcelAsyncTask> getExpiredTasks(int timeoutMinutes);

    /**
     * 获取需要清理的任务列表
     *
     * @param retentionDays 保留天数
     * @return 需要清理的任务列表
     */
    List<ExcelAsyncTask> getTasksToCleanup(int retentionDays);

    /**
     * 批量更新任务状态
     *
     * @param taskIds 任务ID列表
     * @param status  新状态
     */
    default void batchUpdateTaskStatus(List<String> taskIds, TaskStatus status) {
        taskIds.forEach(taskId -> updateTaskStatus(taskId, status));
    }

    /**
     * 批量删除任务
     *
     * @param taskIds 任务ID列表
     */
    default void batchRemoveTasks(List<String> taskIds) {
        taskIds.forEach(this::removeTask);
    }
}
