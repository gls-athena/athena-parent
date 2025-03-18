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
     * 该方法用于配置Redis缓存管理器的各项参数，包括序列化方式、键前缀、过期时间等。
     * 首先构建基础的缓存配置，然后应用配置文件和注解中的过期时间设置。
     *
     * @param builder Redis缓存管理器构建器，用于构建和配置Redis缓存管理器
     */
    @Override
    public void customize(RedisCacheManager.RedisCacheManagerBuilder builder) {
        // 构建基础缓存配置，包括序列化方式、键前缀等
        RedisCacheConfiguration baseConfig = createBaseConfiguration();
        builder.cacheDefaults(baseConfig);

        // 应用配置文件中的过期时间设置，将配置文件中定义的过期时间应用到缓存管理器
        applyConfigurationExpires(builder, baseConfig);

        // 应用注解中的过期时间设置，将注解中定义的过期时间应用到缓存管理器
        applyAnnotationExpires(builder, baseConfig);
    }

    /**
     * 创建基础缓存配置
     * 该方法用于生成Redis缓存的基础配置，包括序列化方式和键前缀生成策略。
     * 默认配置基础上，设置键的序列化方式为字符串序列化，值的序列化方式为JSON序列化，
     * 并指定键前缀生成策略为自定义的生成方法。
     *
     * @return RedisCacheConfiguration 返回配置好的Redis缓存基础配置对象
     */
    private RedisCacheConfiguration createBaseConfiguration() {
        // 使用默认缓存配置，并设置键和值的序列化方式，以及键前缀生成策略
        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonRedisSerializer))
                .computePrefixWith(this::generateCacheKeyPrefix);
    }

    /**
     * 生成缓存键前缀
     * 该方法根据传入的缓存名称和配置中的前缀生成一个完整的缓存键前缀。
     * 前缀部分从缓存配置中获取，如果未配置则默认为空字符串。
     * 生成的缓存键前缀格式为：${prefix}${cacheName}:
     *
     * @param cacheName 缓存名称，用于生成缓存键前缀的一部分
     * @return 完整的缓存键前缀，包含配置的前缀（如果有）、缓存名称和冒号
     */
    private String generateCacheKeyPrefix(String cacheName) {
        // 从配置中获取Redis键前缀，如果未配置则使用空字符串
        // 将前缀与缓存名称和冒号拼接，生成完整的缓存键前缀
        return Optional.ofNullable(cacheProperties.getRedis().getKeyPrefix())
                .orElse("") + cacheName + ":";
    }

    /**
     * 应用来自配置文件的过期时间设置
     * 遍历配置文件中的过期时间设置，并应用到对应的缓存配置中。该方法会根据配置文件中定义的缓存名称和对应的过期时间，
     * 将这些设置应用到Redis缓存管理器的构建器中，从而实现对不同缓存的不同过期时间配置。
     *
     * @param builder    Redis缓存管理器的构建器，用于配置缓存管理器的各项参数
     * @param baseConfig 基础缓存配置，作为模板用于生成每个缓存的配置
     */
    private void applyConfigurationExpires(RedisCacheManager.RedisCacheManagerBuilder builder,
                                           RedisCacheConfiguration baseConfig) {
        // 遍历配置文件中的所有缓存过期时间设置
        defaultCacheProperties.getExpires().forEach((cacheName, cacheExpire) -> {
            // 将配置中的时间单位和时间值转换为Duration对象
            Duration duration = Duration.of(cacheExpire.getTimeToLive(),
                    cacheExpire.getTimeUnit().toChronoUnit());
            // 将生成的Duration对象应用到基础缓存配置中，并为指定缓存名称设置该配置
            builder.withCacheConfiguration(cacheName, baseConfig.entryTtl(duration));
        });
    }

    /**
     * 应用来自注解的过期时间设置
     * <p>
     * 该方法遍历从注解中获取的缓存名称及其对应的过期时间，并将这些过期时间应用到Redis缓存管理器的配置中。
     * 通过调用`builder.withCacheConfiguration`方法，为每个缓存名称设置相应的过期时间。
     *
     * @param builder    Redis缓存管理器的构建器，用于配置和管理缓存实例
     * @param baseConfig 基础缓存配置，包含缓存的默认设置，如序列化方式等
     */
    private void applyAnnotationExpires(RedisCacheManager.RedisCacheManagerBuilder builder,
                                        RedisCacheConfiguration baseConfig) {
        // 遍历从注解中获取的缓存名称及其对应的过期时间
        cacheExpireProcessor.getExpires().forEach((cacheName, duration) ->
                // 为每个缓存名称设置相应的过期时间
                builder.withCacheConfiguration(cacheName, baseConfig.entryTtl(duration)));
    }

}
