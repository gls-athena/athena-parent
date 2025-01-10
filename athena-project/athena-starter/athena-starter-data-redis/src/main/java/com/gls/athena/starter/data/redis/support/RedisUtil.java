package com.gls.athena.starter.data.redis.support;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.extra.spring.SpringUtil;
import lombok.experimental.UtilityClass;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis 通用操作工具类
 * 提供缓存、分布式锁、计数器等常用功能
 *
 * @author george
 */
@UtilityClass
public class RedisUtil {
    private static final String LOCK_PREFIX = "athena:lock:";
    private static final String CACHE_PREFIX = "athena:cache:";
    private static final String COUNTER_PREFIX = "athena:counter:";
    private static final String SEPARATOR = ":";

    /**
     * 获取缓存值
     *
     * @param cacheName 缓存名称
     * @param key       键
     * @return 缓存值
     */
    public Object getCacheValue(String cacheName, String key) {
        return getRedisTemplate().opsForValue().get(getCacheKey(cacheName, key));
    }

    /**
     * 获取缓存值
     *
     * @param cacheName 缓存名称
     * @param key       键
     * @param clazz     类型
     * @return 缓存值
     */
    public <T> T getCacheValue(String cacheName, String key, Class<T> clazz) {
        Object value = getCacheValue(cacheName, key);
        return convertValue(value, clazz);
    }

    /**
     * 获取缓存值
     *
     * @param cacheName     缓存名称
     * @param key           键
     * @param typeReference 类型
     * @return 缓存值
     */
    public <T> T getCacheValue(String cacheName, String key, TypeReference<T> typeReference) {
        Object value = getCacheValue(cacheName, key);
        return convertValue(value, typeReference);
    }

    /**
     * 获取缓存值
     *
     * @param key 键
     * @return 缓存值
     */
    public Object getCacheValue(String key) {
        return getRedisTemplate().opsForValue().get(getCacheKey(key));
    }

    /**
     * 获取缓存值
     *
     * @param key   键
     * @param clazz 类型
     * @return 缓存值
     */
    public <T> T getCacheValue(String key, Class<T> clazz) {
        Object value = getCacheValue(key);
        return convertValue(value, clazz);
    }

    /**
     * 获取缓存值
     *
     * @param key           键
     * @param typeReference 类型
     * @return 缓存值
     */
    public <T> T getCacheValue(String key, TypeReference<T> typeReference) {
        Object value = getCacheValue(key);
        return convertValue(value, typeReference);
    }

    /**
     * 获取所有缓存值
     *
     * @param cacheName 缓存名称
     * @return 缓存值列表
     */
    public List<Object> getCacheValueList(String cacheName) {
        Set<String> keys = getRedisTemplate().keys(getCacheKey(cacheName, "*"));
        if (keys == null) {
            return null;
        }
        return getRedisTemplate().opsForValue().multiGet(keys);
    }

    /**
     * 获取所有缓存值
     *
     * @param cacheName 缓存名称
     * @param clazz     类型
     * @return 缓存值列表
     */
    public <T> List<T> getCacheValueList(String cacheName, Class<T> clazz) {
        List<Object> values = getCacheValueList(cacheName);
        if (values == null) {
            return null;
        }
        return values.stream().map(value -> convertValue(value, clazz)).toList();
    }

    /**
     * 获取所有缓存值
     *
     * @param cacheName     缓存名称
     * @param typeReference 类型
     * @return 缓存值列表
     */
    public <T> List<T> getCacheValueList(String cacheName, TypeReference<T> typeReference) {
        List<Object> values = getCacheValueList(cacheName);
        if (values == null) {
            return null;
        }
        return values.stream().map(value -> convertValue(value, typeReference)).toList();
    }

    /**
     * 设置缓存值
     *
     * @param cacheName 缓存名称
     * @param key       键
     * @param value     值
     */
    public void setCacheValue(String cacheName, String key, Object value) {
        getRedisTemplate().opsForValue().set(getCacheKey(cacheName, key), value);
    }

