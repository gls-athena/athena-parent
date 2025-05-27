package com.gls.athena.starter.core.async;

import cn.hutool.extra.spring.SpringUtil;
import com.gls.athena.starter.core.support.AspectUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

/**
 * 异步任务事件发送者
 *
 * <p>负责异步任务生命周期事件（开始/成功/错误）的标准化事件封装与发布，
 * 通过Spring事件机制实现业务解耦。</p>
 *
 * @author george
 */
@Component
public class AsyncTaskEventSender {

    /**
     * 发送异步任务开始事件
     *
     * <p>在任务执行前触发，创建包含任务基础信息的传输对象并设置初始状态，
     * 通过Spring事件机制广播事件。</p>
     *
     * @param taskId    任务唯一标识符，需保证全局唯一性
     * @param point     方法执行上下文，用于获取切点参数信息
     * @param asyncTask 任务注解实例，包含任务元数据配置
     */
    public void sendAsyncTaskStartEvent(String taskId, ProceedingJoinPoint point, AsyncTask asyncTask) {
        // 基础信息封装（复用创建逻辑）
        AsyncTaskDto asyncTaskDto = createAsyncTaskDto(taskId, point, asyncTask);

        // 状态机切换：执行中状态
        asyncTaskDto.setStatus(AsyncTaskStatus.EXECUTING);

        // 事件发布
        SpringUtil.publishEvent(asyncTaskDto);
    }

    /**
     * 构建异步任务数据传输对象
     *
     * <p>封装标准化的事件数据格式，包含任务元数据、执行参数和初始状态，
     * 为不同事件类型提供统一的数据基础。</p>
     *
     * @param taskId    任务唯一标识符
     * @param point     方法执行上下文，用于参数提取
     * @param asyncTask 任务注解实例，包含code/name等元数据
     * @return 标准化数据传输对象，已初始化等待状态
     */
    private AsyncTaskDto createAsyncTaskDto(String taskId, ProceedingJoinPoint point, AsyncTask asyncTask) {
        // 基础元数据封装
        AsyncTaskDto asyncTaskDto = new AsyncTaskDto();
        asyncTaskDto.setTaskId(taskId);
        asyncTaskDto.setCode(asyncTask.code());
        asyncTaskDto.setName(asyncTask.name());
        asyncTaskDto.setDescription(asyncTask.description());
        asyncTaskDto.setType(asyncTask.type());

        // 动态参数注入
        asyncTaskDto.setParams(AspectUtil.getParams(point));

        // 初始状态设置
        asyncTaskDto.setStatus(AsyncTaskStatus.WAITING);

        return asyncTaskDto;
    }

    /**
     * 发送异步任务错误事件
     *
     * <p>在任务执行异常时触发，记录错误堆栈信息并更新任务状态，
     * 提供完整的错误上下文供监听方处理。</p>
     *
     * @param taskId    任务唯一标识符
     * @param point     方法执行上下文
     * @param asyncTask 任务注解实例
     * @param throwable 具体异常对象，包含错误堆栈信息
     */
    public void sendAsyncTaskErrorEvent(String taskId, ProceedingJoinPoint point, AsyncTask asyncTask, Throwable throwable) {
        // 基础信息封装
        AsyncTaskDto asyncTaskDto = createAsyncTaskDto(taskId, point, asyncTask);

        // 状态机切换：失败状态+错误记录
        asyncTaskDto.setStatus(AsyncTaskStatus.FAIL);
        asyncTaskDto.setError(throwable.getMessage());

        // 事件发布
        SpringUtil.publishEvent(asyncTaskDto);
    }

    /**
     * 发送异步任务成功事件
     *
     * <p>在任务正常完成后触发，携带任务执行结果数据，
     * 完成状态机终态切换。</p>
     *
     * @param taskId    任务唯一标识符
     * @param point     方法执行上下文
     * @param asyncTask 任务注解实例
     * @param res       任务执行结果对象，需可序列化
     */
    public void sendAsyncTaskSuccessEvent(String taskId, ProceedingJoinPoint point, AsyncTask asyncTask, Object res) {
        // 基础信息封装
        AsyncTaskDto asyncTaskDto = createAsyncTaskDto(taskId, point, asyncTask);

        // 状态机切换：成功状态+结果绑定
        asyncTaskDto.setStatus(AsyncTaskStatus.SUCCESS);
        asyncTaskDto.setResult(res);

        // 事件发布
        SpringUtil.publishEvent(asyncTaskDto);
    }

}
