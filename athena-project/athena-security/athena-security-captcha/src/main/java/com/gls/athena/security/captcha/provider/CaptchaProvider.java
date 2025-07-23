package com.gls.athena.security.captcha.provider;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 验证码提供者接口，定义了验证码处理的相关操作
 *
 * @author george
 */
public interface CaptchaProvider {

    /**
     * 判断当前请求是否支持验证码处理
     * 支持的请求类型包括发送验证码、验证验证码和验证码类型请求
     *
     * @param request HTTP请求对象
     * @return 如果支持则返回true，否则返回false
     */
    default boolean supports(HttpServletRequest request) {
        return isSendCaptchaRequest(request) || isValidateCaptchaRequest(request) || isCaptchaTypeRequest(request);
    }

    /**
     * 判断当前请求是否为验证码类型请求
     *
     * @param request HTTP请求对象
     * @return 如果是验证码类型请求则返回true，否则返回false
     */
    boolean isCaptchaTypeRequest(HttpServletRequest request);

    /**
     * 判断当前请求是否为发送验证码请求
     *
     * @param request HTTP请求对象
     * @return 如果是发送验证码请求则返回true，否则返回false
     */
    boolean isSendCaptchaRequest(HttpServletRequest request);

    /**
     * 判断当前请求是否为验证验证码请求
     *
     * @param request HTTP请求对象
     * @return 如果是验证验证码请求则返回true，否则返回false
     */
    boolean isValidateCaptchaRequest(HttpServletRequest request);

    /**
     * 发送验证码给客户端
     *
     * @param request  HTTP请求对象
     * @param response HTTP响应对象
     */
    void sendCaptcha(HttpServletRequest request, HttpServletResponse response);

    /**
     * 验证客户端提供的验证码是否有效
     *
     * @param request HTTP请求对象
     */
    void validateCaptcha(HttpServletRequest request);
}
