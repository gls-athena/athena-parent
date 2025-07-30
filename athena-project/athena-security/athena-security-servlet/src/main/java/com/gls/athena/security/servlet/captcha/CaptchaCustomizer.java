package com.gls.athena.security.servlet.captcha;

import com.gls.athena.security.servlet.captcha.repository.ICaptchaRepository;
import jakarta.annotation.Resource;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 验证码自定义器
 * 用于自定义验证码的配置，支持自定义存储、处理器、提供器等
 *
 * @author george
 */
@Component
public class CaptchaCustomizer implements Customizer<CaptchaConfigurer<HttpSecurity>> {

    /**
     * 验证码配置属性
     */
    @Resource
    private CaptchaProperties captchaProperties;

    /**
     * 验证码仓库，可选注入
     */
    @Resource
    private Optional<ICaptchaRepository> captchaRepository;

    /**
     * 认证失败处理器，可选注入
     */
    @Resource
    private Optional<AuthenticationFailureHandler> authenticationFailureHandler;

    /**
     * 自定义验证码提供器列表，可选注入
     */
    @Resource
    private Optional<List<CaptchaProvider<?>>> customProviders;

    /**
     * 验证码提供器定制器，可选注入
     */
    @Resource
    private Optional<Customizer<List<CaptchaProvider<?>>>> providersCustomizer;

    /**
     * 自定义验证码配置
     *
     * @param configurer 验证码配置器
     */
    @Override
    public void customize(CaptchaConfigurer<HttpSecurity> configurer) {
        // 配置验证码属性
        configurer.properties(captchaProperties);

        // 配置验证码仓库
        captchaRepository.ifPresent(configurer::captchaRepository);

        // 配置认证失败处理器
        authenticationFailureHandler.ifPresent(configurer::failureHandler);

        // 添加自定义验证码提供器
        customProviders.ifPresent(providers ->
                providers.forEach(configurer::addProvider));

        // 配置验证码提供器定制器
        providersCustomizer.ifPresent(configurer::providersCustomizer);
    }
}
