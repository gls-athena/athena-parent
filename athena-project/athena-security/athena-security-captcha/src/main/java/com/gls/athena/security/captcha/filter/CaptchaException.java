package com.gls.athena.security.captcha.filter;

import org.springframework.security.core.AuthenticationException;

/**
 * @author lizy19
 */
public class CaptchaException extends AuthenticationException {

    public CaptchaException(String message) {
        super(message);
    }

    public CaptchaException(String message, Throwable cause) {
        super(message, cause);
    }

}
