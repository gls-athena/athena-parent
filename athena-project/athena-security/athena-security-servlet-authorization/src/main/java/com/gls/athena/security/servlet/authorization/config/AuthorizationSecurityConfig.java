package com.gls.athena.security.servlet.authorization.config;

import com.gls.athena.security.servlet.authorization.customizer.AuthorizationServerCustomizer;
import com.gls.athena.security.servlet.captcha.CaptchaConfigurer;
import com.gls.athena.security.servlet.captcha.CaptchaCustomizer;
import com.gls.athena.security.servlet.client.customizer.OAuth2LoginCustomizer;
import com.gls.athena.security.servlet.customizer.AuthorizeHttpRequestsCustomizer;
import com.gls.athena.security.servlet.customizer.CsrfCustomizer;
import com.gls.athena.security.servlet.customizer.ExceptionHandlingCustomizer;
import com.gls.athena.security.servlet.customizer.ResourceServerCustomizer;
import com.gls.athena.security.servlet.rest.RestConfigurer;
import com.gls.athena.security.servlet.rest.RestCustomizer;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 授权配置
 *
 * @author george
 */
@Configuration
public class AuthorizationSecurityConfig {

    /**
     * 授权安全过滤器链
     *
     * @param http                          Http安全
     * @param authorizationServerCustomizer OAuth2授权服务器自定义器
     * @param exceptionHandlingCustomizer   异常处理自定义器
     * @return SecurityFilterChain 安全过滤器链
     * @throws Exception 异常
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationSecurityFilterChain(HttpSecurity http,
                                                                AuthorizationServerCustomizer authorizationServerCustomizer,
                                                                ExceptionHandlingCustomizer exceptionHandlingCustomizer) throws Exception {
        // OAuth2授权服务器配置
        OAuth2AuthorizationServerConfigurer authorizationServer = OAuth2AuthorizationServerConfigurer.authorizationServer();
        // 授权服务器安全匹配
        http.securityMatcher(authorizationServer.getEndpointsMatcher());
        // 默认安全配置
        http.with(authorizationServer, authorizationServerCustomizer);
        // 异常处理
        http.exceptionHandling(exceptionHandlingCustomizer);
        // 构建
        return http.build();
    }

    /**
     * 默认安全过滤器链
     *
     * @param http                            Http安全
     * @param restCustomizer                  REST自定义器
     * @param captchaCustomizer               验证码自定义器
     * @param authorizeHttpRequestsCustomizer 请求授权自定义器
     * @param oauth2LoginCustomizer           OAuth2登录自定义器
     * @param resourceServerCustomizer        OAuth2资源服务器自定义器
     * @param csrfCustomizer                  CSRF自定义器
     * @return SecurityFilterChain 安全过滤器链
     * @throws Exception 异常
     */
    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http,
                                                          RestCustomizer restCustomizer,
                                                          CaptchaCustomizer captchaCustomizer,
                                                          AuthorizeHttpRequestsCustomizer authorizeHttpRequestsCustomizer,
                                                          OAuth2LoginCustomizer oauth2LoginCustomizer,
                                                          ResourceServerCustomizer resourceServerCustomizer,
                                                          CsrfCustomizer csrfCustomizer) throws Exception {
        // REST 登录
        http.with(new RestConfigurer<>(), restCustomizer);
        // 验证码
        http.with(new CaptchaConfigurer<>(), captchaCustomizer);
        // OAuth2 登录
        http.oauth2Login(oauth2LoginCustomizer);
        // 资源服务器
        http.oauth2ResourceServer(resourceServerCustomizer);
        // 配置请求授权
        http.authorizeHttpRequests(authorizeHttpRequestsCustomizer);
        // CSRF
        http.csrf(csrfCustomizer);
        // 构建
        return http.build();
    }
}
