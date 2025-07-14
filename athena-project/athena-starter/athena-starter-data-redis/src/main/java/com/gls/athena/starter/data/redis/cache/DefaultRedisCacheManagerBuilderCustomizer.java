package com.gls.athena.starter.data.redis.cache;

import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

/**
 * Redis缓存管理器构建器定制器
 * <p>
 * 提供以下功能：
 * <ul>
 *     <li>配置缓存序列化方式（Key使用String序列化，Value使用JSON序列化）</li>
 *     <li>自定义缓存键前缀生成策略</li>
 *     <li>支持配置文件和注解两种方式设置缓存过期时间</li>
 * </ul>
 *
 * @author athena-team
 */
@Component
public class DefaultRedisCacheManagerBuilderCustomizer implements RedisCacheManagerBuilderCustomizer {

    /**
     * Spring Boot缓存配置属性
     */
    @Resource
    private CacheProperties cacheProperties;

    /**
     * 自定义缓存配置属性
     */
    @Resource
    private DefaultCacheProperties defaultCacheProperties;

    /**
     * JSON序列化器
     */
    @Resource
    private RedisSerializer<Object> jsonRedisSerializer;

    /**
     * 缓存过期时间处理器
     */
    @Resource
    private CacheExpireProcessor cacheExpireProcessor;

    /**
     * 定制Redis缓存管理器配置
     *
     * @param builder Redis缓存管理器构建器
     */
    @Override
    public void customize(RedisCacheManager.RedisCacheManagerBuilder builder) {
        // 构建基础配置并设置为默认配置
        RedisCacheConfiguration baseConfig = createBaseConfiguration();
        builder.cacheDefaults(baseConfig);

        // 应用配置文件中的过期时间设置
        applyConfigurationExpires(builder, baseConfig);

        // 应用注解中的过期时间设置
        applyAnnotationExpires(builder, baseConfig);
    }

    /**
     * 创建基础缓存配置
     * <p>
     * 配置内容包括：
     * <ul>
     *     <li>Key序列化：String序列化器</li>
     *     <li>Value序列化：JSON序列化器</li>
     *     <li>Key前缀：自定义前缀生成策略</li>
     * </ul>
     *
     * @return Redis缓存基础配置
     */
    private RedisCacheConfiguration createBaseConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonRedisSerializer))
                .computePrefixWith(this::generateCacheKeyPrefix);
    }

    /**
     * 生成缓存键前缀
     * <p>
     * 前缀格式：{@code ${keyPrefix}${cacheName}:}
     *
     * @param cacheName 缓存名称
     * @return 缓存键前缀
     */
    private String generateCacheKeyPrefix(String cacheName) {
        return Optional.ofNullable(cacheProperties.getRedis().getKeyPrefix())
                .orElse("") + cacheName + ":";
    }

    /**
     * 应用配置文件中的过期时间设置
     *
     * @param builder    Redis缓存管理器构建器
     * @param baseConfig 基础缓存配置
     */
    private void applyConfigurationExpires(RedisCacheManager.RedisCacheManagerBuilder builder,
                                           RedisCacheConfiguration baseConfig) {
        defaultCacheProperties.getExpires().forEach((cacheName, cacheExpire) -> {
            Duration duration = Duration.of(cacheExpire.getTimeToLive(),
                    cacheExpire.getTimeUnit().toChronoUnit());
            builder.withCacheConfiguration(cacheName, baseConfig.entryTtl(duration));
        });
    }

    /**
     * 应用注解中的过期时间设置
     *
     * @param builder    Redis缓存管理器构建器
     * @param baseConfig 基础缓存配置
     */
    private void applyAnnotationExpires(RedisCacheManager.RedisCacheManagerBuilder builder,
                                        RedisCacheConfiguration baseConfig) {
        cacheExpireProcessor.getExpires().forEach((cacheName, duration) ->
                builder.withCacheConfiguration(cacheName, baseConfig.entryTtl(duration)));
    }

}
