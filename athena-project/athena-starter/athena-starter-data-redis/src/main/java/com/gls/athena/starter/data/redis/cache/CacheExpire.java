package com.gls.athena.starter.data.redis.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 缓存过期时间注解
 * <p>
 * 该注解用于配置缓存数据的生存时间（TTL），支持应用在方法或类级别：
 * <ul>
 *   <li>方法级别：为特定缓存方法设置过期时间</li>
 *   <li>类级别：为该类中所有缓存方法设置统一的过期时间</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>{@code
 * @CacheExpire(timeToLive = 30, timeUnit = TimeUnit.MINUTES)
 * @Cacheable("userCache")
 * public User getUserById(Long id) {
 *     return userRepository.findById(id);
 * }
 * }</pre>
 *
 * @author george
 * @see java.util.concurrent.TimeUnit
 * @since 1.0.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheExpire {

    /**
     * 缓存数据的生存时间值
     * <p>
     * 该值必须为正数，表示缓存在指定时间单位下的存活时长。
     * 过期后缓存将被自动清除，下次访问时会重新加载数据。
     *
     * @return 过期时间的数值，必须大于0
     */
    long timeToLive();

    /**
     * 过期时间的时间单位
     * <p>
     * 配合 {@link #timeToLive()} 属性使用，用于确定具体的过期时间长度。
     * 支持所有 {@link TimeUnit} 枚举值，包括：
     * <ul>
     *   <li>{@link TimeUnit#NANOSECONDS} - 纳秒</li>
     *   <li>{@link TimeUnit#MICROSECONDS} - 微秒</li>
     *   <li>{@link TimeUnit#MILLISECONDS} - 毫秒</li>
     *   <li>{@link TimeUnit#SECONDS} - 秒（默认值）</li>
     *   <li>{@link TimeUnit#MINUTES} - 分钟</li>
     *   <li>{@link TimeUnit#HOURS} - 小时</li>
     *   <li>{@link TimeUnit#DAYS} - 天</li>
     * </ul>
     *
     * @return 时间单位，默认为 {@link TimeUnit#SECONDS}
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
