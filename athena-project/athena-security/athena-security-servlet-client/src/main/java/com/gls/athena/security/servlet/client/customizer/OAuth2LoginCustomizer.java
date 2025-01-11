package com.gls.athena.security.servlet.client.customizer;

import com.gls.athena.security.servlet.client.delegate.DelegateAuthorizationCodeTokenResponseClient;
import com.gls.athena.security.servlet.client.delegate.DelegateAuthorizationRequestResolver;
import com.gls.athena.security.servlet.client.delegate.DelegateOAuth2UserService;
import com.gls.athena.security.servlet.rest.RestProperties;
import jakarta.annotation.Resource;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.stereotype.Component;

/**
 * OAuth2 登录配置自定义器
 * 用于自定义 OAuth2 登录流程中的各个环节，包括授权、令牌获取和用户信息获取等过程
 *
 * @author george
 */
@Component
public class OAuth2LoginCustomizer implements Customizer<OAuth2LoginConfigurer<HttpSecurity>> {

    @Resource
    private RestProperties restProperties;
    /**
     * 委托授权请求解析器
     */
    @Resource
    private DelegateAuthorizationRequestResolver authorizationRequestResolver;
    /**
     * 委托授权码令牌响应客户端
     */
    @Resource
    private DelegateAuthorizationCodeTokenResponseClient accessTokenResponseClient;
    /**
     * 委托 OAuth2 用户信息服务
     */
    @Resource
    private DelegateOAuth2UserService oauth2UserService;

    /**
     * 配置 OAuth2 登录流程
     * 包括登录页面、授权端点、重定向端点、令牌端点和用户信息端点的自定义配置
     *
     * @param configurer OAuth2 登录配置器
     */
    @Override
    public void customize(OAuth2LoginConfigurer<HttpSecurity> configurer) {
        // 登录页面
        configurer.loginPage(restProperties.getLoginPage());
        // 授权端点自定义器
        configurer.authorizationEndpoint(this::authorizationEndpoint);
        // 重定向端点自定义器
        configurer.redirectionEndpoint(this::redirectionEndpoint);
        // 令牌端点自定义器
        configurer.tokenEndpoint(this::tokenEndpoint);
        // 用户信息端点自定义器
        configurer.userInfoEndpoint(this::userInfoEndpoint);
    }

    /**
     * 配置 OAuth2 授权端点
     * 设置自定义的授权请求解析器，用于处理授权请求的生成和解析
     *
     * @param config 授权端点配置器
     */
    private void authorizationEndpoint(OAuth2LoginConfigurer<HttpSecurity>.AuthorizationEndpointConfig config) {
        // 自定义 OAuth2 授权请求解析器
        config.authorizationRequestResolver(authorizationRequestResolver);
    }

    /**
     * 配置 OAuth2 重定向端点
     * 处理授权服务器回调的配置
     *
     * @param config 重定向端点配置器
     */
    private void redirectionEndpoint(OAuth2LoginConfigurer<HttpSecurity>.RedirectionEndpointConfig config) {
    }

    /**
     * 配置 OAuth2 令牌端点
     * 设置自定义的令牌响应客户端，用于处理访问令牌的请求和响应
     *
     * @param config 令牌端点配置器
     */
    private void tokenEndpoint(OAuth2LoginConfigurer<HttpSecurity>.TokenEndpointConfig config) {
        // 自定义 OAuth2 授权码令牌响应客户端
        config.accessTokenResponseClient(accessTokenResponseClient);
    }

    /**
     * 配置 OAuth2 用户信息端点
     * 设置自定义的用户信息服务，用于获取和处理用户详细信息
     *
     * @param config 用户信息端点配置器
     */
    private void userInfoEndpoint(OAuth2LoginConfigurer<HttpSecurity>.UserInfoEndpointConfig config) {
        // 自定义 OAuth2 用户信息服务
        config.userService(oauth2UserService);
    }
}
