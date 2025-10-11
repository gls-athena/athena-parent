package com.gls.athena.starter.file.config;

import com.gls.athena.starter.data.redis.support.RedisUtil;
import com.gls.athena.starter.file.manager.IFileInfoManager;
import com.gls.athena.starter.file.manager.RedisFileInfoManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redis 文件信息配置类
 * 当 RedisUtil 类存在且配置项 athena.file.info = redis 时生效
 * 提供基于 Redis 的文件信息管理器实现
 *
 * @author george
 */
@Configuration
@ConditionalOnClass(RedisUtil.class)
@ConditionalOnProperty(prefix = "athena.file", name = "info", havingValue = "redis")
public class RedisFileInfoConfig {

    /**
     * 创建 Redis 文件信息管理器 Bean
     * 当容器中不存在 IFileInfoManager 类型的 Bean 时创建
     *
     * @return Redis 文件信息管理器实例
     */
    @Bean
    @ConditionalOnMissingBean(IFileInfoManager.class)
    public IFileInfoManager redisFileInfoManager() {
        return new RedisFileInfoManager();
    }
}
