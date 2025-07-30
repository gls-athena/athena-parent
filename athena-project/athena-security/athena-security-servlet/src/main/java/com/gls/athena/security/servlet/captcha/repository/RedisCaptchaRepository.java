package com.gls.athena.security.servlet.captcha.repository;

import cn.hutool.core.date.DateUtil;
import com.gls.athena.security.servlet.captcha.base.BaseCaptcha;
import com.gls.athena.starter.data.redis.support.RedisUtil;

import java.util.concurrent.TimeUnit;

/**
 * Redis验证码存储实现
 * 基于Redis实现验证码的存储、获取和删除操作
 *
 * @author george
 */
public class RedisCaptchaRepository implements ICaptchaRepository {
    /**
     * Redis缓存键前缀
     */
    private static final String CACHE_NAME = "captcha";

    /**
     * 将验证码保存到Redis中
     *
     * @param key     验证码唯一标识
     * @param captcha 验证码对象
     */
    @Override
    public void save(String key, BaseCaptcha captcha) {
        RedisUtil.setCacheValue(buildKey(key), captcha, DateUtil.betweenMs(DateUtil.date(), captcha.getExpireTime()), TimeUnit.MILLISECONDS);
    }

    /**
     * 从Redis中获取验证码
     *
     * @param key 验证码唯一标识
     * @return 验证码对象，如果不存在则返回null
     */
    @Override
    public BaseCaptcha get(String key) {
        return RedisUtil.getCacheValue(buildKey(key), BaseCaptcha.class);
    }

    /**
     * 从Redis中删除指定的验证码
     *
     * @param key 验证码唯一标识
     */
    @Override
    public void remove(String key) {
        RedisUtil.deleteCacheValue(buildKey(key));
    }

    /**
     * 构建Redis缓存键
     * 使用 CACHE_NAME 作为前缀，确保键的唯一性
     *
     * @param key 验证码标识
     * @return 完整的Redis缓存键
     */
    private String buildKey(String key) {
        return CACHE_NAME + RedisUtil.SEPARATOR + key;
    }
}
