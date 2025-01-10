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
 * Redis缓存配置属性类
 * 用于配置不同业务场景下的缓存过期策略
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = IConstants.BASE_PROPERTIES_PREFIX + ".cache")
public class DefaultCacheProperties extends BaseProperties {
    /**
     * 缓存过期时间配置映射
     * key: 缓存名称
     * value: 对应的过期策略
     */
    private Map<String, CacheExpire> expires = new HashMap<>();

    /**
     * 缓存过期策略配置
     * 定义单个缓存项的过期时间及时间单位
     */
    @Data
    public static class CacheExpire {
        /**
         * 过期时间值
         * 需要配合timeUnit一起使用
         */
        private Long timeToLive;

        /**
         * 时间单位
         * 如: TimeUnit.SECONDS, TimeUnit.MINUTES 等
         */
        private TimeUnit timeUnit;
    }
}
