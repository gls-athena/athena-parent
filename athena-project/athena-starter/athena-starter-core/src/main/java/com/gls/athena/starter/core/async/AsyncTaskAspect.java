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
     * @param point     连接点对象，用于获取目标方法上下文并控制方法执行流程
     * @param asyncTask 标注在方法上的异步任务注解，包含任务配置元数据
     * @return 目标方法的原始返回值，对CompletableFuture类型会附加回调处理
     * @throws Throwable 传播目标方法执行过程中抛出的任何异常
     */
    @Around("@annotation(asyncTask)")
    public Object aroundAsyncTask(ProceedingJoinPoint point, AsyncTask asyncTask) throws Throwable {
        // 生成全局唯一任务标识
        String taskId = IdUtil.fastUUID();
        try {
            // 前置处理：发送任务启动事件，记录任务初始化状态
            asyncTaskEventSender.sendAsyncTaskStartEvent(taskId, point, asyncTask);

            // 执行目标业务方法并获取原始返回值
            Object result = point.proceed();

            // 异步结果处理：对CompletableFuture类型结果注册完成回调
            if (result instanceof CompletableFuture<?> future) {
                future.whenComplete((res, throwable) -> {
                    // 后置处理：根据异步任务完成状态发送对应事件
                    if (throwable != null) {
                        asyncTaskEventSender.sendAsyncTaskErrorEvent(taskId, point, asyncTask, throwable);
                    } else {
                        asyncTaskEventSender.sendAsyncTaskSuccessEvent(taskId, point, asyncTask, res);
                    }
                });
            } else {
                // 同步结果异常处理：不符合异步结果约定时发送错误通知
                asyncTaskEventSender.sendAsyncTaskErrorEvent(taskId, point, asyncTask,
                        new RuntimeException("返回的结果不是CompletableFuture"));
            }

            return result;
        } catch (Throwable e) {
            // 异常处理：捕获执行过程中的异常并发送错误事件
            asyncTaskEventSender.sendAsyncTaskErrorEvent(taskId, point, asyncTask, e);
            throw e;
        }
    }
}
