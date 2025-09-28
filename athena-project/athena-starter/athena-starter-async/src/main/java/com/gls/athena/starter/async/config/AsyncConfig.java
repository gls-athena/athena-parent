package com.gls.athena.starter.async.config;

import com.gls.athena.starter.async.manager.IAsyncTaskManager;
import com.gls.athena.starter.async.manager.InMemoryAsyncTaskManager;
import com.gls.athena.starter.async.manager.RedisAsyncTaskManager;
import com.gls.athena.starter.data.redis.support.RedisUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 异步线程池配置
 *
 * @author george
 */
@Configuration
public class AsyncConfig {

    /**
     * 创建基于内存的异步任务管理器Bean
     * 当容器中不存在IAsyncTaskManager类型的Bean，且配置属性athena.async.manager.type值为memory或未配置时创建
     *
     * @return 内存异步任务管理器实例
     */
    @Bean
    @ConditionalOnMissingBean(IAsyncTaskManager.class)
    @ConditionalOnProperty(prefix = "athena.async.manager", name = "type", havingValue = "memory", matchIfMissing = true)
    public IAsyncTaskManager inMemoryAsyncTaskManager() {
        return new InMemoryAsyncTaskManager();
    }

    /**
     * 创建基于Redis的异步任务管理器Bean
     * 当容器中不存在IAsyncTaskManager类型的Bean，classpath中存在RedisUtil类，
     * 且配置属性athena.async.manager.type值为redis时创建
     *
     * @return Redis异步任务管理器实例
     */
    @Bean
    @ConditionalOnMissingBean(IAsyncTaskManager.class)
    @ConditionalOnClass(RedisUtil.class)
    @ConditionalOnProperty(prefix = "athena.async.manager", name = "type", havingValue = "redis")
    public IAsyncTaskManager redisAsyncTaskManager() {
        return new RedisAsyncTaskManager();
    }
}
