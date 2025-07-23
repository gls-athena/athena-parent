/**
 * 验证码存储接口
 * 用于定义验证码的存储和检索操作
 */
package com.gls.athena.security.captcha.repository;

import com.gls.athena.security.captcha.domain.Captcha;

/**
 * CaptchaRepository接口定义了验证码的存储和检索方法
 * 它提供了保存、获取和移除验证码的功能
 *
 * @author george
 */
public interface CaptchaRepository {

    /**
     * 保存验证码
     *
     * @param key     验证码的键，用于后续的检索
     * @param captcha 要保存的验证码对象，包含验证码的详细信息
     */
    void saveCaptcha(String key, Captcha captcha);

    /**
     * 获取验证码
     *
     * @param key 验证码的键，用于检索特定的验证码对象
     * @return 返回找到的验证码对象，如果未找到则返回null
     */
    Captcha getCaptcha(String key);

    /**
     * 移除验证码
     *
     * @param key 要移除的验证码的键
     *            指定的键对应的验证码对象将会从存储中删除
     */
    void removeCaptcha(String key);
}
