package com.gls.athena.sdk.feishu.support;

import com.gls.athena.starter.data.redis.support.RedisUtil;
import com.lark.oapi.core.cache.ICache;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 飞书缓存实现类，基于Redis存储SDK相关数据
 * 使用统一前缀隔离缓存命名空间
 *
 * @author george
 */
@Component
public class RedisFeishuCache implements ICache {

    /**
     * Redis缓存键前缀，用于隔离飞书SDK缓存数据
     * 值为"feishu"
     */
    private static final String CACHE_PREFIX = "feishu";

    /**
     * 获取指定键的缓存值
     *
     * @param key 缓存键（实际存储键为"feishu:{key}"格式）
     * @return 缓存值，若不存在或已过期则返回null
     */
    @Override
    public String get(String key) {
        return RedisUtil.getCacheValue(CACHE_PREFIX, key, String.class);
    }

    /**
     * 设置带过期时间的缓存
     *
     * @param key      缓存键（实际存储键为"feishu:{key}"格式）
     * @param value    缓存值
     * @param expire   过期时间数值
     * @param timeUnit 过期时间单位（支持秒、分、小时等时间单位）
     */
    @Override
    public void set(String key, String value, int expire, TimeUnit timeUnit) {
        RedisUtil.setCacheValue(CACHE_PREFIX, key, value, expire, timeUnit);
    }
}
