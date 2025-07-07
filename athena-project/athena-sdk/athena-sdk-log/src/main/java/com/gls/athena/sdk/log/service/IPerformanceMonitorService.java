package com.gls.athena.sdk.log.service;

import com.gls.athena.sdk.log.domain.MethodLogDto;

/**
 * 性能监控服务接口
 * 职责：专门负责方法执行性能统计和监控
 *
 * @author george
 */
public interface IPerformanceMonitorService {

    /**
     * 计算方法执行时长
     *
     * @param methodLogDto 方法日志对象
     * @return 执行时长（毫秒）
     */
    long calculateExecutionTime(MethodLogDto methodLogDto);

    /**
     * 判断方法执行是否超时
     *
     * @param methodLogDto     方法日志对象
     * @param timeoutThreshold 超时阈值（毫秒）
     * @return true表示超时
     */
    boolean isTimeout(MethodLogDto methodLogDto, long timeoutThreshold);

    /**
     * 记录性能指标
     *
     * @param methodLogDto 方法日志对象
     */
    void recordPerformanceMetrics(MethodLogDto methodLogDto);
}
