package com.gls.athena.starter.data.redis.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 缓存过期时间注解
 * 用于指定缓存数据的存活时间，可以应用在方法或类上
 * 当应用在类上时，该类的所有缓存方法都将使用此过期时间配置
 *
 * @author george
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheExpire {

    /**
     * 设置缓存的过期时间
     * 需要配合 timeUnit 属性一起使用以确定具体的时间长度
     *
     * @return 过期时间的数值
     */
    long timeToLive();

    /**
     * 设置过期时间的单位
     * 默认使用秒作为时间单位
     * 可选值包括：DAYS、HOURS、MINUTES、SECONDS 等
     *
     * @return 时间单位，默认为 TimeUnit.SECONDS
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
