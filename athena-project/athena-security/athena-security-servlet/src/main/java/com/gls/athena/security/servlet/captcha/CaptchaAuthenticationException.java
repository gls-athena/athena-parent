package com.gls.athena.security.servlet.captcha;

import org.springframework.security.core.AuthenticationException;

/**
 * 验证码认证异常
 * <p>
 * 当验证码校验失败时抛出此异常，例如验证码错误、验证码过期等情况
 *
 * @author george
 * @since 1.0.0
 */
public class CaptchaAuthenticationException extends AuthenticationException {

    /**
     * 构造一个验证码认证异常
     *
     * @param message 异常信息
     */
    public CaptchaAuthenticationException(String message) {
        super(message);
    }

    /**
     * 构造一个验证码认证异常
     *
     * @param message 异常信息
     * @param cause   导致该异常的原因
     */
    public CaptchaAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
