package com.gls.athena.starter.data.redis.support;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * Redis配置类
 * <p>
 * 提供RedisTemplate的自动配置，统一设置序列化策略以确保数据的一致性和可读性。
 * 配置包括：
 * <ul>
 *   <li>键序列化：使用String序列化器，确保键的可读性</li>
 *   <li>值序列化：使用JSON序列化器，支持复杂对象的存储</li>
 *   <li>Hash键值序列化：与普通键值保持一致的序列化策略</li>
 * </ul>
 * </p>
 *
 * @author george
 * @since 1.0.0
 */
@Configuration
public class RedisConfig {

    /**
     * Redis操作模板
     * <p>
     * Spring Data Redis提供的核心操作类，用于执行各种Redis命令
     * </p>
     */
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * JSON序列化器
     * <p>
     * 用于将Java对象序列化为JSON格式存储到Redis中，
     * 以及将JSON格式的数据反序列化为Java对象
     * </p>
     */
    @Resource
    private RedisSerializer<Object> jsonRedisSerializer;

    /**
     * 初始化RedisTemplate序列化配置
     * <p>
     * 在Bean创建完成后自动执行，统一配置RedisTemplate的序列化策略：
     * </p>
     * <ul>
     *   <li>键序列化器：{@link RedisSerializer#string()} - 使用UTF-8编码的字符串序列化</li>
     *   <li>值序列化器：JSON序列化器 - 支持复杂对象的JSON格式存储</li>
     *   <li>Hash键序列化器：{@link RedisSerializer#string()} - 与普通键保持一致</li>
     *   <li>Hash值序列化器：JSON序列化器 - 与普通值保持一致</li>
     * </ul>
     *
     * @apiNote 此方法会在Spring容器初始化完成后自动调用，无需手动执行
     */
    @PostConstruct
    public void init() {
        // 设置键的序列化方式为String
        redisTemplate.setKeySerializer(RedisSerializer.string());
        // 设置Hash键的序列化方式为String
        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        // 设置值的序列化方式为JSON
        redisTemplate.setValueSerializer(jsonRedisSerializer);
        // 设置Hash值的序列化方式为JSON
        redisTemplate.setHashValueSerializer(jsonRedisSerializer);
    }
}
