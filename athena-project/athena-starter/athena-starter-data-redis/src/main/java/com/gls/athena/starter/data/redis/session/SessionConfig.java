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
 * Redis Session配置类
 * 用于配置Spring Session的Redis序列化器
 *
 * @author george
 * @since 1.0.0
 */
@Configuration
public class SessionConfig {
    /**
     * Spring Session默认Redis序列化器的Bean名称
     */
    private static final String SPRING_SESSION_DEFAULT_REDIS_SERIALIZER = "springSessionDefaultRedisSerializer";

    /**
     * 配置Spring Session的Redis JSON序列化器
     *
     * @param jackson2ObjectMapperBuilder  Jackson对象映射器构建器
     * @param redisObjectMapperCustomizers Redis对象映射器自定义配置提供者
     * @return Redis对象序列化器
     */
    @Bean(SPRING_SESSION_DEFAULT_REDIS_SERIALIZER)
    @ConditionalOnMissingBean
    public RedisSerializer<Object> jsonRedisSerializer(
            Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder,
            ObjectProvider<RedisObjectMapperCustomizer> redisObjectMapperCustomizers) {

        // 构建基础ObjectMapper
        ObjectMapper objectMapper = jackson2ObjectMapperBuilder.build();

        // 应用自定义的Redis对象映射器配置
        redisObjectMapperCustomizers.forEach(customizer -> customizer.customize(objectMapper));

        // 配置对象映射器的序列化特性
        configureObjectMapper(objectMapper);

        return new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);
    }

    /**
     * 配置ObjectMapper的序列化特性
     *
     * @param objectMapper 需要配置的对象映射器
     */
    private void configureObjectMapper(ObjectMapper objectMapper) {
        // 设置所有访问器的可见性
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 配置默认类型处理
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );
    }
}
