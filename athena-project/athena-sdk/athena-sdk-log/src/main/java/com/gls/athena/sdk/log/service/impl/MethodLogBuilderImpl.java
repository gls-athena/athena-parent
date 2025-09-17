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

    /**
     * 创建方法日志对象，初始化基本的日志信息
     *
     * @param point     切点信息，用于获取类名、方法名等元数据
     * @param methodLog 方法日志注解信息，包含code、name、description等配置
     * @return 初始化完成的MethodLogDto对象
     */
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

    /**
     * 填充方法执行成功的结果信息
     *
     * @param logDto 方法日志对象
     * @param result 方法执行结果
     */
    @Override
    public void fillSuccessResult(MethodLogDto logDto, Object result) {
        logDto.setResult(result)
                .setEndTime(new Date())
                .setType(MethodLogType.NORMAL);

        // 记录性能指标
        performanceMonitorService.recordPerformanceMetrics(logDto);
    }

    /**
     * 填充方法执行失败的异常信息
     *
     * @param logDto    方法日志对象
     * @param throwable 抛出的异常对象
     */
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
