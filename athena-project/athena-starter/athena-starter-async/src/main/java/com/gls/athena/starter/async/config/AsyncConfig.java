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
     * 内存异步任务管理器配置类
     * 当配置属性 athena.async.manager.type=memory 或未配置时生效
     */
    @Configuration
    @ConditionalOnProperty(prefix = "athena.async.manager", name = "type", havingValue = "memory", matchIfMissing = true)
    public static class InMemoryAsyncTaskManagerConfig {

        /**
         * 创建内存异步任务管理器Bean
         * 当容器中不存在IAsyncTaskManager类型的Bean时创建
         *
         * @return 内存异步任务管理器实例
         */
        @Bean
        @ConditionalOnMissingBean(IAsyncTaskManager.class)
        public IAsyncTaskManager inMemoryAsyncTaskManager() {
            return new InMemoryAsyncTaskManager();
        }
    }

    /**
     * Redis异步任务管理器配置类
     * 当classpath中存在RedisUtil类且配置属性 athena.async.manager.type=redis 时生效
     */
    @Configuration
    @ConditionalOnClass(RedisUtil.class)
    @ConditionalOnProperty(prefix = "athena.async.manager", name = "type", havingValue = "redis")
    public static class RedisAsyncTaskManagerConfig {

        /**
         * 创建Redis异步任务管理器Bean
         * 当容器中不存在IAsyncTaskManager类型的Bean时创建
         *
         * @return Redis异步任务管理器实例
         */
        @Bean
        @ConditionalOnMissingBean(IAsyncTaskManager.class)
        public IAsyncTaskManager redisAsyncTaskManager() {
            return new RedisAsyncTaskManager();
        }
    }

}

