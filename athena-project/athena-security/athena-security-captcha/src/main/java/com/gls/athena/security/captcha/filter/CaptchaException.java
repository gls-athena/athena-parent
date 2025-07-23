package com.gls.athena.security.captcha.filter;

import org.springframework.security.core.AuthenticationException;

/**
 * 自定义验证码异常类，用于在验证码验证失败时抛出
 * 继承自Spring Security的AuthenticationException，以便于集成到Spring Security的异常处理机制中
 *
 * @author george
 */
public class CaptchaException extends AuthenticationException {

    /**
     * 构造函数，用于创建带有指定消息的CaptchaException对象
     *
     * @param message 异常消息，说明验证码验证失败的原因
     */
    public CaptchaException(String message) {
        super(message);
    }

    /**
     * 构造函数，用于创建带有指定消息和原因的CaptchaException对象
     *
     * @param message 异常消息，说明验证码验证失败的原因
     * @param cause   异常的原因，通常是一个下层异常
     */
    public CaptchaException(String message, Throwable cause) {
        super(message, cause);
    }

}
