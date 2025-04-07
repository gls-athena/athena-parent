package com.gls.athena.sdk.log.method;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.gls.athena.sdk.log.domain.MethodLogDto;
import com.gls.athena.sdk.log.domain.MethodLogType;
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

    /**
     * 环绕通知，用于在方法执行前后进行日志记录和事件发布。
     *
     * @param point     切点对象，包含被拦截方法的相关信息。
     * @param methodLog 方法日志注解，包含日志记录所需的元数据。
     * @return 方法执行结果，如果方法正常执行则返回结果，否则抛出异常。
     * @throws Throwable 如果方法执行过程中抛出异常，则抛出该异常。
     */
    @Around("@annotation(methodLog)")
    public Object around(ProceedingJoinPoint point, MethodLog methodLog) throws Throwable {
        // 获取类名、方法名、应用名称和跟踪ID
        String className = point.getTarget().getClass().getName();
        String methodName = point.getSignature().getName();
        String applicationName = SpringUtil.getApplicationName();
        String traceId = this.getTraceId();

        // 记录方法开始执行的日志信息
        log.debug("[类名]:{},[方法]:{}", className, methodName);
        Map<String, Object> args = AspectUtil.getParams(point);
        log.debug("方法参数：{}", args);
        Date startTime = new Date();
        log.debug("方法开始时间：{}", startTime);

        try {
            // 执行目标方法并获取结果
            Object result = point.proceed();
            log.debug("方法执行结果：{}", result);
            log.debug("方法执行时间：{}ms", System.currentTimeMillis() - startTime.getTime());

            // 发布方法正常执行的事件
            SpringUtil.publishEvent(new MethodLogDto()
                    .setArgs(args)
                    .setResult(result)
                    .setStartTime(startTime)
                    .setEndTime(new Date())
                    .setType(MethodLogType.NORMAL)
                    .setTraceId(traceId)
                    .setCode(methodLog.code())
                    .setName(methodLog.name())
                    .setDescription(methodLog.description())
                    .setApplicationName(applicationName)
                    .setClassName(className)
                    .setMethodName(methodName));

            return result;
        } catch (Throwable throwable) {
            // 记录方法执行异常的日志信息
            log.error("方法执行异常：{}", throwable.getMessage(), throwable);

            // 发布方法执行异常的事件
            SpringUtil.publishEvent(new MethodLogDto()
                    .setArgs(args)
                    .setStartTime(startTime)
                    .setEndTime(new Date())
                    .setType(MethodLogType.ERROR)
                    .setTraceId(traceId)
                    .setErrorMessage(throwable.getMessage())
                    .setThrowable(ExceptionUtil.stacktraceToString(throwable))
                    .setCode(methodLog.code())
                    .setName(methodLog.name())
                    .setDescription(methodLog.description())
                    .setApplicationName(applicationName)
                    .setClassName(className)
                    .setMethodName(methodName));

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
