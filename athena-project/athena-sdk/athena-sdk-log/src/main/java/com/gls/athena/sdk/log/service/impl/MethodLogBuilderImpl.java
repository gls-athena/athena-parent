package com.gls.athena.sdk.log.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.gls.athena.common.core.util.AspectUtil;
import com.gls.athena.sdk.log.domain.MethodLogDto;
import com.gls.athena.sdk.log.domain.MethodLogType;
import com.gls.athena.sdk.log.method.MethodLog;
import com.gls.athena.sdk.log.service.IMethodLogBuilder;
import com.gls.athena.sdk.log.service.IPerformanceMonitorService;
import com.gls.athena.sdk.log.service.ITraceService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 方法日志构建器实现
 * 职责：专门负责构建和填充方法日志数据
 *
 * @author george
 */
@Slf4j
@Service
public class MethodLogBuilderImpl implements IMethodLogBuilder {

    @Resource
    private ITraceService traceService;

    @Resource
    private IPerformanceMonitorService performanceMonitorService;

    @Override
    public MethodLogDto createMethodLog(ProceedingJoinPoint point, MethodLog methodLog) {
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
        logDto.setArgs(AspectUtil.getParams(point))
                .setStartTime(new Date())
                .setTraceId(traceService.getCurrentTraceId());

        return logDto;
    }

    @Override
    public void fillSuccessResult(MethodLogDto logDto, Object result) {
        logDto.setResult(result)
                .setEndTime(new Date())
                .setType(MethodLogType.NORMAL);

        // 记录性能指标
        performanceMonitorService.recordPerformanceMetrics(logDto);
    }

    @Override
    public void fillErrorResult(MethodLogDto logDto, Throwable throwable) {
        log.error("方法执行异常：{}", throwable.getMessage(), throwable);

        logDto.setErrorMessage(throwable.getMessage())
                .setThrowable(AspectUtil.getStackTraceAsString(throwable))
                .setEndTime(new Date())
                .setType(MethodLogType.ERROR);

        // 记录性能指标（包含异常情况）
        performanceMonitorService.recordPerformanceMetrics(logDto);
    }
}
