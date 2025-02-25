package com.gls.athena.security.servlet.captcha.repository;

import com.gls.athena.security.servlet.captcha.base.BaseCaptcha;

/**
 * 验证码仓储接口
 * <p>
 * 提供验证码的存储、获取和删除等基础操作。实现类可以基于不同的存储介质(如内存、Redis等)
 * 来提供具体的验证码存储实现。
 *
 * @author george
 * @since 1.0
 */
public interface ICaptchaRepository {

    /**
     * 保存验证码
     *
     * @param key     验证码唯一标识，用于后续获取和验证
     * @param captcha 待保存的验证码对象，包含验证码的内容和相关属性
     * @throws IllegalArgumentException 当key或captcha为null时抛出
     */
    void save(String key, BaseCaptcha captcha);

    /**
     * 根据key获取验证码
     *
     * @param key 验证码唯一标识
     * @return 返回与key关联的验证码对象，如果不存在则返回null
     * @throws IllegalArgumentException 当key为null时抛出
     */
    BaseCaptcha get(String key);

    /**
     * 移除指定的验证码
     *
     * @param key 待移除验证码的唯一标识
     * @throws IllegalArgumentException 当key为null时抛出
     */
    void remove(String key);
}
