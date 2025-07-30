package com.gls.athena.security.servlet.rest;

import cn.hutool.extra.spring.SpringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.DelegatingAuthenticationConverter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
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
     * 创建用于匹配登录处理URL的请求匹配器
     *
     * <p>
     * 该方法实现父类的抽象方法，用于创建一个Ant风格的路径请求匹配器，
     * 该匹配器将验证请求URL是否与指定的登录处理URL匹配，且请求方法是否为POST。
     * </p>
     *
     * @param loginProcessingUrl 需要进行匹配的登录处理URL路径，
     *                           通常为表单登录提交的目标地址
     * @return AntPathRequestMatcher 实例，用于匹配POST请求到指定URL的请求
     */
    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return PathPatternRequestMatcher.withDefaults()
                .matcher(HttpMethod.POST, loginProcessingUrl);
    }

    /**
     * 配置HTTP安全构建器
     * <p>
     * 主要功能：
     * 1. 设置默认的认证转换器，并允许自定义转换器优先执行
     * 2. 设置默认的认证提供者，并允许添加自定义提供者
     * 3. 调用父类配置完成过滤器链的最终配置
     *
     * @param http HTTP安全构建器，用于配置安全相关的HTTP设置
     * @throws Exception 可能抛出任何配置过程中出现的异常
     */
    @Override
    public void configure(H http) throws Exception {
        // 配置认证转换器：合并默认转换器和自定义转换器（自定义优先），并设置到认证过滤器中
        List<AuthenticationConverter> authenticationConverters = createDefaultAuthenticationConverters();
        if (!this.authenticationConverters.isEmpty()) {
            authenticationConverters.addAll(0, this.authenticationConverters);
        }
        this.authenticationConvertersConsumer.accept(authenticationConverters);
        getAuthenticationFilter().setAuthenticationConverter(new DelegatingAuthenticationConverter(authenticationConverters));

        // 配置认证提供者：合并默认提供者和自定义提供者，并注册到HTTP安全构建器中
        List<AuthenticationProvider> authenticationProviders = createDefaultAuthenticationProviders();
        if (!this.authenticationProviders.isEmpty()) {
            authenticationProviders.addAll(this.authenticationProviders);
        }
        this.authenticationProvidersConsumer.accept(authenticationProviders);
        authenticationProviders.forEach(
                (authenticationProvider) -> http.authenticationProvider(postProcess(authenticationProvider)));

        // 调用父类配置完成最终过滤器配置
        super.configure(http);
    }

    /**
     * 创建默认的认证转换器列表
     * <p>
     * 该方法创建并配置两个默认的认证转换器：
     * 1. MobileAuthenticationConverter - 用于移动端认证，配置手机号参数
     * 2. UsernamePasswordAuthenticationConverter - 用于用户名密码认证，配置用户名和密码参数
     * <p>
     * 注：所有配置参数均从restProperties中获取
     *
     * @return List<AuthenticationConverter> 包含两个默认认证转换器的列表：
     * - MobileAuthenticationConverter实例（已配置手机号参数）
     * - UsernamePasswordAuthenticationConverter实例（已配置用户名和密码参数）
     */
    private List<AuthenticationConverter> createDefaultAuthenticationConverters() {
        // 初始化认证转换器列表
        List<AuthenticationConverter> authenticationConverters = new ArrayList<>();

        // 添加并配置移动端认证转换器
        authenticationConverters.add(new MobileAuthenticationConverter()
                .setMobileParameter(restProperties.getMobileParameter()));

        // 添加并配置用户名密码认证转换器
        authenticationConverters.add(new UsernamePasswordAuthenticationConverter()
                .setUsernameParameter(restProperties.getUsernameParameter())
                .setPasswordParameter(restProperties.getPasswordParameter()));

        return authenticationConverters;
    }

    /**
     * 创建默认的认证提供者列表
     * <p>
     * 该方法会创建一个包含移动端认证提供者的默认认证提供者列表。
     * 首先从Spring容器中获取UserDetailsService实例，如果存在则创建
     * MobileAuthenticationProvider并添加到返回列表中。
     *
     * @return List<AuthenticationProvider> 包含移动端认证提供者的列表，
     * 如果UserDetailsService不存在则返回空列表
     */
    private List<AuthenticationProvider> createDefaultAuthenticationProviders() {
        // 初始化认证提供者列表
        List<AuthenticationProvider> authenticationProviders = new ArrayList<>();

        // 从Spring容器获取用户详情服务
        UserDetailsService userDetailsService = SpringUtil.getBean(UserDetailsService.class);

        // 如果用户详情服务存在，则创建移动端认证提供者并添加到列表
        if (userDetailsService != null) {
            authenticationProviders.add(new MobileAuthenticationProvider(userDetailsService));
        }

        return authenticationProviders;
    }

}
