package com.gls.athena.sdk.log.service.impl;

import com.gls.athena.sdk.log.config.LogProperties;
import com.gls.athena.sdk.log.domain.MethodLogDto;
import com.gls.athena.sdk.log.service.IPerformanceMonitorService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 性能监控服务实现
 * 职责：专门负责方法执行性能统计和监控
 *
 * @author george
 */
@Slf4j
@Service
public class PerformanceMonitorServiceImpl implements IPerformanceMonitorService {

    @Resource
    private LogProperties logProperties;

    /**
     * 计算方法的执行时间
     *
     * @param methodLogDto 方法日志数据传输对象，包含开始时间和结束时间
     * @return 方法执行时间（毫秒），如果时间信息不完整则返回0
     */
    @Override
    public long calculateExecutionTime(MethodLogDto methodLogDto) {
        if (methodLogDto.getStartTime() == null || methodLogDto.getEndTime() == null) {
            return 0;
        }
        return methodLogDto.getEndTime().getTime() - methodLogDto.getStartTime().getTime();
    }

    /**
     * 判断方法是否超时
     *
     * @param methodLogDto     方法日志数据传输对象
     * @param timeoutThreshold 超时阈值（毫秒）
     * @return 如果执行时间超过阈值返回true，否则返回false
     */
    @Override
    public boolean isTimeout(MethodLogDto methodLogDto, long timeoutThreshold) {
        long executionTime = calculateExecutionTime(methodLogDto);
        return executionTime > timeoutThreshold;
    }

    /**
     * 记录方法的性能指标
     * 包括执行时间、超时警告等信息
     *
     * @param methodLogDto 方法日志数据传输对象
     */
    @Override
    public void recordPerformanceMetrics(MethodLogDto methodLogDto) {
        // 检查是否启用性能监控
        if (!logProperties.getPerformance().isEnabled()) {
            return;
        }

        try {
            long executionTime = calculateExecutionTime(methodLogDto);
            long timeoutThreshold = logProperties.getPerformance().getTimeoutThreshold();

            // 记录基本性能指标
            log.debug("方法性能统计 - 类：{}，方法：{}，执行时长：{}ms",
                    methodLogDto.getClassName(),
                    methodLogDto.getMethodName(),
                    executionTime);

            // 超时警告
            if (isTimeout(methodLogDto, timeoutThreshold)) {
                log.warn("方法执行超时 - {}#{}，执行时长：{}ms，阈值：{}ms",
                        methodLogDto.getClassName(),
                        methodLogDto.getMethodName(),
                        executionTime,
                        timeoutThreshold);
            }

        } catch (Exception e) {
            log.error("记录性能指标失败：{}", e.getMessage());
        }
    }
}
