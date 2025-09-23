package com.gls.athena.starter.async.config;

import com.gls.athena.starter.async.web.service.IAsyncTaskInfoService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步线程池配置
 *
 * @author george
 */
@Configuration
@EnableConfigurationProperties(ThreadPoolProperties.class)
public class AsyncConfig {

    /**
     * 配置并创建线程池任务执行器
     * <p>
     * 该方法根据ThreadPoolProperties配置创建一个ThreadPoolTaskExecutor实例，
     * 用于处理异步任务。线程池的核心参数如核心线程数、最大线程数、队列容量等
     * 均从配置属性中获取。
     *
     * @param threadPoolProperties 线程池配置属性对象，包含线程池的各项配置参数
     * @return 配置完成并初始化的ThreadPoolTaskExecutor实例
     */
    @Bean(AsyncConstants.DEFAULT_THREAD_POOL_NAME)
    @ConditionalOnMissingBean(name = AsyncConstants.DEFAULT_THREAD_POOL_NAME)
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

    /**
     * 创建异步任务信息服务接口实例
     * <p>
     * 该方法创建一个IAsyncTaskInfoService实例，用于处理异步任务信息。
     *
     * @return 创建的IAsyncTaskInfoService实例
     */
    @Bean
    @ConditionalOnMissingBean(IAsyncTaskInfoService.class)
    public IAsyncTaskInfoService asyncTaskInfoService() {
        throw new IllegalStateException("请实现 IAsyncTaskInfoService 接口");
    }
}
