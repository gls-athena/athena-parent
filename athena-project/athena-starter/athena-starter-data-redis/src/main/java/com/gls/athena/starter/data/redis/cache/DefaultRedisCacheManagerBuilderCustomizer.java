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
 * Redis缓存管理器构建器定制化实现
 * 提供Redis缓存的以下核心功能：
 * 1. 配置缓存序列化方式
 * 2. 设置缓存键前缀
 * 3. 管理缓存过期时间
 * 4. 支持配置文件和注解两种方式设置过期时间
 *
 * @author
 */
@Component
public class DefaultRedisCacheManagerBuilderCustomizer implements RedisCacheManagerBuilderCustomizer {

    /**
     * Redis缓存基础配置属性
     */
    @Resource
    private CacheProperties cacheProperties;

    /**
     * 默认缓存配置属性
     */
    @Resource
    private DefaultCacheProperties defaultCacheProperties;

    /**
     * JSON序列化器，用于缓存值的序列化
     */
    @Resource
    private RedisSerializer<Object> jsonRedisSerializer;

    /**
     * 缓存过期时间处理器，处理注解方式的过期时间设置
     */
    @Resource
    private CacheExpireProcessor cacheExpireProcessor;

    /**
     * 定制化Redis缓存管理器的配置
     * 包括序列化方式、键前缀、过期时间等设置
     *
     * @param builder Redis缓存管理器构建器
     */
    @Override
    public void customize(RedisCacheManager.RedisCacheManagerBuilder builder) {
        // 构建基础缓存配置
        RedisCacheConfiguration baseConfig = createBaseConfiguration();
        builder.cacheDefaults(baseConfig);

        // 应用配置文件中的过期时间设置
        applyConfigurationExpires(builder, baseConfig);

        // 应用注解中的过期时间设置
        applyAnnotationExpires(builder, baseConfig);
    }

    /**
     * 创建基础缓存配置
     * 设置序列化方式和键前缀生成策略
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
     * 格式: ${prefix}${cacheName}:
     *
     * @param cacheName 缓存名称
     * @return 完整的缓存键前缀
     */
    private String generateCacheKeyPrefix(String cacheName) {
        return Optional.ofNullable(cacheProperties.getRedis().getKeyPrefix())
                .orElse("") + cacheName + ":";
    }

    /**
     * 应用来自配置文件的过期时间设置
     * 遍历配置文件中的过期时间设置，并应用到对应的缓存配置中
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
     * 应用来自注解的过期时间设置
     * 遍历注解中的过期时间设置，并应用到对应的缓存配置中
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
