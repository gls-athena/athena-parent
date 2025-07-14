package com.gls.athena.starter.data.redis.cache;

import jakarta.annotation.Resource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.context.annotation.Configuration;

/**
 * Redis缓存配置类
 *
 * <p>提供Spring Cache的基础配置，包括：
 * <ul>
 *     <li>启用缓存注解支持</li>
 *     <li>配置缓存解析器</li>
 *     <li>绑定缓存属性配置</li>
 * </ul>
 *
 * @author george
 * @since 1.0.0
 */
@Configuration
@EnableCaching
@EnableConfigurationProperties(DefaultCacheProperties.class)
public class CacheConfig implements CachingConfigurer {

    /**
     * 默认缓存解析器
     *
     * <p>负责解析缓存注解并路由到相应的缓存管理器
     */
    @Resource
    private DefaultCacheResolver defaultCacheResolver;

    /**
     * 获取缓存解析器
     *
     * <p>实现{@link CachingConfigurer#cacheResolver()}方法，
     * 为Spring Cache框架提供缓存解析策略
     *
     * @return 缓存解析器实例
     */
    @Override
    public CacheResolver cacheResolver() {
        return defaultCacheResolver;
    }

}
