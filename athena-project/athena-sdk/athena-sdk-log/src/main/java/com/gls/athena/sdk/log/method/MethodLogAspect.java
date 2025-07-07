package com.gls.athena.sdk.log.method;

import com.gls.athena.sdk.log.domain.MethodLogDto;
import com.gls.athena.sdk.log.service.IMethodLogBuilder;
import com.gls.athena.sdk.log.service.IMethodLogPublisher;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 方法日志切面
 * 职责：仅负责拦截方法调用，委托给专门的服务处理日志构建和发布
 *
 * @author george
 */
@Aspect
@Slf4j
@Component
public class MethodLogAspect {

    @Resource
    private IMethodLogBuilder methodLogBuilder;

    @Resource
    private IMethodLogPublisher methodLogPublisher;

    /**
     * 方法环绕通知
     * 职责单一：只负责拦截方法调用，具体的日志处理委托给专门的服务
     */
    @Around("@annotation(methodLog)")
    public Object around(ProceedingJoinPoint point, MethodLog methodLog) throws Throwable {
        MethodLogDto logDto = methodLogBuilder.createMethodLog(point, methodLog);

        try {
            Object result = point.proceed();
            methodLogBuilder.fillSuccessResult(logDto, result);
            return result;
        } catch (Throwable throwable) {
            methodLogBuilder.fillErrorResult(logDto, throwable);
            throw throwable;
        } finally {
            // 异步发布日志事件，不影响主流程
            methodLogPublisher.publishLog(logDto);
        }
    }
}
