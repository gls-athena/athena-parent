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
     * 配置Spring Session的Redis JSON序列化器。
     * 该函数通过使用Jackson库将对象序列化为JSON格式，并将其存储在Redis中。
     * 序列化器可以通过自定义的Redis对象映射器配置进行进一步定制。
     *
     * @param jackson2ObjectMapperBuilder  Jackson对象映射器构建器，用于构建基础的ObjectMapper。
     * @param redisObjectMapperCustomizers Redis对象映射器自定义配置提供者，用于对ObjectMapper进行自定义配置。
     * @return 返回一个Redis对象序列化器，用于将对象序列化为JSON格式并存储在Redis中。
     */
    @Bean(SPRING_SESSION_DEFAULT_REDIS_SERIALIZER)
    @ConditionalOnMissingBean
    public RedisSerializer<Object> jsonRedisSerializer(
            Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder,
            ObjectProvider<RedisObjectMapperCustomizer> redisObjectMapperCustomizers) {

        // 构建基础ObjectMapper，用于后续的JSON序列化
        ObjectMapper objectMapper = jackson2ObjectMapperBuilder.build();

        // 应用所有自定义的Redis对象映射器配置，以增强或修改ObjectMapper的行为
        redisObjectMapperCustomizers.forEach(customizer -> customizer.customize(objectMapper));

        // 配置ObjectMapper的序列化特性，确保其符合预期的序列化行为
        configureObjectMapper(objectMapper);

        // 返回配置好的Jackson2JsonRedisSerializer，用于将对象序列化为JSON并存储在Redis中
        return new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);
    }

    /**
     * 配置ObjectMapper的序列化特性
     * <p>
     * 该方法用于配置ObjectMapper的序列化行为，包括设置访问器的可见性和默认类型处理。
     * 通过配置这些特性，可以确保ObjectMapper在序列化和反序列化过程中能够正确处理对象的可见性和类型信息。
     *
     * @param objectMapper 需要配置的对象映射器，通常是一个ObjectMapper实例。
     */
    private void configureObjectMapper(ObjectMapper objectMapper) {
        // 设置所有访问器的可见性为ANY，确保所有字段和属性都可以被序列化和反序列化
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        // 配置默认类型处理，允许在序列化过程中包含类型信息，以便在反序列化时能够正确还原对象类型
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );
    }

}
