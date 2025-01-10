package com.gls.athena.starter.data.jpa.support;

import org.hibernate.annotations.IdGeneratorType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Snowflake ID生成器注解
 * 用于标注需要使用雪花算法生成分布式唯一ID的字段
 *
 * @author george
 */
@IdGeneratorType(SnowflakeIdGenerator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SnowflakeId {

    /**
     * 工作机器ID (0~31)
     * 用于标识不同的服务器节点
     *
     * @return 工作机器ID
     */
    long workerId() default 0;

    /**
     * 数据中心ID (0~31)
     * 用于标识不同的数据中心
     *
     * @return 数据中心ID
     */
    long datacenterId() default 0;
}
