package com.gls.athena.common.core.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 性能监控工具类
 * <p>
 * 提供系统性能监控、方法执行时间统计等功能
 *
 * @author george
 */
@Slf4j
@UtilityClass
public class PerformanceUtil {

    private static final ConcurrentHashMap<String, AtomicLong> METHOD_CALL_COUNT = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, AtomicLong> METHOD_TOTAL_TIME = new ConcurrentHashMap<>();
    private static final ThreadLocal<Long> START_TIME = new ThreadLocal<>();

    /**
     * 开始计时
     * <p>
     * 记录当前线程的起始时间，并增加指定方法的调用次数。
     *
     * @param methodName 方法名，用于标识被监控的方法
     */
    public void startTiming(String methodName) {
        START_TIME.set(System.currentTimeMillis());
        METHOD_CALL_COUNT.computeIfAbsent(methodName, k -> new AtomicLong(0)).incrementAndGet();
    }

    /**
     * 结束计时并记录执行时间
     * <p>
     * 计算从开始到结束的时间差，并更新该方法的总执行时间。如果执行时间超过1秒，会输出警告日志。
     *
     * @param methodName 方法名，用于标识被监控的方法
     * @return 执行时间（毫秒），若未调用startTiming则返回0
     */
    public long endTiming(String methodName) {
        Long startTime = START_TIME.get();
        if (startTime == null) {
            log.warn("方法 {} 未调用 startTiming", methodName);
            return 0;
        }

        long executionTime = System.currentTimeMillis() - startTime;
        METHOD_TOTAL_TIME.computeIfAbsent(methodName, k -> new AtomicLong(0)).addAndGet(executionTime);
        START_TIME.remove();

        if (executionTime > 1000) {
            log.warn("方法 {} 执行时间较长: {}ms", methodName, executionTime);
        }

        return executionTime;
    }

    /**
     * 获取方法平均执行时间
     * <p>
     * 根据方法名获取其累计执行时间和调用次数，计算平均执行时间。
     *
     * @param methodName 方法名
     * @return 平均执行时间（毫秒），若无数据则返回0.0
     */
    public double getAverageExecutionTime(String methodName) {
        AtomicLong totalTime = METHOD_TOTAL_TIME.get(methodName);
        AtomicLong callCount = METHOD_CALL_COUNT.get(methodName);

        if (totalTime == null || callCount == null || callCount.get() == 0) {
            return 0.0;
        }

        return (double) totalTime.get() / callCount.get();
    }

    /**
     * 获取系统内存使用情况
     * <p>
     * 通过JVM管理接口获取堆内存和非堆内存的使用信息，并格式化为可读字符串。
     *
     * @return 内存使用信息字符串，包含已用、最大值及使用率等信息
     */
    public String getMemoryUsage() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapUsage = memoryBean.getNonHeapMemoryUsage();

        return String.format(
                "堆内存: 已用=%s, 最大=%s, 使用率=%.2f%% | 非堆内存: 已用=%s, 最大=%s",
                FileUtil.formatFileSize(heapUsage.getUsed()),
                FileUtil.formatFileSize(heapUsage.getMax()),
                (double) heapUsage.getUsed() / heapUsage.getMax() * 100,
                FileUtil.formatFileSize(nonHeapUsage.getUsed()),
                FileUtil.formatFileSize(nonHeapUsage.getMax())
        );
    }

    /**
     * 检查内存使用率是否过高
     * <p>
     * 判断当前堆内存使用率是否超过给定阈值。
     *
     * @param threshold 阈值（百分比，如80表示80%）
     * @return 如果使用率超过阈值返回true，否则返回false
     */
    public boolean isMemoryUsageHigh(double threshold) {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();

        double usagePercentage = (double) heapUsage.getUsed() / heapUsage.getMax() * 100;
        return usagePercentage > threshold;
    }

    /**
     * 打印性能统计报告
     * <p>
     * 输出系统内存使用情况以及所有被监控方法的调用次数和平均执行时间。
     */
    public void printPerformanceReport() {
        log.info("=== 性能统计报告 ===");
        log.info("系统内存使用情况: {}", getMemoryUsage());

        METHOD_CALL_COUNT.forEach((methodName, callCount) -> {
            double avgTime = getAverageExecutionTime(methodName);
            log.info("方法: {}, 调用次数: {}, 平均执行时间: {:.2f}ms",
                    methodName, callCount.get(), avgTime);
        });
    }

    /**
     * 清空统计数据
     * <p>
     * 清除所有方法的调用次数与执行时间统计信息。
     */
    public void clearStatistics() {
        METHOD_CALL_COUNT.clear();
        METHOD_TOTAL_TIME.clear();
        log.info("性能统计数据已清空");
    }

    /**
     * 执行并监控方法性能（无返回值）
     * <p>
     * 在执行Runnable任务前后自动调用startTiming和endTiming进行性能监控。
     *
     * @param methodName 方法名
     * @param runnable   要执行的任务
     */
    public void executeWithMonitoring(String methodName, Runnable runnable) {
        startTiming(methodName);
        try {
            runnable.run();
        } finally {
            endTiming(methodName);
        }
    }

    /**
     * 执行并监控方法性能（有返回值）
     * <p>
     * 在执行Supplier任务前后自动调用startTiming和endTiming进行性能监控。
     *
     * @param methodName 方法名
     * @param supplier   要执行的任务，支持返回结果
     * @param <T>        返回类型
     * @return 执行结果
     */
    public <T> T executeWithMonitoring(String methodName, java.util.function.Supplier<T> supplier) {
        startTiming(methodName);
        try {
            return supplier.get();
        } finally {
            endTiming(methodName);
        }
    }
}
