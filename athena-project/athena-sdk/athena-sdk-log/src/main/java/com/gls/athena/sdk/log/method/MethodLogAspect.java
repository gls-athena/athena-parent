package com.gls.athena.sdk.log.method;

import cn.hutool.extra.spring.SpringUtil;
import com.gls.athena.starter.core.support.AspectUtil;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * 系统日志切面
 * 提供方法级别的环绕通知功能，用于记录方法执行日志、跟踪执行状态，并通过事件机制上报执行结果
 *
 * @author george
 */
@Aspect
@Slf4j
@Component
public class MethodLogAspect {

    /**
     * 跟踪器，用于获取分布式追踪上下文信息
     */
    @Resource
    private Tracer tracer;

    /**
     * 方法事件发送器，用于发布方法执行成功/失败事件
     */
    @Resource
    private MethodEventSender methodEventSender;

    /**
     * 环绕通知方法，拦截被@MethodLog注解标记的方法
     *
     * @param point     连接点对象，包含被拦截方法的上下文信息（类实例、方法参数等）
     * @param methodLog 方法日志注解对象，包含日志配置元数据（如日志级别、事件类型配置等）
     * @return 被拦截方法的原始返回值，切面不会修改方法返回值
     * @throws Throwable 传播被拦截方法抛出的原始异常，切面不会吞没异常
     */
    @Around("@annotation(methodLog)")
    public Object around(ProceedingJoinPoint point, MethodLog methodLog) throws Throwable {
        // 构建基础追踪信息：获取分布式追踪ID和应用名称
        String traceId = this.getTraceId();
        String applicationName = SpringUtil.getApplicationName();

        // 解析目标方法信息：获取完整类名和方法名称
        String className = point.getTarget().getClass().getName();
        String methodName = point.getSignature().getName();

        // 准备方法调用上下文：提取方法参数并记录调用开始时间
        Map<String, Object> args = AspectUtil.getParams(point);
        Date startTime = new Date();

        try {
            // 执行目标方法并获取返回结果
            Object result = point.proceed();

            // 发送成功事件：包含完整调用上下文、执行结果和耗时信息
            methodEventSender.sendSuccessEvent(traceId, args, startTime, result, methodLog,
                    applicationName, className, methodName);

            return result;
        } catch (Throwable throwable) {
            // 异常处理：记录错误日志并发送异常事件（包含异常堆栈信息）
            log.error("方法执行异常：{}", throwable.getMessage(), throwable);

            methodEventSender.sendErrorEvent(traceId, args, startTime, methodLog,
                    applicationName, className, methodName, throwable);

            throw throwable;
        }
    }

    /**
     * 获取当前追踪上下文中的traceId
     *
     * @return 当前Span的追踪ID（traceId），当不存在有效Span时返回空字符串
     */
    private String getTraceId() {
        Span span = tracer.currentSpan();
        if (span != null) {
            return span.context().traceId();
        }
        return "";
    }
}
