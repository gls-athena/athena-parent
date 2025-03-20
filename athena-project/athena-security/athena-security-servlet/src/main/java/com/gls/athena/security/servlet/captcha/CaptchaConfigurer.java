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
 * 该类用于集成Spring Security，支持图形验证码和短信验证码的配置。
 * 通过配置验证码提供器、验证码存储仓库、认证失败处理器等，实现验证码的生成、发送和验证功能。
 *
 * @param <H> HttpSecurityBuilder类型，表示Spring Security的HTTP安全配置构建器
 * @author george
 */
@Getter
public final class CaptchaConfigurer<H extends HttpSecurityBuilder<H>>
        extends AbstractHttpConfigurer<CaptchaConfigurer<H>, H> {

    /**
     * 验证码提供器列表
     * 用于存储和管理不同类型的验证码提供器，如图形验证码和短信验证码提供器。
     */
    private final List<CaptchaProvider<?>> providers;

    /**
     * 验证码存储仓库
     * 用于存储生成的验证码信息，默认使用Redis作为存储仓库。
     */
    private ICaptchaRepository captchaRepository;

    /**
     * 认证失败处理器
     * 当验证码验证失败时，调用该处理器进行相应的处理，默认使用DefaultAuthenticationFailureHandler。
     */
    private AuthenticationFailureHandler authenticationFailureHandler;

    /**
     * 验证码提供器配置接口
     * 允许用户自定义验证码提供器的配置，默认使用Customizer.withDefaults()进行初始化。
     */
    private Customizer<List<CaptchaProvider<?>>> providersCustomizer;

    /**
     * 验证码配置属性
     * 包含验证码相关的配置信息，如验证码长度、过期时间、图形验证码的宽度和高度等。
     */
    private CaptchaProperties captchaProperties;

    /**
     * 构造函数，初始化默认配置
     * 初始化验证码提供器列表、验证码存储仓库、认证失败处理器、验证码提供器配置接口和验证码配置属性。
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
     * 返回一个新的CaptchaConfigurer实例，用于配置验证码相关的功能。
     *
     * @return CaptchaConfigurer实例
     */
    public static CaptchaConfigurer<HttpSecurity> captcha() {
        return new CaptchaConfigurer<>();
    }

    /**
     * 设置验证码存储器
     * 允许用户自定义验证码存储仓库，用于存储生成的验证码信息。
     *
     * @param captchaRepository 验证码存储仓库实例
     * @return 当前CaptchaConfigurer实例，支持链式调用
     */
    public CaptchaConfigurer<H> captchaRepository(ICaptchaRepository captchaRepository) {
        Assert.notNull(captchaRepository, "captchaRepository cannot be null");
        this.captchaRepository = captchaRepository;
        return this;
    }

    /**
     * 设置认证失败处理器
     * 允许用户自定义验证码验证失败时的处理逻辑。
     *
     * @param failureHandler 认证失败处理器实例
     * @return 当前CaptchaConfigurer实例，支持链式调用
     */
    public CaptchaConfigurer<H> failureHandler(AuthenticationFailureHandler failureHandler) {
        Assert.notNull(failureHandler, "failureHandler cannot be null");
        this.authenticationFailureHandler = failureHandler;
        return this;
    }

    /**
     * 设置验证码提供器配置
     * 允许用户自定义验证码提供器的配置，如添加或修改验证码提供器。
     *
     * @param providersCustomizer 验证码提供器配置接口实例
     * @return 当前CaptchaConfigurer实例，支持链式调用
     */
    public CaptchaConfigurer<H> providersCustomizer(Customizer<List<CaptchaProvider<?>>> providersCustomizer) {
        Assert.notNull(providersCustomizer, "providersCustomizer cannot be null");
        this.providersCustomizer = providersCustomizer;
        return this;
    }

    /**
     * 设置验证码属性
     * 允许用户自定义验证码相关的配置属性，如验证码长度、过期时间等。
     *
     * @param properties 验证码配置属性实例
     * @return 当前CaptchaConfigurer实例，支持链式调用
     */
    public CaptchaConfigurer<H> properties(CaptchaProperties properties) {
        Assert.notNull(properties, "properties cannot be null");
        this.captchaProperties = properties;
        return this;
    }

    /**
     * 添加自定义验证码提供器
     * 允许用户添加自定义的验证码提供器，以支持更多类型的验证码。
     *
     * @param provider 验证码提供器实例
     * @return 当前CaptchaConfigurer实例，支持链式调用
     */
    public CaptchaConfigurer<H> addProvider(CaptchaProvider<?> provider) {
        Assert.notNull(provider, "provider cannot be null");
        this.providers.add(provider);
        return this;
    }

    /**
     * 配置验证码过滤器
     * 在Spring Security的HTTP安全配置中添加验证码过滤器，并在用户名密码认证过滤器之前执行。
     *
     * @param builder HttpSecurityBuilder实例，用于配置Spring Security的HTTP安全配置
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
     * 创建并返回包含默认和自定义验证码提供器的列表。
     *
     * @return 包含所有验证码提供器的列表
     */
    private List<CaptchaProvider<?>> createProviders() {
        List<CaptchaProvider<?>> allProviders = new ArrayList<>(providers);
        allProviders.add(createImageCaptchaProvider());
        allProviders.add(createSmsCaptchaProvider());
        return allProviders;
    }

    /**
     * 创建短信验证码提供器
     * 根据配置属性创建并返回短信验证码提供器。
     *
     * @return 短信验证码提供器实例
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
     * 根据配置属性创建并返回图形验证码提供器。
     *
     * @return 图形验证码提供器实例
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
