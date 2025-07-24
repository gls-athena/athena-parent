package com.gls.athena.security.rest.configurer;

import cn.hutool.extra.spring.SpringUtil;
import com.gls.athena.security.rest.converter.MobileAuthenticationConverter;
import com.gls.athena.security.rest.converter.UsernamePasswordAuthenticationConverter;
import com.gls.athena.security.rest.filter.RestAuthenticationFilter;
import com.gls.athena.security.rest.provider.MobileAuthenticationProvider;
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
 * REST 登录配置器，用于配置 restful API 的登录认证
 *
 * @author lizy19
 */
public final class RestLoginConfigurer<H extends HttpSecurityBuilder<H>>
        extends AbstractAuthenticationFilterConfigurer<H, RestLoginConfigurer<H>, RestAuthenticationFilter> {
    /**
     * 认证转换器列表，用于将请求转换为认证对象
     */
    private final List<AuthenticationConverter> authenticationConverters = new ArrayList<>();
    /**
     * 认证提供者列表，用于处理具体的认证逻辑
     */
    private final List<AuthenticationProvider> authenticationProviders = new ArrayList<>();
    /**
     * 认证转换器消费者，用于在配置阶段处理认证转换器列表
     */
    private final Consumer<List<AuthenticationConverter>> authenticationConvertersConsumer = (authenticationConverters) -> {
    };
    /**
     * 认证提供者消费者，用于在配置阶段处理认证提供者列表
     */
    private final Consumer<List<AuthenticationProvider>> authenticationProvidersConsumer = (authenticationProviders) -> {
    };

    /**
     * 构造函数，初始化认证过滤器和默认登录处理URL
     */
    public RestLoginConfigurer() {
        super(new RestAuthenticationFilter(), "/rest/login");
    }

    /**
     * 创建并返回一个新的 RestLoginConfigurer 实例
     *
     * @return RestLoginConfigurer 实例
     */
    public static RestLoginConfigurer<HttpSecurity> restLogin() {
        return new RestLoginConfigurer<>();
    }

    /**
     * 添加认证转换器
     *
     * @param authenticationConverter 认证转换器实例
     * @return 当前配置器实例，支持链式调用
     */
    public RestLoginConfigurer<H> authenticationConverter(AuthenticationConverter authenticationConverter) {
        this.authenticationConverters.add(authenticationConverter);
        return this;
    }

    /**
     * 添加认证提供者
     *
     * @param authenticationProvider 认证提供者实例
     * @return 当前配置器实例，支持链式调用
     */
    public RestLoginConfigurer<H> authenticationProvider(AuthenticationProvider authenticationProvider) {
        this.authenticationProviders.add(authenticationProvider);
        return this;
    }

    @Override
    public RestLoginConfigurer<H> loginPage(String loginPage) {
        return super.loginPage(loginPage);
    }

    /**
     * 创建登录处理URL的请求匹配器
     *
     * @param loginProcessingUrl 登录处理的URL路径
     * @return 返回配置好的请求匹配器，用于匹配POST请求到指定的登录处理URL
     */
    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        // 使用默认的路径模式请求匹配器，配置为匹配指定URL的POST请求
        return PathPatternRequestMatcher.withDefaults()
                .matcher(HttpMethod.POST, loginProcessingUrl);
    }

    /**
     * 配置HTTP安全认证机制
     *
     * @param http HTTP安全配置对象，用于配置认证过滤器和认证提供者
     * @throws Exception 配置过程中可能抛出的异常
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
     *
     * @return 认证转换器列表，包含移动端认证转换器和用户名密码认证转换器
     */
    private List<AuthenticationConverter> createDefaultAuthenticationConverters() {
        // 初始化认证转换器列表
        List<AuthenticationConverter> authenticationConverters = new ArrayList<>();
        // 添加并配置移动端认证转换器
        authenticationConverters.add(new MobileAuthenticationConverter());
        // 添加并配置用户名密码认证转换器
        authenticationConverters.add(new UsernamePasswordAuthenticationConverter());
        return authenticationConverters;
    }

    /**
     * 创建默认的认证提供者列表
     *
     * @return 认证提供者列表，包含移动端认证提供者（如果用户详情服务可用）
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
