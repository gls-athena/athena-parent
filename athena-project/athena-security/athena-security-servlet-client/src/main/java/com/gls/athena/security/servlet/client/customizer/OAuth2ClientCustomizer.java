package com.gls.athena.security.servlet.client.customizer;

import com.gls.athena.security.servlet.client.delegate.DelegateAuthorizationCodeTokenResponseClient;
import com.gls.athena.security.servlet.client.delegate.DelegateAuthorizationRequestResolver;
import jakarta.annotation.Resource;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2ClientConfigurer;
import org.springframework.stereotype.Component;

/**
 * OAuth2 客户端配置自定义器
 * <p>
 * 用于自定义 Spring Security OAuth2 客户端的配置，包括授权请求解析和令牌响应处理等核心功能。
 * 实现了 {@link Customizer} 接口，可以被 Spring Security 配置链自动装配。
 *
 * @author george
 */
@Component
public class OAuth2ClientCustomizer implements Customizer<OAuth2ClientConfigurer<HttpSecurity>> {
    /**
     * 授权请求解析器
     * <p>
     * 负责处理和解析 OAuth2 授权请求，支持自定义授权请求的构建和处理逻辑
     */
    @Resource
    private DelegateAuthorizationRequestResolver authorizationRequestResolver;

    /**
     * 授权码令牌响应客户端
     * <p>
     * 负责处理授权码换取访问令牌的请求，支持自定义令牌请求的处理逻辑
     */
    @Resource
    private DelegateAuthorizationCodeTokenResponseClient accessTokenResponseClient;

    /**
     * 配置 OAuth2 客户端
     * <p>
     * 通过此方法配置 OAuth2 客户端的核心功能，包括授权码授权流程的自定义配置
     *
     * @param configurer OAuth2 客户端配置器
     */
    @Override
    public void customize(OAuth2ClientConfigurer<HttpSecurity> configurer) {
        configurer.authorizationCodeGrant(this::authorizationCodeGrant);
    }

    /**
     * 配置授权码授权流程
     * <p>
     * 设置自定义的授权请求解析器和令牌响应客户端，用于处理授权码授权流程中的请求解析和令牌获取
     *
     * @param configurer 授权码授权配置器
     */
    private void authorizationCodeGrant(OAuth2ClientConfigurer<HttpSecurity>.AuthorizationCodeGrantConfigurer configurer) {
        // 设置委托授权请求解析器
        configurer.authorizationRequestResolver(authorizationRequestResolver);
        // 设置委托授权码令牌响应客户端
        configurer.accessTokenResponseClient(accessTokenResponseClient);
    }
}
