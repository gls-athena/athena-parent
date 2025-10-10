package com.gls.athena.starter.core.thread;

import com.gls.athena.common.core.constant.IConstants;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置类
 * 用于配置和初始化应用程序的线程池
 */
@Configuration
@EnableConfigurationProperties(ThreadPoolProperties.class)
public class ThreadConfig {

    /**
     * 创建并配置线程池任务执行器
     *
     * @param threadPoolProperties 线程池配置属性对象，包含线程池的核心参数配置
     * @return 配置好的线程池执行器实例
     */
    @Primary
    @Bean(IConstants.DEFAULT_THREAD_POOL_NAME)
    public Executor threadPoolTaskExecutor(ThreadPoolProperties threadPoolProperties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置线程池核心参数
        executor.setCorePoolSize(threadPoolProperties.getCorePoolSize());
        executor.setMaxPoolSize(threadPoolProperties.getMaxPoolSize());
        executor.setQueueCapacity(threadPoolProperties.getQueueCapacity());
        executor.setKeepAliveSeconds(threadPoolProperties.getKeepAliveSeconds());
        executor.setThreadNamePrefix(threadPoolProperties.getThreadNamePrefix());
        // 设置拒绝策略为调用者运行策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 设置关闭时等待任务完成相关参数
        executor.setWaitForTasksToCompleteOnShutdown(threadPoolProperties.isWaitForTasksToCompleteOnShutdown());
        executor.setAwaitTerminationSeconds(threadPoolProperties.getAwaitTerminationSeconds());
        // 初始化线程池
        executor.initialize();
        return executor;
    }
}

