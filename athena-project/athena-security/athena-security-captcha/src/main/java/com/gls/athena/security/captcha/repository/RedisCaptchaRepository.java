package com.gls.athena.security.captcha.repository;

import cn.hutool.core.date.DateUtil;
import com.gls.athena.security.captcha.domain.Captcha;
import com.gls.athena.starter.data.redis.support.RedisUtil;

import java.util.concurrent.TimeUnit;

/**
 * 使用Redis作为存储的验证码仓库实现类
 *
 * @author george
 */
public class RedisCaptchaRepository implements CaptchaRepository {

    /**
     * Redis中存储验证码的前缀
     * <p>
     * 用于区分其他类型的数据，避免键冲突
     * </p>
     */
    private static final String CAPTCHA_PREFIX = "captcha:";

    /**
     * 保存验证码到Redis
     *
     * @param key     验证码的键，通常与用户或会话相关联
     * @param captcha 验证码对象，包含验证码信息和过期时间
     */
    @Override
    public void saveCaptcha(String key, Captcha captcha) {
        // 计算验证码的剩余存活时间，并将验证码信息保存到Redis中
        RedisUtil.setCacheValue(CAPTCHA_PREFIX + key, captcha, DateUtil.betweenMs(DateUtil.date(), captcha.getExpireTime()), TimeUnit.MILLISECONDS);
    }

    /**
     * 从Redis获取验证码
     *
     * @param key 验证码的键，用于检索对应的验证码信息
     * @return 返回找到的验证码对象，如果不存在则返回null
     */
    @Override
    public Captcha getCaptcha(String key) {
        // 根据键从Redis中获取验证码信息，并转换为Captcha对象返回
        return RedisUtil.getCacheValue(CAPTCHA_PREFIX + key, Captcha.class);
    }

    /**
     * 从Redis移除验证码
     *
     * @param key 验证码的键，用于定位并删除对应的验证码信息
     */
    @Override
    public void removeCaptcha(String key) {
        // 删除Redis中与指定键关联的验证码信息
        RedisUtil.deleteCacheValue(CAPTCHA_PREFIX + key);
    }
}
