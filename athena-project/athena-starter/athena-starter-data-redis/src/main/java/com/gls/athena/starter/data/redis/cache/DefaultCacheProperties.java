package com.gls.athena.starter.data.redis.cache;

import com.gls.athena.common.core.constant.BaseProperties;
import com.gls.athena.common.core.constant.IConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Redis 缓存配置属性类
 * <p>
 * 基于 Spring Boot 配置属性机制，用于管理不同业务场景下的缓存过期策略。
 * 支持通过配置文件为不同的缓存名称设置个性化的过期时间和时间单位。
 * <p>
 * 配置示例：
 * <pre>
 * athena:
 *   cache:
 *     expires:
 *       userCache:
 *         timeToLive: 30
 *         timeUnit: MINUTES
 *       productCache:
 *         timeToLive: 1
 *         timeUnit: HOURS
 * </pre>
 *
 * @author george
 * @see BaseProperties
 * @see ConfigurationProperties
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = IConstants.BASE_PROPERTIES_PREFIX + ".cache")
public class DefaultCacheProperties extends BaseProperties {

    /**
     * 缓存过期时间配置映射
     * <p>
     * 存储不同缓存名称对应的过期策略配置，支持为每个缓存设置独立的过期时间。
     */
    private Map<String, CacheExpire> expires = new HashMap<>();

    /**
     * 缓存过期策略配置
     * <p>
     * 定义单个缓存项的过期时间及时间单位，用于精确控制缓存的生命周期。
     */
    @Data
    public static class CacheExpire {

        /**
         * 过期时间值
         * <p>
         * 必须与 {@link #timeUnit} 配合使用，确定缓存的具体过期时长
         */
        private Long timeToLive;

        /**
         * 时间单位
         * <p>
         * 指定 {@link #timeToLive} 的时间单位
         *
         * @see TimeUnit#SECONDS
         * @see TimeUnit#MINUTES
         * @see TimeUnit#HOURS
         * @see TimeUnit#DAYS
         */
        private TimeUnit timeUnit;
    }
}
