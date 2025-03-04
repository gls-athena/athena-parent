package com.gls.athena.security.servlet.captcha;

import com.gls.athena.security.servlet.captcha.image.ImageCaptchaGenerator;
import com.gls.athena.security.servlet.captcha.image.ImageCaptchaSender;
import com.gls.athena.security.servlet.captcha.repository.ICaptchaRepository;
import com.gls.athena.security.servlet.captcha.repository.RedisCaptchaRepository;
import com.gls.athena.security.servlet.captcha.sms.SmsCaptchaGenerator;
import com.gls.athena.security.servlet.captcha.sms.SmsCaptchaSender;
import com.gls.athena.security.servlet.handler.DefaultAuthenticationFailureHandler;
import lombok.Getter;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * 验证码配置类
 * 集成Spring Security, 支持图形验证码和短信验证码的配置
 *
 * @param <H> HttpSecurityBuilder类型
 */
@Getter
public final class CaptchaConfigurer<H extends HttpSecurityBuilder<H>>
        extends AbstractHttpConfigurer<CaptchaConfigurer<H>, H> {

    /**
     * 验证码提供器列表
     */
    private final List<CaptchaProvider<?>> providers;

    /**
     * 验证码存储仓库
     */
    private ICaptchaRepository captchaRepository;

    /**
     * 认证失败处理器
     */
    private AuthenticationFailureHandler authenticationFailureHandler;

    /**
     * 验证码提供器配置接口
     */
    private Customizer<List<CaptchaProvider<?>>> providersCustomizer;

    /**
     * 验证码配置属性
     */
    private CaptchaProperties captchaProperties;

    /**
     * 构造函数，初始化默认配置
     */
    public CaptchaConfigurer() {
        this.providers = new ArrayList<>();
        this.captchaRepository = new RedisCaptchaRepository();
        this.authenticationFailureHandler = new DefaultAuthenticationFailureHandler();
        this.providersCustomizer = Customizer.withDefaults();
        this.captchaProperties = new CaptchaProperties();
    }

    /**
     * 创建验证码配置器实例
     */
    public static CaptchaConfigurer<HttpSecurity> captcha() {
        return new CaptchaConfigurer<>();
    }

    /**
     * 设置验证码存储器
     */
    public CaptchaConfigurer<H> captchaRepository(ICaptchaRepository captchaRepository) {
        Assert.notNull(captchaRepository, "captchaRepository cannot be null");
        this.captchaRepository = captchaRepository;
        return this;
    }

    /**
     * 设置认证失败处理器
     */
    public CaptchaConfigurer<H> failureHandler(AuthenticationFailureHandler failureHandler) {
        Assert.notNull(failureHandler, "failureHandler cannot be null");
        this.authenticationFailureHandler = failureHandler;
        return this;
    }

    /**
     * 设置验证码提供器配置
     */
    public CaptchaConfigurer<H> providersCustomizer(Customizer<List<CaptchaProvider<?>>> providersCustomizer) {
        Assert.notNull(providersCustomizer, "providersCustomizer cannot be null");
        this.providersCustomizer = providersCustomizer;
        return this;
    }

    /**
     * 设置验证码属性
     */
    public CaptchaConfigurer<H> properties(CaptchaProperties properties) {
        Assert.notNull(properties, "properties cannot be null");
        this.captchaProperties = properties;
        return this;
    }

    /**
     * 添加自定义验证码提供器
     */
    public CaptchaConfigurer<H> addProvider(CaptchaProvider<?> provider) {
        Assert.notNull(provider, "provider cannot be null");
        this.providers.add(provider);
        return this;
    }

    /**
     * 配置验证码过滤器
     */
    @Override
    public void configure(H builder) {
        List<CaptchaProvider<?>> allProviders = createProviders();
        providersCustomizer.customize(allProviders);

        CaptchaFilter captchaFilter = new CaptchaFilter(authenticationFailureHandler, allProviders);
        // 将验证码过滤器添加到用户名密码认证过滤器之前
        builder.addFilterBefore(postProcess(captchaFilter), UsernamePasswordAuthenticationFilter.class);
    }

    /**
     * 初始化验证码提供器
     */
    private List<CaptchaProvider<?>> createProviders() {
        List<CaptchaProvider<?>> allProviders = new ArrayList<>(providers);
        allProviders.add(createImageCaptchaProvider());
        allProviders.add(createSmsCaptchaProvider());
        return allProviders;
    }

    /**
     * 创建短信验证码提供器
     */
    private CaptchaProvider<?> createSmsCaptchaProvider() {
        CaptchaProperties.Sms sms = captchaProperties.getSms();
        return new CaptchaProvider<>(
                captchaRepository,
                new SmsCaptchaGenerator(sms.getLength(), sms.getExpireIn()),
                new SmsCaptchaSender(sms.getTemplateCode()),
                sms.getCodeParameterName(),
                sms.getTargetParameterName(),
                sms.getUrl(),
                sms.getUrls(),
                sms.getLoginProcessingUrl(),
                sms.getOauth2TokenUrl()
        );
    }

    /**
     * 创建图形验证码提供器
     */
    private CaptchaProvider<?> createImageCaptchaProvider() {
        CaptchaProperties.Image image = captchaProperties.getImage();
        return new CaptchaProvider<>(
                captchaRepository,
                new ImageCaptchaGenerator(
                        image.getLength(),
                        image.getExpireIn(),
                        image.getWidth(),
                        image.getHeight(),
                        image.getLineCount(),
                        image.getFontSize()
                ),
                new ImageCaptchaSender(),
                image.getCodeParameterName(),
                image.getTargetParameterName(),
                image.getUrl(),
                image.getUrls(),
                image.getLoginProcessingUrl(),
                image.getOauth2TokenUrl()
        );
    }
}
