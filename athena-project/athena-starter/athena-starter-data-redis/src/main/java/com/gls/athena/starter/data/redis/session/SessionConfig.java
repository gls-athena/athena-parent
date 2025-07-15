package com.gls.athena.starter.data.redis.session;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.gls.athena.starter.data.redis.support.RedisObjectMapperCustomizer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Spring Session Redis配置
 * <p>
 * 提供Spring Session在Redis中的序列化配置，
 * 使用Jackson进行JSON序列化以提升性能和可读性。
 *
 * @author george
 * @since 1.0.0
 */
@Configuration
public class SessionConfig {
    /**
     * Spring Session默认Redis序列化器Bean名称
     */
    private static final String SPRING_SESSION_DEFAULT_REDIS_SERIALIZER = "springSessionDefaultRedisSerializer";

    /**
     * 创建Spring Session的JSON序列化器
     * <p>
     * 使用Jackson ObjectMapper进行JSON序列化，支持自定义配置。
     * 该序列化器将被Spring Session用于在Redis中存储会话数据。
     *
     * @param jackson2ObjectMapperBuilder  Jackson构建器
     * @param redisObjectMapperCustomizers 自定义配置器
     * @return Redis JSON序列化器
     */
    @Bean(SPRING_SESSION_DEFAULT_REDIS_SERIALIZER)
    @ConditionalOnMissingBean
    public RedisSerializer<Object> jsonRedisSerializer(
            Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder,
            ObjectProvider<RedisObjectMapperCustomizer> redisObjectMapperCustomizers) {

        ObjectMapper objectMapper = jackson2ObjectMapperBuilder.build();

        // 应用自定义配置
        redisObjectMapperCustomizers.forEach(customizer -> customizer.customize(objectMapper));

        // 配置序列化特性
        configureObjectMapper(objectMapper);

        return new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);
    }

    /**
     * 配置ObjectMapper序列化特性
     * <p>
     * 设置可见性和类型信息，确保正确的序列化/反序列化行为。
     *
     * @param objectMapper 待配置的ObjectMapper
     */
    private void configureObjectMapper(ObjectMapper objectMapper) {
        // 设置所有字段可见
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        // 启用类型信息以支持多态序列化
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );
    }
}
