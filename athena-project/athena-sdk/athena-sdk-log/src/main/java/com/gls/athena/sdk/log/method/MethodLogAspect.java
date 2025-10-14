package com.gls.athena.sdk.log.method;

import cn.hutool.extra.spring.SpringUtil;
import com.gls.athena.sdk.log.domain.MethodLogDto;
import com.gls.athena.sdk.log.domain.MethodLogType;
import com.gls.athena.sdk.log.service.IPerformanceMonitorService;
import com.gls.athena.sdk.log.service.ITraceService;
import com.gls.athena.starter.async.util.AopUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Date;

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
    private ITraceService traceService;
    @Resource
    private IPerformanceMonitorService performanceMonitorService;

    /**
     * 方法环绕通知
     * 拦截带有 {@link MethodLog} 注解的方法调用，记录方法执行信息、参数、结果或异常，并异步发布日志事件。
     *
     * @param point     切点对象，包含被拦截方法的信息
     * @param methodLog 方法上的注解实例，用于获取日志配置信息
     * @return 被拦截方法的返回值
     * @throws Throwable 如果被拦截方法抛出异常，则原样抛出
     */
    @Around("@annotation(methodLog)")
    public Object around(ProceedingJoinPoint point, MethodLog methodLog) throws Throwable {
        MethodLogDto logDto = createMethodLog(point, methodLog);

        try {
            Object result = point.proceed();
            fillSuccessResult(logDto, result);
            return result;
        } catch (Throwable throwable) {
            fillErrorResult(logDto, throwable);
            throw throwable;
        } finally {
            // 异步发布日志事件，不影响主流程
            SpringUtil.publishEvent(logDto);
        }
    }

    /**
     * 创建方法日志数据传输对象
     * 从切点和注解中提取必要信息，填充到 MethodLogDto 对象中
     *
     * @param point     切点对象，用于获取方法签名和参数
     * @param methodLog 方法上的注解实例，用于获取业务相关配置
     * @return 初始化完成的 MethodLogDto 实例
     */
    private MethodLogDto createMethodLog(ProceedingJoinPoint point, MethodLog methodLog) {
        MethodLogDto logDto = new MethodLogDto();

        // 设置注解信息
        logDto.setCode(methodLog.code())
                .setName(methodLog.name())
                .setDescription(methodLog.description());

        // 设置应用和方法信息
        logDto.setApplicationName(SpringUtil.getApplicationName())
                .setClassName(point.getSignature().getDeclaringTypeName())
                .setMethodName(point.getSignature().getName());

        // 设置执行参数和时间
        logDto.setArgs(AopUtil.getParams(point))
                .setStartTime(new Date())
                .setTraceId(traceService.getCurrentTraceId());

        return logDto;
    }

    /**
     * 填充方法成功执行的结果信息
     * 包括返回值、结束时间、日志类型，并记录性能指标
     *
     * @param logDto 方法日志数据传输对象
     * @param result 方法执行结果
     */
    private void fillSuccessResult(MethodLogDto logDto, Object result) {
        logDto.setResult(result)
                .setEndTime(new Date())
                .setType(MethodLogType.NORMAL);

        // 记录性能指标
        performanceMonitorService.recordPerformanceMetrics(logDto);
    }

    /**
     * 填充方法执行失败的异常信息
     * 包括错误消息、堆栈信息、结束时间、日志类型，并记录性能指标（含异常）
     *
     * @param logDto    方法日志数据传输对象
     * @param throwable 抛出的异常对象
     */
    private void fillErrorResult(MethodLogDto logDto, Throwable throwable) {
        log.error("方法执行异常：{}", throwable.getMessage(), throwable);

        logDto.setErrorMessage(throwable.getMessage())
                .setThrowable(AopUtil.getStackTraceAsString(throwable))
                .setEndTime(new Date())
                .setType(MethodLogType.ERROR);

        // 记录性能指标（包含异常情况）
        performanceMonitorService.recordPerformanceMetrics(logDto);
    }
}
