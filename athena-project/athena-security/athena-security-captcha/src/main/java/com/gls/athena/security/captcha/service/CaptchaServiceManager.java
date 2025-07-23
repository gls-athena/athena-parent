package com.gls.athena.security.captcha.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 验证码服务管理器类，用于根据请求选择合适的验证码服务实现
 *
 * @author george
 */
@Component
@RequiredArgsConstructor
public class CaptchaServiceManager {

    /**
     * 支持不同场景或类型的验证码服务列表
     */
    private final List<BaseCaptchaService<?>> captchaServices;

    /**
     * 根据请求获取最合适的验证码服务实现
     *
     * @param request HTTP请求对象，包含请求信息如URL、请求头等
     * @return 返回支持该请求的验证码服务实现，如果没有匹配的实现则返回null
     */
    public BaseCaptchaService<?> getCaptchaService(HttpServletRequest request) {
        // 遍历验证码服务列表，寻找支持当前请求的服务实现
        return captchaServices.stream()
                .filter(service -> service.supports(request))
                .findFirst()
                .orElse(null);
    }
}
