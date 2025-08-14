package com.gls.athena.starter.data.redis.support;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

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
@Slf4j
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
        validateParameters(cacheName, "cacheName");
        validateParameters(key, "key");

        try {
            return getRedisTemplate().opsForValue().get(getCacheKey(cacheName, key));
        } catch (Exception e) {
            log.error("Failed to get cache value for cacheName: {}, key: {}", cacheName, key, e);
            return null;
        }
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
        validateParameters(cacheName, "cacheName");

        String pattern = getCacheKey(cacheName, "*");
        Set<String> keys = new HashSet<>();

        // 使用SCAN命令替代KEYS命令，修复废弃API问题
        getRedisTemplate().execute((RedisCallback<Void>) connection -> {
            try (Cursor<byte[]> cursor = connection.keyCommands().scan(ScanOptions.scanOptions()
                    .match(pattern)
                    .count(100)
                    .build())) {
                while (cursor.hasNext()) {
                    keys.add(new String(cursor.next()));
                }
            } catch (Exception e) {
                log.error("Failed to scan cache keys with pattern: {}", pattern, e);
            }
            return null;
        });

        if (keys.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            List<Object> values = getRedisTemplate().opsForValue().multiGet(keys);
            return values != null ? values : new ArrayList<>();
        } catch (Exception e) {
            log.error("Failed to get multiple cache values for keys: {}", keys, e);
            return new ArrayList<>();
        }
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
        validateParameters(cacheName, "cacheName");

        String pattern = getCacheKey(cacheName, "*");
        AtomicLong deletedCount = new AtomicLong(0);

        getRedisTemplate().execute((RedisCallback<Void>) connection -> {
            try (Cursor<byte[]> cursor = connection.keyCommands().scan(ScanOptions.scanOptions()
                    .match(pattern)
                    .count(100)
                    .build())) {

                List<String> batch = new ArrayList<>();
                while (cursor.hasNext()) {
                    batch.add(new String(cursor.next()));

                    // 分批删除，每批100个键
                    if (batch.size() >= 100) {
                        Long deleted = getRedisTemplate().delete(batch);
                        deletedCount.addAndGet(deleted != null ? deleted : 0);
                        batch.clear();
                    }
                }

                // 删除剩余的键
                if (!batch.isEmpty()) {
                    Long deleted = getRedisTemplate().delete(batch);
                    deletedCount.addAndGet(deleted != null ? deleted : 0);
                }
            } catch (Exception e) {
                log.error("Failed to delete cache by pattern: {}", pattern, e);
            }
            return null;
        });

        return deletedCount.get();
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
        validateParameters(lockName, "lockName");
        validateParameters(key, "key");

        try {
            RLock lock = getRedissonClient().getLock(getLockKey(lockName, key));
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            } else {
                log.warn("Attempt to release lock not held by current thread: {}:{}", lockName, key);
            }
        } catch (Exception e) {
            log.error("Failed to release lock: {}:{}", lockName, key, e);
        }
    }

    /**
     * 释放分布式锁（简化版，安全版本）
     *
     * @param key 锁键
     */
    public void releaseLock(String key) {
        validateParameters(key, "key");

        try {
            RLock lock = getRedissonClient().getLock(getLockKey(key));
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            } else {
                log.warn("Attempt to release lock not held by current thread: {}", key);
            }
        } catch (Exception e) {
            log.error("Failed to release lock: {}", key, e);
        }
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
     * 计数器递增（步长为1）优化版本
     * <p>对指定计数器进行原子性递增操作，Redis会自动处理不存在的键</p>
     *
     * @param counterName 计数器名称
     * @param key         计数器键
     * @return 递增后的值
     */
    public Long incrementCounter(String counterName, String key) {
        return getRedisTemplate().opsForValue().increment(getCounterKey(counterName, key));
    }

    /**
     * 计数器递增（自定义步长）优化版本
     * <p>对指定计数器进行原子性递增操作，支持自定义递增步长</p>
     *
     * @param counterName 计数器名称
     * @param key         计数器键
     * @param delta       递增步长（可以为负数实现递减）
     * @return 递增后的值
     */
    public Long incrementCounter(String counterName, String key, long delta) {
        return getRedisTemplate().opsForValue().increment(getCounterKey(counterName, key), delta);
    }

    /**
     * 计数器递增（简化版，步长为1）优化版本
     *
     * @param key 计数器键
     * @return 递增后的值
     */
    public Long incrementCounter(String key) {
        return getRedisTemplate().opsForValue().increment(getCounterKey(key));
    }

    /**
     * 计数器递增（简化版，自定义步长）优化版本
     *
     * @param key   计数器键
     * @param delta 递增步长
     * @return 递增后的值
     */
    public Long incrementCounter(String key, long delta) {
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

    // ========== 缓存过期时间管理 ==========

    /**
     * 设置缓存键的过期时间
     *
     * @param cacheName 缓存名称
     * @param key       缓存键
     * @param timeout   过期时间
     * @param timeUnit  时间单位
     * @return 是否设置成功
     */
    public boolean expireCacheValue(String cacheName, String key, long timeout, TimeUnit timeUnit) {
        validateParameters(cacheName, "cacheName");
        validateParameters(key, "key");

        try {
            return getRedisTemplate().expire(getCacheKey(cacheName, key), timeout, timeUnit);
        } catch (Exception e) {
            log.error("Failed to set expiration for cache: {}:{}", cacheName, key, e);
            return false;
        }
    }

    /**
     * 设置缓存键的过期时间（简化版）
     *
     * @param key      缓存键
     * @param timeout  过期时间
     * @param timeUnit 时间单位
     * @return 是否设置成功
     */
    public boolean expireCacheValue(String key, long timeout, TimeUnit timeUnit) {
        validateParameters(key, "key");

        try {
            return getRedisTemplate().expire(getCacheKey(key), timeout, timeUnit);
        } catch (Exception e) {
            log.error("Failed to set expiration for cache: {}", key, e);
            return false;
        }
    }

    /**
     * 获取缓存键的剩余过期时间
     *
     * @param cacheName 缓存名称
     * @param key       缓存键
     * @param timeUnit  时间单位
     * @return 剩余过期时间，-1表示永不过期，-2表示键不存在
     */
    public long getCacheExpireTime(String cacheName, String key, TimeUnit timeUnit) {
        validateParameters(cacheName, "cacheName");
        validateParameters(key, "key");

        try {
            return getRedisTemplate().getExpire(getCacheKey(cacheName, key), timeUnit);
        } catch (Exception e) {
            log.error("Failed to get expiration time for cache: {}:{}", cacheName, key, e);
            return -2;
        }
    }

    /**
     * 检查缓存键是否存在
     *
     * @param cacheName 缓存名称
     * @param key       缓存键
     * @return 是否存在
     */
    public boolean hasCacheKey(String cacheName, String key) {
        validateParameters(cacheName, "cacheName");
        validateParameters(key, "key");

        try {
            return getRedisTemplate().hasKey(getCacheKey(cacheName, key));
        } catch (Exception e) {
            log.error("Failed to check cache key existence: {}:{}", cacheName, key, e);
            return false;
        }
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
     * 设置缓存表中的行数据
     *
     * @param tableName 缓存表名称，用于标识要操作的缓存表
     * @param rowId     行ID，用于唯一标识缓存表中的一行数据
     * @param row       要存储的行数据对象
     * @throws IllegalArgumentException 当cacheName或rowId为null时抛出异常
     */
    public void setCacheTableRow(String tableName, String rowId, Object row) {

        // 使用Redis的Hash数据结构存储缓存表行数据
        getRedisTemplate().opsForHash().put(getCacheKey(tableName), rowId, row);
    }

    /**
     * 从缓存中获取指定行的数据
     *
     * @param tableName 缓存名称，用于标识不同的缓存区域
     * @param rowId     行ID，用于唯一标识缓存中的某一行数据
     * @param clazz     返回值的类型Class对象
     * @return 指定类型的缓存行数据对象
     */
    public <T> T getCacheTableRow(String tableName, String rowId, Class<T> clazz) {
        // 从Redis Hash结构中获取指定缓存名称和行ID的数据
        Object row = getRedisTemplate().opsForHash().get(getCacheKey(tableName), rowId);
        // 将获取到的数据转换为指定类型并返回
        return convertValue(row, clazz);
    }

    /**
     * 从缓存中获取指定行的数据
     *
     * @param tableName     缓存表名称，用于标识要操作的缓存表
     * @param rowId         行ID，对应Redis hash中的字段名
     * @param typeReference 返回值的类型引用，用于类型转换
     * @return 指定类型的缓存数据对象
     */
    public <T> T getCacheTableRow(String tableName, String rowId, TypeReference<T> typeReference) {
        // 从Redis hash中获取指定字段的值
        Object row = getRedisTemplate().opsForHash().get(getCacheKey(tableName), rowId);
        // 将获取到的值转换为目标类型并返回
        return convertValue(row, typeReference);
    }

    /**
     * 删除缓存表中指定行的数据
     *
     * @param tableName 缓存表名称，用于标识要操作的缓存空间
     * @param rowId     行ID，用于标识要删除的具体缓存项
     */
    public void deleteCacheTableRow(String tableName, String rowId) {
        // 从Redis中删除指定缓存表的指定行数据
        getRedisTemplate().opsForHash().delete(getCacheKey(tableName), rowId);
    }

    /**
     * 从指定缓存中获取所有行数据并转换为指定类型的列表
     *
     * @param tableName 缓存名称，用于标识要获取数据的缓存
     * @param clazz     目标数据类型，用于将缓存中的数据转换为指定类型
     * @return 转换后的数据列表，如果缓存为空则返回空列表
     */
    public <T> List<T> getCacheTableRows(String tableName, Class<T> clazz) {
        // 从Redis Hash中获取指定缓存的所有键值对
        Map<Object, Object> rows = getRedisTemplate().opsForHash().entries(getCacheKey(tableName));
        // 将缓存中的值转换为目标类型并收集为列表
        return rows.values().stream().map(row -> convertValue(row, clazz)).collect(Collectors.toList());
    }

    /**
     * 从指定缓存中获取所有行数据并转换为指定类型的列表
     *
     * @param tableName     缓存名称，用于标识要获取数据的缓存
     * @param typeReference 目标类型引用，用于指定返回列表中元素的类型
     * @return 转换后的对象列表，包含缓存中所有行数据
     */
    public <T> List<T> getCacheTableRows(String tableName, TypeReference<T> typeReference) {
        // 从Redis Hash中获取指定缓存的所有键值对
        Map<Object, Object> rows = getRedisTemplate().opsForHash().entries(getCacheKey(tableName));
        // 将所有值转换为目标类型并收集为列表
        return rows.values().stream().map(row -> convertValue(row, typeReference)).collect(Collectors.toList());
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

    /**
     * 参数校验
     *
     * @param parameter 参数值
     * @param paramName 参数名称
     * @throws IllegalArgumentException 当参数为空时抛出异常
     */
    private void validateParameters(String parameter, String paramName) {
        if (StrUtil.isBlank(parameter)) {
            throw new IllegalArgumentException(paramName + " cannot be null or empty");
        }
    }
}
