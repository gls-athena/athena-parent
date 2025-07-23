package com.gls.athena.security.captcha.provider;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 验证码服务管理器类，用于根据请求选择合适的验证码服务实现
 * <p>
 * 这个类维护了一个验证码服务提供者的列表，并提供方法来获取适当的验证码服务
 * 它可以根据HTTP请求来判断哪个验证码服务提供者是合适的
 *
 * @author george
 */
@Component
@RequiredArgsConstructor
public class CaptchaProviderManager {

    /**
     * 支持不同场景或类型的验证码服务列表
     * <p>
     * 这个列表包含了所有可用的验证码服务提供者，每个提供者都实现了CaptchaProvider接口
     */
    private final List<CaptchaProvider> captchaProviders;

    /**
     * 根据请求获取适当的验证码服务提供者
     * <p>
     * 此方法通过检查每个验证码服务提供者是否支持给定的HTTP请求来选择一个验证码服务提供者
     * 如果找到支持的提供者，则返回它；否则，返回null
     *
     * @param request HTTP请求对象，用于确定所需的验证码服务类型
     * @return CaptchaProvider接口的实现，用于生成验证码如果找不到合适的提供者，则返回null
     */
    public CaptchaProvider getCaptchaService(HttpServletRequest request) {
        return captchaProviders.stream()
                .filter(service -> service.supports(request))
                .findFirst()
                .orElse(null);
    }
}
