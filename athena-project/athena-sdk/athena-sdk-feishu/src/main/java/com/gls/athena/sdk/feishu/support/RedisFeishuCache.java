package com.gls.athena.sdk.feishu.support;

import com.gls.athena.starter.data.redis.support.RedisUtil;
import com.lark.oapi.core.cache.ICache;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 飞书缓存
 *
 * @author george
 */
@Component
public class RedisFeishuCache implements ICache {

    private static final String CACHE_PREFIX = "feishu";

    @Override
    public String get(String key) {
        return RedisUtil.getCacheValue(CACHE_PREFIX, key, String.class);
    }

    @Override
    public void set(String key, String value, int expire, TimeUnit timeUnit) {
        RedisUtil.setCacheValue(CACHE_PREFIX, key, value, expire, timeUnit);
    }
}
