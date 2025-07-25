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
 * <p>
 * 提供缓存、分布式锁、计数器等常用功能的封装，简化 Redis 操作。
 * 基于 Spring Data Redis 和 Redisson 实现，支持多种数据类型的缓存操作。
 * </p>
 *
 * <h3>主要功能</h3>
 * <ul>
 *   <li>缓存操作：支持字符串、对象、集合等类型的缓存</li>
 *   <li>分布式锁：基于 Redisson 实现的分布式锁机制</li>
 *   <li>计数器：原子性递增计数器功能</li>
 *   <li>批量操作：支持批量获取、删除等操作</li>
 * </ul>
 *
 * <h3>使用示例</h3>
 * <pre>{@code
 * // 缓存操作
 * RedisUtil.setCacheValue("user", "123", userObj);
 * User user = RedisUtil.getCacheValue("user", "123", User.class);
 *
 * // 分布式锁
 * if (RedisUtil.getLock("order", "123")) {
 *     try {
 *         // 业务逻辑
 *     } finally {
 *         RedisUtil.releaseLock("order", "123");
 *     }
 * }
 *
 * // 计数器
 * Long count = RedisUtil.incrementCounter("visit", "page1");
 * }</pre>
 *
 * @author george
 * @see org.springframework.data.redis.core.RedisTemplate
 * @see org.redisson.api.RedissonClient
 * @since 1.0.0
 */
@UtilityClass
public class RedisUtil {

    /**
     * 键名分隔符，用于组装复合键名
     */
    public static final String SEPARATOR = ":";

    /**
     * 分布式锁键名前缀，格式：athena:lock:{lockName}:{key}
     */
    private static final String LOCK_PREFIX = "athena:lock:";

    /**
     * 缓存键名前缀，格式：athena:cache:{cacheName}:{key}
     */
    private static final String CACHE_PREFIX = "athena:cache:";

    /**
     * 计数器键名前缀，格式：athena:counter:{counterName}:{key}
     */
    private static final String COUNTER_PREFIX = "athena:counter:";

    // ========== 缓存操作相关方法 ==========

    /**
     * 获取缓存值（Object类型）
     * <p>获取指定缓存名称和键对应的缓存值，返回原始 Object 类型</p>
     *
     * @param cacheName 缓存名称，用于分类管理不同业务的缓存
     * @param key       缓存键，业务唯一标识
     * @return 缓存值，如果不存在则返回 null
     * @throws IllegalArgumentException 如果 cacheName 或 key 为空
     */
    public Object getCacheValue(String cacheName, String key) {
        return getRedisTemplate().opsForValue().get(getCacheKey(cacheName, key));
    }

    /**
     * 获取缓存值并转换为指定类型
     * <p>获取缓存值并自动转换为指定的 Class 类型，适用于简单对象类型</p>
     *
     * @param <T>       返回值类型
     * @param cacheName 缓存名称
     * @param key       缓存键
     * @param clazz     目标类型的 Class 对象
     * @return 转��后的缓存值，如果不存在或转换失败则返回 null
     * @throws ClassCastException 如果类型转换失败
     */
    public <T> T getCacheValue(String cacheName, String key, Class<T> clazz) {
        Object value = getCacheValue(cacheName, key);
        return convertValue(value, clazz);
    }

    /**
     * 获取缓存值并转换为复杂类型
     * <p>获取缓存值并转换为复杂泛型类型，适用于 List、Map 等复杂类型</p>
     *
     * @param <T>           返回值类型
     * @param cacheName     缓存名称
     * @param key           缓存键
     * @param typeReference 类型引用，用于复杂泛型类型转换
     * @return 转换后的缓存值
     * @see cn.hutool.core.lang.TypeReference
     */
    public <T> T getCacheValue(String cacheName, String key, TypeReference<T> typeReference) {
        Object value = getCacheValue(cacheName, key);
        return convertValue(value, typeReference);
    }

    /**
     * 获取缓存值（简化版）
     * <p>直接使用 key 获取缓存值，不指定缓存名称</p>
     *
     * @param key 缓存键
     * @return 缓存值
     */
    public Object getCacheValue(String key) {
        return getRedisTemplate().opsForValue().get(getCacheKey(key));
    }

