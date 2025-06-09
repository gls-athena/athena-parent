package com.gls.athena.sdk.log.method;

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
     * 方法环绕通知，用于记录方法执行的详细信息（入参、结果、耗时、异常等）
     * 通过@Around注解匹配带有MethodLog注解的方法，进行日志记录和事件发布
     *
     * @param point     连接点对象，包含目标方法上下文信息（类名、方法名、参数等）
     * @param methodLog 方法上的注解对象，包含日志配置参数（业务编码、名称、描述等）
     * @return Object 目标方法的执行结果（保持原有返回值）
     * @throws Throwable 目标方法抛出的异常（保持原有异常抛出）
     */
    @Around("@annotation(methodLog)")
    public Object around(ProceedingJoinPoint point, MethodLog methodLog) throws Throwable {
        // 初始化日志传输对象并设置基础信息
        MethodLogDto methodLogDto = new MethodLogDto();
        methodLogDto.setCode(methodLog.code());
        methodLogDto.setName(methodLog.name());
        methodLogDto.setDescription(methodLog.description());
        methodLogDto.setApplicationName(SpringUtil.getApplicationName());
        methodLogDto.setClassName(point.getSignature().getDeclaringTypeName());
        methodLogDto.setMethodName(point.getSignature().getName());
        methodLogDto.setArgs(AspectUtil.getParams(point));
        methodLogDto.setStartTime(new Date());
        methodLogDto.setTraceId(getTraceId());

        try {
            // 执行目标方法并记录正常结果
            Object result = point.proceed();
            methodLogDto.setResult(result);
            methodLogDto.setEndTime(new Date());
            methodLogDto.setType(MethodLogType.NORMAL);
            return result;
        } catch (Throwable throwable) {
            // 捕获异常并记录错误信息
            log.error("方法执行异常：{}", throwable.getMessage(), throwable);
            methodLogDto.setEndTime(new Date());
            methodLogDto.setType(MethodLogType.ERROR);
            methodLogDto.setErrorMessage(throwable.getMessage());

            // 记录完整异常堆栈（需添加异常处理工具类）
            methodLogDto.setThrowable(AspectUtil.getStackTraceAsString(throwable));
            throw throwable;
        } finally {
            log.info("方法执行完毕：{}", methodLogDto);
            SpringUtil.publishEvent(methodLogDto);
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
