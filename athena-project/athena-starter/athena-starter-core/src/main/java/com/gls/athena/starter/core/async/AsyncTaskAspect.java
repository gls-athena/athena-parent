package com.gls.athena.starter.core.async;

import cn.hutool.core.util.IdUtil;
import jakarta.annotation.Resource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * 异步任务切面
 *
 * @author george
 */
@Aspect
@Component
public class AsyncTaskAspect {
    /**
     * 异步任务事件发送器，用于发送异步任务事件
     */
    @Resource
    private AsyncTaskEventSender asyncTaskEventSender;

    /**
     * 环绕通知方法，用于处理带有@AsyncTask注解的方法。
     * 该方法会在目标方法执行前后执行，允许在方法执行前后添加自定义逻辑。
     *
     * @param point     ProceedingJoinPoint对象，用于获取目标方法的相关信息，并控制目标方法的执行。
     * @param asyncTask AsyncTask注解对象，包含注解中定义的属性值。
     * @return Object 目标方法的返回值，如果目标方法有返回值，则返回该值；否则返回null。
     * @throws Throwable 如果目标方法执行过程中抛出异常，则抛出该异常。
     */
    @Around("@annotation(asyncTask)")
    public Object aroundAsyncTask(ProceedingJoinPoint point, AsyncTask asyncTask) throws Throwable {
        // 创建任务ID
        String taskId = IdUtil.fastUUID();
        try {

            // 发送异步任务开始事件，通知任务已开始执行
            asyncTaskEventSender.sendAsyncTaskStartEvent(taskId, point, asyncTask);

            // 执行目标方法，并获取其返回值
            Object result = point.proceed();

            // 如果返回值是CompletableFuture，则等待其完成，并根据完成情况发送成功或失败事件
            if (result instanceof CompletableFuture<?> future) {
                future.whenComplete((res, throwable) -> {
                    if (throwable != null) {
                        asyncTaskEventSender.sendAsyncTaskErrorEvent(taskId, point, asyncTask, throwable);
                    } else {
                        asyncTaskEventSender.sendAsyncTaskSuccessEvent(taskId, point, asyncTask, res);
                    }
                });
            } else {
                // 如果返回值不是CompletableFuture，则直接发送失败事件，表示任务未返回预期的异步结果
                asyncTaskEventSender.sendAsyncTaskErrorEvent(taskId, point, asyncTask, new RuntimeException("返回的结果不是CompletableFuture"));
            }

            // 返回目标方法的执行结果
            return result;
        } catch (Throwable e) {
            // 如果目标方法执行过程中抛出异常，则发送失败事件，并重新抛出异常
            asyncTaskEventSender.sendAsyncTaskErrorEvent(taskId, point, asyncTask, e);
            throw e;
        }
    }

}
