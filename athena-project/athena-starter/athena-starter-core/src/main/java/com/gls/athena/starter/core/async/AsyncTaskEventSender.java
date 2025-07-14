package com.gls.athena.starter.core.async;

import cn.hutool.extra.spring.SpringUtil;
import com.gls.athena.starter.core.support.AspectUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

/**
 * 异步任务事件发送器
 *
 * <p>负责异步任务生命周期事件的封装与发布，支持任务开始、成功、失败三种事件类型。
 * 通过Spring事件机制实现与业务逻辑的解耦。</p>
 *
 * @author george
 */
@Component
public class AsyncTaskEventSender {

    /**
     * 发送任务开始事件
     *
     * @param taskId    任务唯一标识
     * @param point     AOP切点上下文
     * @param asyncTask 异步任务注解
     */
    public void sendAsyncTaskStartEvent(String taskId, ProceedingJoinPoint point, AsyncTask asyncTask) {
        // 创建任务DTO并设置执行状态
        AsyncTaskDto asyncTaskDto = createAsyncTaskDto(taskId, point, asyncTask);
        asyncTaskDto.setStatus(AsyncTaskStatus.EXECUTING);

        // 发布事件
        SpringUtil.publishEvent(asyncTaskDto);
    }

    /**
     * 创建异步任务数据传输对象
     *
     * @param taskId    任务唯一标识
     * @param point     AOP切点上下文
     * @param asyncTask 异步任务注解
     * @return 初始化的任务DTO，状态为WAITING
     */
    private AsyncTaskDto createAsyncTaskDto(String taskId, ProceedingJoinPoint point, AsyncTask asyncTask) {
        AsyncTaskDto asyncTaskDto = new AsyncTaskDto();
        asyncTaskDto.setTaskId(taskId);
        asyncTaskDto.setCode(asyncTask.code());
        asyncTaskDto.setName(asyncTask.name());
        asyncTaskDto.setDescription(asyncTask.description());
        asyncTaskDto.setType(asyncTask.type());
        asyncTaskDto.setParams(AspectUtil.getParams(point));
        asyncTaskDto.setStatus(AsyncTaskStatus.WAITING);

        return asyncTaskDto;
    }

    /**
     * 发送任务失败事件
     *
     * @param taskId    任务唯一标识
     * @param point     AOP切点上下文
     * @param asyncTask 异步任务注解
     * @param throwable 异常信息
     */
    public void sendAsyncTaskErrorEvent(String taskId, ProceedingJoinPoint point, AsyncTask asyncTask, Throwable throwable) {
        // 创建任务DTO并设置失败状态
        AsyncTaskDto asyncTaskDto = createAsyncTaskDto(taskId, point, asyncTask);
        asyncTaskDto.setStatus(AsyncTaskStatus.FAIL);
        asyncTaskDto.setError(throwable.getMessage());

        // 发布事件
        SpringUtil.publishEvent(asyncTaskDto);
    }

    /**
     * 发送任务成功事件
     *
     * @param taskId    任务唯一标识
     * @param point     AOP切点上下文
     * @param asyncTask 异步任务注解
     * @param res       任务执行结果
     */
    public void sendAsyncTaskSuccessEvent(String taskId, ProceedingJoinPoint point, AsyncTask asyncTask, Object res) {
        // 创建任务DTO并设置成功状态
        AsyncTaskDto asyncTaskDto = createAsyncTaskDto(taskId, point, asyncTask);
        asyncTaskDto.setStatus(AsyncTaskStatus.SUCCESS);
        asyncTaskDto.setResult(res);

        // 发布事件
        SpringUtil.publishEvent(asyncTaskDto);
    }
}
