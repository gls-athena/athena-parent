package com.gls.athena.starter.data.redis.support;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * Redis自动配置类
 * <p>
 * 用于配置RedisTemplate的序列化方式，确保Redis数据的正确存储和读取
 * key使用String序列化
 * value使用JSON序列化
 * </p>
 *
 * @author george
 */
@Configuration
public class RedisConfig {

    /**
     * Redis操作模板
     * 用于执行Redis操作的核心类
     */
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * JSON序列化器
     * 用于对象与JSON字符串的转换
     */
    @Resource
    private RedisSerializer<Object> jsonRedisSerializer;

    /**
     * 初始化RedisTemplate配置
     * <p>
     * 设置Redis的key和value的序列化方式：
     * - 键(key)采用String序列化
     * - 值(value)采用JSON序列化
     * - Hash结构的key采用String序列化
     * - Hash结构的value采用JSON序列化
     * </p>
     */
    @PostConstruct
    public void init() {
        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        redisTemplate.setValueSerializer(jsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jsonRedisSerializer);
    }
}