    /**
     * 获取缓存值并转换类型（简化版）
     *
     * @param <T>   返回值类型
     * @param key   缓存键
     * @param clazz 目标类型
     * @return 转换后的缓存值
     */
    public <T> T getCacheValue(String key, Class<T> clazz) {
        Object value = getCacheValue(key);
        return convertValue(value, clazz);
    }

    /**
     * 获取缓存值并转换复杂类型（简化版）
     *
     * @param <T>           返回值类型
     * @param key           缓存键
     * @param typeReference 类型引用
     * @return 转换后的缓存值
     */
    public <T> T getCacheValue(String key, TypeReference<T> typeReference) {
        Object value = getCacheValue(key);
        return convertValue(value, typeReference);
    }

    /**
     * 批量获取缓存值列表
     * <p>获取指定缓存名称下所有的缓存值，使用通配符匹配</p>
     *
     * @param cacheName 缓存名称
     * @return 缓存值列表，如果没有匹配的键则返回 null
     * @apiNote 该方法会使用 KEYS 命令，在生产环境中应谨慎使用
     */
    public List<Object> getCacheValueList(String cacheName) {
        Set<String> keys = getRedisTemplate().keys(getCacheKey(cacheName, "*"));
        if (keys == null) {
            return null;
        }
        return getRedisTemplate().opsForValue().multiGet(keys);
    }

    /**
     * 批量获取缓存值并转换类型
     *
     * @param <T>       返回值类型
     * @param cacheName 缓存名称
     * @param clazz     目标类型
     * @return 转换后的缓存值列表
     */
    public <T> List<T> getCacheValueList(String cacheName, Class<T> clazz) {
        List<Object> values = getCacheValueList(cacheName);
        if (values == null) {
            return null;
        }
        return values.stream().map(value -> convertValue(value, clazz)).toList();
    }

    /**
     * 批量获取缓存值并转换复杂类型
     *
     * @param <T>           返回值类型
     * @param cacheName     缓存名称
     * @param typeReference 类型引用
     * @return 转换后的缓存值列表
     */
    public <T> List<T> getCacheValueList(String cacheName, TypeReference<T> typeReference) {
        List<Object> values = getCacheValueList(cacheName);
        if (values == null) {
            return null;
        }
        return values.stream().map(value -> convertValue(value, typeReference)).toList();
    }

    /**
     * 设置缓存值（永不过期）
     * <p>将键值对存储到 Redis 中，不设置过期时间</p>
     *
     * @param cacheName 缓存名称
     * @param key       缓存键
     * @param value     缓存值，支持任意可序列化对象
     */
    public void setCacheValue(String cacheName, String key, Object value) {
        getRedisTemplate().opsForValue().set(getCacheKey(cacheName, key), value);
    }

    /**
     * 设置缓存值（带过期时间）
     * <p>将键值对存储到 Redis 中，并设置过期时间</p>
     *
     * @param cacheName 缓存名称
     * @param key       缓存键
     * @param value     缓存值
     * @param timeout   过期时间（数值）
     * @param timeUnit  时间单位（如：秒、分钟、小时等）
     */
    public void setCacheValue(String cacheName, String key, Object value, long timeout, TimeUnit timeUnit) {
        getRedisTemplate().opsForValue().set(getCacheKey(cacheName, key), value, timeout, timeUnit);
    }

    /**
     * 设置缓存值（简化版，永不过期）
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public void setCacheValue(String key, Object value) {
        getRedisTemplate().opsForValue().set(getCacheKey(key), value);
    }

    /**
     * 设置缓存值（简化版，带过期时间）
     *
     * @param key      缓存键
     * @param value    缓存值
     * @param timeout  过期时间
     * @param timeUnit 时间单位
     */
    public void setCacheValue(String key, Object value, long timeout, TimeUnit timeUnit) {
        getRedisTemplate().opsForValue().set(getCacheKey(key), value, timeout, timeUnit);
    }

