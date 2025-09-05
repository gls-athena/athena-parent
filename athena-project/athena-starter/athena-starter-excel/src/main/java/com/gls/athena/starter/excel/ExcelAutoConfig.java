package com.gls.athena.starter.excel;

import com.gls.athena.starter.excel.config.ExcelProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Excel自动配置
 *
 * @author george
 */
@Configuration
@ComponentScan
@EnableAsync
@EnableScheduling
@EnableConfigurationProperties(ExcelProperties.class)
public class ExcelAutoConfig {

    /**
     * 配置Excel异步导出专用线程池
     *
     * @param excelProperties Excel配置属性
     * @return ��程池执行器
     */
    @Bean("excelAsyncExecutor")
    public Executor excelAsyncExecutor(ExcelProperties excelProperties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        ExcelProperties.AsyncThreadPool config = excelProperties.getAsyncThreadPool();

        executor.setCorePoolSize(config.getCorePoolSize());
        executor.setMaxPoolSize(config.getMaxPoolSize());
        executor.setQueueCapacity(config.getQueueCapacity());
        executor.setKeepAliveSeconds(config.getKeepAliveSeconds());
        executor.setThreadNamePrefix(config.getThreadNamePrefix());

        // 设置拒绝策略为调用者运行
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());

        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);

        executor.initialize();
        return executor;
    }
}