    /**
     * 设置缓存值
     *
     * @param cacheName 缓存名称
     * @param key       键
     * @param value     值
     * @param timeout   超时时间
     * @param timeUnit  时间单位
     */
    public void setCacheValue(String cacheName, String key, Object value, long timeout, TimeUnit timeUnit) {
        getRedisTemplate().opsForValue().set(getCacheKey(cacheName, key), value, timeout, timeUnit);
    }

    /**
     * 设置缓存值
     *
     * @param key   键
     * @param value 值
     */
    public void setCacheValue(String key, Object value) {
        getRedisTemplate().opsForValue().set(getCacheKey(key), value);
    }

    /**
     * 设置缓存值
     *
     * @param key      键
     * @param value    值
     * @param timeout  超时时间
     * @param timeUnit 时间单位
     */
    public void setCacheValue(String key, Object value, long timeout, TimeUnit timeUnit) {
        getRedisTemplate().opsForValue().set(getCacheKey(key), value, timeout, timeUnit);
    }

    /**
     * 删除缓存值
     *
     * @param cacheName 缓存名称
     * @param key       键
     */
    public void deleteCacheValue(String cacheName, String key) {
        getRedisTemplate().delete(getCacheKey(cacheName, key));
    }

    /**
     * 删除缓存值
     *
     * @param key 键
     */
    public void deleteCacheValue(String key) {
        getRedisTemplate().delete(getCacheKey(key));
    }

    /**
     * 批量删除缓存
     *
     * @param cacheName 缓存名称
     * @return 删除的key数量
     */
    public long deleteCacheByPattern(String cacheName) {
        Set<String> keys = getRedisTemplate().keys(getCacheKey(cacheName, "*"));
        if (keys != null && !keys.isEmpty()) {
            return Objects.requireNonNull(getRedisTemplate().delete(keys));
        }
        return 0L;
    }

    /**
     * 获取锁
     *
     * @param lockName 锁名称
     * @param key      键
     * @return 锁
     */
    public boolean getLock(String lockName, String key) {
        return getRedissonClient().getLock(getLockKey(lockName, key)).tryLock();
    }

    /**
     * 获取锁
     *
     * @param key 键
     * @return 锁
     */
    public boolean getLock(String key) {
        return getRedissonClient().getLock(getLockKey(key)).tryLock();
    }

