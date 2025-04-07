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
 *
 * @author george
 */
@Aspect
@Slf4j
@Component
public class MethodLogAspect {

    /**
     * 跟踪器
     */
    @Resource
    private Tracer tracer;
    @Resource
    private MethodEventSender methodEventSender;

    /**
     * 环绕通知，用于在方法执行前后进行日志记录和事件发布。
     *
     * @param point     切点对象，包含被拦截方法的相关信息，如目标类、方法签名等。
     * @param methodLog 方法日志注解，包含日志记录所需的元数据，如日志级别、事件类型等。
     * @return 方法执行结果，如果方法正常执行则返回结果，否则抛出异常。
     * @throws Throwable 如果方法执行过程中抛出异常，则抛出该异常。
     */
    @Around("@annotation(methodLog)")
    public Object around(ProceedingJoinPoint point, MethodLog methodLog) throws Throwable {
        // 获取类名、方法名、应用名称和跟踪ID，用于日志记录和事件发布
        String traceId = this.getTraceId();
        String applicationName = SpringUtil.getApplicationName();
        String className = point.getTarget().getClass().getName();
        String methodName = point.getSignature().getName();

        // 获取方法参数并记录方法开始执行的时间
        Map<String, Object> args = AspectUtil.getParams(point);
        Date startTime = new Date();

        try {
            // 执行目标方法并获取结果
            Object result = point.proceed();

            // 发送方法执行成功的事件，包含方法执行结果和相关信息
            methodEventSender.sendSuccessEvent(traceId, args, startTime, result, methodLog, applicationName, className, methodName);

            return result;
        } catch (Throwable throwable) {
            // 记录方法执行异常的日志信息，并发送方法执行失败的事件
            log.error("方法执行异常：{}", throwable.getMessage(), throwable);

            methodEventSender.sendErrorEvent(traceId, args, startTime, methodLog, applicationName, className, methodName, throwable);

            throw throwable;
        }
    }

    /**
     * 获取traceId
     *
     * @return traceId
     */
    private String getTraceId() {
        Span span = tracer.currentSpan();
        if (span != null) {
            return span.context().traceId();
        }
        return "";
    }
}
