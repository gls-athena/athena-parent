package com.gls.athena.starter.log.method;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.gls.athena.starter.log.domain.MethodLogDto;
import com.gls.athena.starter.log.domain.MethodLogType;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
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
     * 环绕通知
     *
     * @param point     切点
     * @param methodLog 方法日志注解
     * @return 方法执行结果
     * @throws Throwable 异常
     */
    @Around("@annotation(methodLog)")
    public Object around(ProceedingJoinPoint point, MethodLog methodLog) throws Throwable {
        String className = point.getTarget().getClass().getName();
        String methodName = point.getSignature().getName();
        String applicationName = SpringUtil.getApplicationName();
        String traceId = this.getTraceId();
        log.debug("[类名]:{},[方法]:{}", className, methodName);
        Map<String, Object> args = getMethodArgs(point);
        log.debug("方法参数：{}", args);
        Date startTime = new Date();
        log.debug("方法开始时间：{}", startTime);
        try {
            Object result = point.proceed();
            log.debug("方法执行结果：{}", result);
            log.debug("方法执行时间：{}ms", System.currentTimeMillis() - startTime.getTime());
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
            log.error("方法执行异常：{}", throwable.getMessage(), throwable);
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
     * 获取方法参数
     *
     * @param point 切点
     * @return 方法参数
     */
    private Map<String, Object> getMethodArgs(ProceedingJoinPoint point) {
        // 获取参数名称
        Map<String, Object> args = new HashMap<>();
        Object[] argValues = point.getArgs();
        String[] argNames = ((CodeSignature) point.getSignature()).getParameterNames();
        for (int i = 0; i < argNames.length; i++) {
            args.put(argNames[i], argValues[i]);
        }
        return args;
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
