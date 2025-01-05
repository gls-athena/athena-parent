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
 * 默认缓存配置
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = IConstants.BASE_PROPERTIES_PREFIX + ".cache")
public class DefaultCacheProperties extends BaseProperties {
    /**
     * 缓存过期时间
     */
    private Map<String, CacheExpire> expires = new HashMap<>();

    /**
     * 缓存过期配置
     */
    @Data
    public static class CacheExpire {
        /**
         * 过期时间
         */
        private Long timeToLive;

        /**
         * 时间单位
         */
        private TimeUnit timeUnit;
    }
}
