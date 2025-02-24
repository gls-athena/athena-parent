package com.gls.athena.security.servlet.rest;

import cn.hutool.extra.spring.SpringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.DelegatingAuthenticationConverter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * REST 配置器
 *
 * @param <H> HTTP 安全构建器
 * @author george
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public final class RestConfigurer<H extends HttpSecurityBuilder<H>> extends
        AbstractAuthenticationFilterConfigurer<H, RestConfigurer<H>, RestAuthenticationFilter> {
    /**
     * 认证转换器
     */
    private final List<AuthenticationConverter> authenticationConverters = new ArrayList<>();
    /**
     * 认证提供者
     */
    private final List<AuthenticationProvider> authenticationProviders = new ArrayList<>();
    /**
     * REST 属性配置
     */
    private RestProperties restProperties = new RestProperties();
    /**
     * 认证转换器消费者
     */
    private Consumer<List<AuthenticationConverter>> authenticationConvertersConsumer = (authenticationConverters) -> {
    };
    /**
     * 认证提供者消费者
     */
    private Consumer<List<AuthenticationProvider>> authenticationProvidersConsumer = (authenticationProviders) -> {
    };

    /**
     * 构造函数
     */
    public RestConfigurer() {
        super(new RestAuthenticationFilter(), null);
    }

    /**
     * REST 自定义器
     *
     * @return REST 自定义器
     */
    public static RestConfigurer<HttpSecurity> rest() {
        return new RestConfigurer<>();
    }

    /**
     * 登录页面
     *
     * @param loginPage 登录页面
     * @return REST 配置器
     */
    @Override
    public RestConfigurer<H> loginPage(String loginPage) {
        return super.loginPage(loginPage);
    }

    /**
     * 添加认证转换器
     *
     * @param authenticationConverter 认证转换器
     * @return REST 配置器
     */
    public RestConfigurer<H> authenticationConverter(AuthenticationConverter authenticationConverter) {
        this.authenticationConverters.add(authenticationConverter);
        return this;
    }

    /**
     * 添加认证提供者
     *
     * @param authenticationProvider 认证提供者
     * @return REST 配置器
     */
    public RestConfigurer<H> authenticationProvider(AuthenticationProvider authenticationProvider) {
        this.authenticationProviders.add(authenticationProvider);
        return this;
    }

    /**
     * 设置登录处理 URL
     *
     * @param loginProcessingUrl 登录处理 URL
     *                           loginProcessingUrl 登录处理 URL
     * @return 请求匹配器
     */
    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return new AntPathRequestMatcher(loginProcessingUrl, "POST");
    }

    /**
     * 配置
     *
     * @param http HTTP 安全构建器
     * @throws Exception 异常
     */
    @Override
    public void configure(H http) throws Exception {
        // 设置默认的认证转换器
        List<AuthenticationConverter> authenticationConverters = createDefaultAuthenticationConverters();
        if (!this.authenticationConverters.isEmpty()) {
            authenticationConverters.addAll(0, this.authenticationConverters);
        }
        this.authenticationConvertersConsumer.accept(authenticationConverters);
        getAuthenticationFilter().setAuthenticationConverter(new DelegatingAuthenticationConverter(authenticationConverters));
        // 设置默认的认证提供者
        List<AuthenticationProvider> authenticationProviders = createDefaultAuthenticationProviders();
        if (!this.authenticationProviders.isEmpty()) {
            authenticationProviders.addAll(this.authenticationProviders);
        }
        this.authenticationProvidersConsumer.accept(authenticationProviders);
        authenticationProviders.forEach(
                (authenticationProvider) -> http.authenticationProvider(postProcess(authenticationProvider)));
        // 配置过滤器
        super.configure(http);
    }

    /**
     * 创建默认的认证转换器
     *
     * @return 认证转换器
     */
    private List<AuthenticationConverter> createDefaultAuthenticationConverters() {
        List<AuthenticationConverter> authenticationConverters = new ArrayList<>();
        authenticationConverters.add(new MobileAuthenticationConverter()
                .setMobileParameter(restProperties.getMobileParameter()));
        authenticationConverters.add(new UsernamePasswordAuthenticationConverter()
                .setUsernameParameter(restProperties.getUsernameParameter())
                .setPasswordParameter(restProperties.getPasswordParameter()));
        return authenticationConverters;
    }

    /**
     * 创建默认的认证提供者
     *
     * @return 认证提供者
     */
    private List<AuthenticationProvider> createDefaultAuthenticationProviders() {
        List<AuthenticationProvider> authenticationProviders = new ArrayList<>();
        UserDetailsService userDetailsService = SpringUtil.getBean(UserDetailsService.class);
        if (userDetailsService != null) {
            authenticationProviders.add(new MobileAuthenticationProvider(userDetailsService));
        }
        return authenticationProviders;
    }
}