    /**
     * 获取带超时的分布式锁
     *
     * @param lockName  锁名称
     * @param key       键
     * @param waitTime  等待时间
     * @param leaseTime 租期时间
     * @param timeUnit  时间单位
     * @return 是否获取成功
     */
    public boolean getLock(String lockName, String key, long waitTime, long leaseTime, TimeUnit timeUnit) {
        try {
            RLock lock = getRedissonClient().getLock(getLockKey(lockName, key));
            return lock.tryLock(waitTime, leaseTime, timeUnit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 释放锁
     *
     * @param lockName 锁名称
     * @param key      键
     */
    public void releaseLock(String lockName, String key) {
        getRedissonClient().getLock(getLockKey(lockName, key)).unlock();
    }

    /**
     * 释放锁
     *
     * @param key 键
     */
    public void releaseLock(String key) {
        getRedissonClient().getLock(getLockKey(key)).unlock();
    }

    /**
     * 判断是否加锁
     *
     * @param lockName 锁名称
     * @param key      键
     * @return 是否加锁
     */
    public boolean isLocked(String lockName, String key) {
        return getRedissonClient().getLock(getLockKey(lockName, key)).isLocked();
    }

    /**
     * 判断是否加锁
     *
     * @param key 键
     * @return 是否加锁
     */
    public boolean isLocked(String key) {
        return getRedissonClient().getLock(getLockKey(key)).isLocked();
    }

    /**
     * 计数器增加
     *
     * @param counterName 计数器名称
     * @param key         键
     * @return 增加后的值
     */
    public Long incrementCounter(String counterName, String key) {
        if (Boolean.FALSE.equals(getRedisTemplate().hasKey(getCounterKey(counterName, key)))) {
            getRedisTemplate().opsForValue().set(getCounterKey(counterName, key), 0);
        }
        return getRedisTemplate().opsForValue().increment(getCounterKey(counterName, key));
    }

    /**
     * 计数器增加
     *
     * @param counterName 计数器名称
     * @param key         键
     * @param delta       增量
     * @return 增加后的值
     */
    public Long incrementCounter(String counterName, String key, long delta) {
        if (Boolean.FALSE.equals(getRedisTemplate().hasKey(getCounterKey(counterName, key)))) {
            getRedisTemplate().opsForValue().set(getCounterKey(counterName, key), 0);
        }
        return getRedisTemplate().opsForValue().increment(getCounterKey(counterName, key), delta);
    }

    /**
     * 计数器增加
     *
     * @param key 键
     * @return 增加后的值
     */
    public Long incrementCounter(String key) {
        if (Boolean.FALSE.equals(getRedisTemplate().hasKey(getCounterKey(key)))) {
            getRedisTemplate().opsForValue().set(getCounterKey(key), 0);
        }
        return getRedisTemplate().opsForValue().increment(getCounterKey(key));
    }

    /**
     * 计数器增加
     *
     * @param key   键
     * @param delta 增量
     * @return 增加后的值
     */
    public Long incrementCounter(String key, long delta) {
        if (Boolean.FALSE.equals(getRedisTemplate().hasKey(getCounterKey(key)))) {
            getRedisTemplate().opsForValue().set(getCounterKey(key), 0);
        }
        return getRedisTemplate().opsForValue().increment(getCounterKey(key), delta);
    }

    /**
     * 获取计数器当前值
     *
     * @param counterName 计数器名称
     * @param key         键
     * @return 当前值
     */
    public Long getCounterValue(String counterName, String key) {
        Object value = getRedisTemplate().opsForValue().get(getCounterKey(counterName, key));
        return value == null ? 0L : Convert.convert(Long.class, value);
    }

    /**
     * 获取缓存key
     *
     * @param cacheName 缓存名称
     * @param key       键
     * @return 缓存key
     */
    public String getCacheKey(String cacheName, String key) {
        return CACHE_PREFIX + cacheName + SEPARATOR + key;
    }

    /**
     * 获取缓存key
     *
     * @param key 键
     * @return 缓存key
     */
    public String getCacheKey(String key) {
        return CACHE_PREFIX + key;
    }

    /**
     * 获取锁key
     *
     * @param lockName 锁名称
     * @param key      键
     * @return 锁key
     */
    public String getLockKey(String lockName, String key) {
        return LOCK_PREFIX + lockName + SEPARATOR + key;
    }

    /**
     * 获取锁key
     *
     * @param key 键
     * @return 锁key
     */
    public String getLockKey(String key) {
        return LOCK_PREFIX + key;
    }

    /**
     * 获取计数器key
     *
     * @param key 键
     * @return 计数器key
     */
    public String getCounterKey(String key) {
        return COUNTER_PREFIX + key;
    }

    /**
     * 获取计数器key
     *
     * @param counterName 计数器名称
     * @param key         键
     * @return 计数器key
     */
    public String getCounterKey(String counterName, String key) {
        return COUNTER_PREFIX + counterName + SEPARATOR + key;
    }

    /**
     * 获取redis操作模板
     *
     * @return RedisTemplate 操作模板
     */
    private RedisTemplate<String, Object> getRedisTemplate() {
        return SpringUtil.getBean("redisTemplate");
    }

    /**
     * 获取redisson客户端
     *
     * @return RedissonClient 客户端
     */
    private RedissonClient getRedissonClient() {
        return SpringUtil.getBean(RedissonClient.class);
    }

    /**
     * 转换值
     *
     * @param value 值
     * @param clazz 类型
     * @param <T>   类型
     * @return 转换后的值
     */
    private <T> T convertValue(Object value, Class<T> clazz) {
        if (value == null) {
            return null;
        }
        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        }
        return Convert.convert(clazz, value);
    }

    /**
     * 转换值
     *
     * @param value         值
     * @param typeReference 类型
     * @param <T>           类型
     * @return 转换后的值
     */
    private <T> T convertValue(Object value, TypeReference<T> typeReference) {
        if (value == null) {
            return null;
        }
        return Convert.convert(typeReference, value);
    }
}
