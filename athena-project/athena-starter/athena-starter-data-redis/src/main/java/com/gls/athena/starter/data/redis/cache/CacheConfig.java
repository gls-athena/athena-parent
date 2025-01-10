package com.gls.athena.starter.data.redis.cache;

import jakarta.annotation.Resource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.context.annotation.Configuration;

/**
 * Redis缓存配置类
 * <p>
 * 该配置类用于设置系统的缓存行为，主要功能包括：
 * <ul>
 *     <li>启用Spring Cache注解功能</li>
 *     <li>配置默认的缓存解析器</li>
 *     <li>加载缓存配置属性</li>
 * </ul>
 * </p>
 *
 * @author george
 * @see CachingConfigurer
 * @see DefaultCacheProperties
 * @since 1.0.0
 */
@Configuration
@EnableCaching
@EnableConfigurationProperties(DefaultCacheProperties.class)
public class CacheConfig implements CachingConfigurer {

    /**
     * 默认缓存解析器
     * <p>用于解析缓存注解并确定使用哪个缓存管理器</p>
     */
    @Resource
    private DefaultCacheResolver defaultCacheResolver;

    /**
     * 配置并返回缓存解析器
     * <p>
     * 该方法实现自{@link CachingConfigurer}接口，用于为Spring Cache提供缓存解析策略。
     * 缓存解析器负责在运行时解析缓存操作和确定目标缓存。
     * </p>
     *
     * @return {@link CacheResolver} 缓存解析器实例
     */
    @Override
    public CacheResolver cacheResolver() {
        return defaultCacheResolver;
    }
}