    /**
     * 删除指定缓存
     * <p>删除指定缓存名称和键对应的缓存项</p>
     *
     * @param cacheName 缓存名称
     * @param key       缓存键
     * @return 删除是否成功
     */
    public void deleteCacheValue(String cacheName, String key) {
        getRedisTemplate().delete(getCacheKey(cacheName, key));
    }

    /**
     * 删除指定缓存（简化版）
     *
     * @param key 缓存键
     */
    public void deleteCacheValue(String key) {
        getRedisTemplate().delete(getCacheKey(key));
    }

    /**
     * 批量删除缓存
     * <p>删除指定缓存名称下的所有缓存项，使用通配符匹配</p>
     *
     * @param cacheName 缓存名称
     * @return 删除的键数量
     * @apiNote 该方法会使用 KEYS 命令，在生产环境中应谨慎使用
     */
    public long deleteCacheByPattern(String cacheName) {
        Set<String> keys = getRedisTemplate().keys(getCacheKey(cacheName, "*"));
        if (keys != null && !keys.isEmpty()) {
            return Objects.requireNonNull(getRedisTemplate().delete(keys));
        }
        return 0L;
    }

    // ========== 分布式锁相关方法 ==========

    /**
     * 尝试获取分布式锁（非阻塞）
     * <p>尝试获取分布式锁，如果锁已被占用则立即返回 false</p>
     *
     * @param lockName 锁名称，用于分类管理不同业务的锁
     * @param key      锁的业务键
     * @return true 表示获取锁成功，false 表示锁已被占用
     */
    public boolean getLock(String lockName, String key) {
        return getRedissonClient().getLock(getLockKey(lockName, key)).tryLock();
    }

    /**
     * 尝试获取分布式锁（简化版）
     *
     * @param key 锁键
     * @return 是否获取成功
     */
    public boolean getLock(String key) {
        return getRedissonClient().getLock(getLockKey(key)).tryLock();
    }

    /**
     * 获取带超时的分布式锁
     * <p>尝试在指定时间内获取锁，并设置锁的租期时间</p>
     *
     * @param lockName  锁名称
     * @param key       锁键
     * @param waitTime  等待获取锁的最大时间
     * @param leaseTime 锁的租期时间（自动释放时间）
     * @param timeUnit  时间单位
     * @return true 表示在指定时间内获取到锁，false 表示超时未获取到锁
     * @throws InterruptedException 如果等待过程中线程被中断
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
     * 释放分布式锁
     * <p>释放��前线程持有的分布式锁</p>
     *
     * @param lockName 锁名称
     * @param key      锁键
     * @throws IllegalMonitorStateException 如果当前线程未持有该锁
     */
    public void releaseLock(String lockName, String key) {
        getRedissonClient().getLock(getLockKey(lockName, key)).unlock();
    }

    /**
     * 释放分布式锁（简化版）
     *
     * @param key 锁键
     */
    public void releaseLock(String key) {
        getRedissonClient().getLock(getLockKey(key)).unlock();
    }

    /**
     * 检查锁是否被占用
     * <p>检查指定的锁是否正在被任何线程持有</p>
     *
     * @param lockName 锁名称
     * @param key      锁键
     * @return true 表示锁被占用，false 表示锁可用
     */
    public boolean isLocked(String lockName, String key) {
        return getRedissonClient().getLock(getLockKey(lockName, key)).isLocked();
    }

    /**
     * 检查锁是否被占用（简化版）
     *
     * @param key 锁键
     * @return 是否被锁定
     */
    public boolean isLocked(String key) {
        return getRedissonClient().getLock(getLockKey(key)).isLocked();
    }

    // ========== 计数器相关方法 ==========

    /**
     * 计数器递增（步长为1）
     * <p>对指定计数器进行原子性递增操作，如果计数器不存在则初始化为0后递增</p>
     *
     * @param counterName 计数器名称
     * @param key         计数器键
     * @return 递增后的值
     */
    public Long incrementCounter(String counterName, String key) {
        if (!getRedisTemplate().hasKey(getCounterKey(counterName, key))) {
            getRedisTemplate().opsForValue().set(getCounterKey(counterName, key), 0);
        }
        return getRedisTemplate().opsForValue().increment(getCounterKey(counterName, key));
    }

