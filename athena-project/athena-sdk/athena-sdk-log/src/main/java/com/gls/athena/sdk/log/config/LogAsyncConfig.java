package com.gls.athena.sdk.log.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 日志异步配置
 * 职责：专门负责日志相关的异步处理配置
 *
 * @author george
 */
@Slf4j
@EnableAsync
@AutoConfiguration
public class LogAsyncConfig {

    /**
     * 日志专用异步执行器
     * 确保日志处理不会阻塞主业务流程
     */
    @Bean("logTaskExecutor")
    public Executor logTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 核心线程数
        executor.setCorePoolSize(2);
        // 最大线程数
        executor.setMaxPoolSize(8);
        // 队列容量
        executor.setQueueCapacity(200);
        // 线程名前缀
        executor.setThreadNamePrefix("log-async-");
        // 空闲线程存活时间
        executor.setKeepAliveSeconds(60);
        // 拒绝策略：调用者运行，确保日志不丢失
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待任务完成再关闭
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 等待时间
        executor.setAwaitTerminationSeconds(60);

        executor.initialize();

        log.info("日志异步执行器初始化完成");
        return executor;
    }
}
