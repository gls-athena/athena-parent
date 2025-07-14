package com.gls.athena.starter.core.async;

import cn.hutool.core.util.IdUtil;
import jakarta.annotation.Resource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * 异步任务切面处理器
 * <p>
 * 该切面用于拦截标注了{@link AsyncTask}注解的方法，提供异步任务生命周期管理功能：
 * <ul>
 *   <li>任务启动时发送开始事件</li>
 *   <li>监控异步任务执行状态</li>
 *   <li>根据执行结果发送成功或失败事件</li>
 * </ul>
 *
 * <p><strong>使用要求：</strong>
 * <ul>
 *   <li>被拦截的方法必须返回{@link CompletableFuture}类型</li>
 *   <li>返回非CompletableFuture类型将触发异常</li>
 * </ul>
 *
 * @author george
 * @see AsyncTask
 * @see AsyncTaskEventSender
 * @since 1.0.0
 */
@Aspect
@Component
public class AsyncTaskAspect {

    /**
     * 异步任务事件发送器
     * <p>负责发送异步任务各个生命周期阶段的事件通知</p>
     */
    @Resource
    private AsyncTaskEventSender asyncTaskEventSender;

    /**
     * 异步任务环绕通知
     * <p>
     * 拦截标注了{@code @AsyncTask}注解的方法，提供以下功能：
     * <ol>
     *   <li>为每个异步任务生成唯一标识ID</li>
     *   <li>在任务开始时发送启动事件</li>
     *   <li>执行目标业务方法</li>
     *   <li>监听异步任务完成状态并发送相应事件</li>
     * </ol>
     *
     * <p><strong>处理流程：</strong></p>
     * <pre>
     * 1. 生成任务ID
     * 2. 发送任务开始事件
     * 3. 执行目标方法
     * 4. 检查返回值类型
     *    - CompletableFuture: 注册完成回调，异步处理结果
     *    - 其他类型: 立即发送错误事件
     * 5. 返回原始结果
     * </pre>
     *
     * @param point     AOP连接点，包含目标方法的执行上下文信息
     * @param asyncTask 异步任务注解实例，包含任务配置参数
     * @return 目标方法的执行结果，通常为{@link CompletableFuture}类型
     * @throws Throwable 目标方法执行过程中的任何异常都会被重新抛出
     */
    @Around("@annotation(asyncTask)")
    public Object aroundAsyncTask(ProceedingJoinPoint point, AsyncTask asyncTask) throws Throwable {
        // 生成全局唯一的任务标识符，用于追踪任务生命周期
        String taskId = IdUtil.fastUUID();

        try {
            // 发送任务启动事件，标记任务开始执行
            asyncTaskEventSender.sendAsyncTaskStartEvent(taskId, point, asyncTask);

            // 执行目标业务方法
            Object result = point.proceed();

            // 处理异步任务结果
            if (result instanceof CompletableFuture<?> future) {
                // 为CompletableFuture注册完成回调，处理异步执行结果
                future.whenComplete((res, throwable) -> {
                    if (throwable != null) {
                        // 异步执行发生异常，发送错误事件
                        asyncTaskEventSender.sendAsyncTaskErrorEvent(taskId, point, asyncTask, throwable);
                    } else {
                        // 异步执行成功，发送成功事件
                        asyncTaskEventSender.sendAsyncTaskSuccessEvent(taskId, point, asyncTask, res);
                    }
                });
            } else {
                // 返回值类型不符合异步任务要求，发送类型错误事件
                asyncTaskEventSender.sendAsyncTaskErrorEvent(taskId, point, asyncTask,
                        new RuntimeException("异步任务方法必须返回CompletableFuture类型，实际返回: " +
                                (result != null ? result.getClass().getSimpleName() : "null")));
            }

            return result;

        } catch (Throwable e) {
            // 捕获方法执行过程中的异常，发送错误事件后重新抛出
            asyncTaskEventSender.sendAsyncTaskErrorEvent(taskId, point, asyncTask, e);
            throw e;
        }
    }
}