    /**
     * 计数器递增（自定义步长）
     * <p>对指定计数器进行原子性递增操作，支持自定义递增步长</p>
     *
     * @param counterName 计数器名称
     * @param key         计数器键
     * @param delta       递增步长（可以为负数实现递减）
     * @return 递增后的值
     */
    public Long incrementCounter(String counterName, String key, long delta) {
        if (!getRedisTemplate().hasKey(getCounterKey(counterName, key))) {
            getRedisTemplate().opsForValue().set(getCounterKey(counterName, key), 0);
        }
        return getRedisTemplate().opsForValue().increment(getCounterKey(counterName, key), delta);
    }

    /**
     * 计数器递增（简化版，步长为1）
     *
     * @param key 计数器键
     * @return 递增后的值
     */
    public Long incrementCounter(String key) {
        if (!getRedisTemplate().hasKey(getCounterKey(key))) {
            getRedisTemplate().opsForValue().set(getCounterKey(key), 0);
        }
        return getRedisTemplate().opsForValue().increment(getCounterKey(key));
    }

    /**
     * 计数器递增（简化版，自定义步长）
     *
     * @param key   计数器键
     * @param delta 递增步长
     * @return 递增后的值
     */
    public Long incrementCounter(String key, long delta) {
        if (!getRedisTemplate().hasKey(getCounterKey(key))) {
            getRedisTemplate().opsForValue().set(getCounterKey(key), 0);
        }
        return getRedisTemplate().opsForValue().increment(getCounterKey(key), delta);
    }

    /**
     * 获取计数器当前值
     * <p>获取指定计数器的当前值，如果计数器不存在则返回 0</p>
     *
     * @param counterName 计数器名称
     * @param key         计数器键
     * @return 计数器当前值，不存在时返回 0
     */
    public Long getCounterValue(String counterName, String key) {
        Object value = getRedisTemplate().opsForValue().get(getCounterKey(counterName, key));
        return value == null ? 0L : Convert.convert(Long.class, value);
    }

    // ========== 私有工具方法 ==========

    /**
     * 构建缓存键名
     * <p>根据缓存名称和键构建完整的 Redis 键名</p>
     *
     * @param cacheName 缓存名称
     * @param key       业务键
     * @return 完整的 Redis 键名，格式：athena:cache:{cacheName}:{key}
     */
    public String getCacheKey(String cacheName, String key) {
        return CACHE_PREFIX + cacheName + SEPARATOR + key;
    }

    /**
     * 构建缓存键名（简化版）
     *
     * @param key 业务键
     * @return 完整的 Redis 键名，格式：athena:cache:{key}
     */
    public String getCacheKey(String key) {
        return CACHE_PREFIX + key;
    }

    /**
     * 构建锁键名
     * <p>根据锁名称和键构建完整的 Redis 锁键名</p>
     *
     * @param lockName 锁名称
     * @param key      业务键
     * @return 完整的 Redis 锁键名，格式：athena:lock:{lockName}:{key}
     */
    public String getLockKey(String lockName, String key) {
        return LOCK_PREFIX + lockName + SEPARATOR + key;
    }

    /**
     * 构建锁键名（简化版）
     *
     * @param key 业务键
     * @return 完整的 Redis 锁键名，格式：athena:lock:{key}
     */
    public String getLockKey(String key) {
        return LOCK_PREFIX + key;
    }

    /**
     * 构建计数器键名
     * <p>根据计数器名称和键构建完整的 Redis 计数器键名</p>
     *
     * @param key 业务键
     * @return 完整的 Redis 计数器键名，格式：athena:counter:{key}
     */
    public String getCounterKey(String key) {
        return COUNTER_PREFIX + key;
    }

    /**
     * 构建计数器键名（带名称前缀）
     * <p>根据计数器名称和键构建完整的 Redis 计数器键名</p>
     *
     * @param counterName 计数器名称
     * @param key         业务键
     * @return 完整的 Redis 计数器键名，格式：athena:counter:{counterName}:{key}
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
