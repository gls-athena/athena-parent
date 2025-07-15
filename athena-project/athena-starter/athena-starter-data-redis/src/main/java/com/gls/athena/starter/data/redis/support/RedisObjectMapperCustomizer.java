package com.gls.athena.starter.data.redis.support;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Redis 对象映射器自定义接口
 * <p>
 * 该函数式接口用于自定义 Redis 序列化和反序列化过程中使用的 Jackson ObjectMapper 配置。
 * 通过实现此接口，可以对 Redis 数据存储的 JSON 序列化行为进行个性化定制，
 * 例如配置日期格式、属性命名策略、序列化特性等。
 * </p>
 *
 * <p>使用示例：</p>
 * <pre>{@code
 * @Component
 * public class CustomRedisObjectMapperCustomizer implements RedisObjectMapperCustomizer {
 *     @Override
 *     public void customize(ObjectMapper objectMapper) {
 *         objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
 *         objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
 *     }
 * }
 * }</pre>
 *
 * @author george
 * @see ObjectMapper
 * @see com.fasterxml.jackson.databind.SerializationFeature
 * @since 1.0.0
 */
@FunctionalInterface
public interface RedisObjectMapperCustomizer {

    /**
     * 自定义 Redis 对象映射器配置
     * <p>
     * 此方法允许对传入的 ObjectMapper 实例进行个性化配置，
     * 以满足特定的 Redis 数据序列化需求。配置将应用于所有通过 Redis 进行的对象序列化操作。
     * </p>
     *
     * @param objectMapper Redis 序列化使用的 Jackson ObjectMapper 实例，不能为 null
     * @throws IllegalArgumentException 如果传入的 objectMapper 为 null
     * @see ObjectMapper#configure(com.fasterxml.jackson.databind.SerializationFeature, boolean)
     * @see ObjectMapper#setDateFormat(java.text.DateFormat)
     */
    void customize(ObjectMapper objectMapper);
}
