package com.gls.athena.starter.data.jpa.support;

import org.hibernate.annotations.IdGeneratorType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Snowflake 分布式唯一ID生成器注解
 *
 * <p>用于标注需要使用雪花算法生成分布式唯一ID的字段。雪花算法生成的ID具有以下特点：
 * <ul>
 *   <li>全局唯一：在分布式环境下保证ID的唯一性</li>
 *   <li>有序递增：生成的ID按时间顺序递增</li>
 *   <li>高性能：单机每秒可生成数百万个ID</li>
 *   <li>无依赖：不依赖数据库等外部系统</li>
 * </ul>
 *
 * <p>ID结构（64位）：
 * <pre>
 * | 1位符号位 | 41位时间戳 | 5位数据中心ID | 5位工作机器ID | 12位序列号 |
 * |    0     |  xxxxxxx  |     xxxxx    |     xxxxx    |   xxxxxxxx  |
 * </pre>
 *
 * <p>使用示例：
 * <pre>{@code
 * @Entity
 * public class User {
 *     @Id
 *     @SnowflakeId(workerId = 1, datacenterId = 1)
 *     private Long id;
 *
 *     // 其他字段...
 * }
 * }</pre>
 *
 * @author george
 * @see SnowflakeIdGenerator
 * @since 1.0.0
 */
@IdGeneratorType(SnowflakeIdGenerator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SnowflakeId {

    /**
     * 工作机器ID
     *
     * <p>用于标识不同的服务器节点，取值范围：0~31（5位二进制）。
     * 在集群环境中，每个服务实例应配置不同的workerId以避免ID冲突。
     *
     * <p>配置建议：
     * <ul>
     *   <li>可以根据服务器IP地址的最后几位计算得出</li>
     *   <li>也可以通过配置中心统一分配管理</li>
     *   <li>确保同一数据中心内的workerId唯一</li>
     * </ul>
     *
     * @return 工作机器ID，默认值为0
     */
    long workerId() default 0;

    /**
     * 数据中心ID
     *
     * <p>用于标识不同的数据中心或机房，取值范围：0~31（5位二进制）。
     * 主要用于多数据中心部署场景，确保跨数据中心的ID唯一性。
     *
     * <p>配置建议：
     * <ul>
     *   <li>按地理位置划分：如华北(0)、华东(1)、华南(2)</li>
     *   <li>按环境划分：如开发(0)、测试(1)、生产(2)</li>
     *   <li>按业务线划分：如用户中心(0)、订单中心(1)、支付中心(2)</li>
     * </ul>
     *
     * @return 数据中心ID，默认值为0
     */
    long datacenterId() default 0;
}
