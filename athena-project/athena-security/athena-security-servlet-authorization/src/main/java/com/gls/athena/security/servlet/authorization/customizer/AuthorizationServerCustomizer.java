package com.gls.athena.security.servlet.authorization.customizer;

import cn.hutool.core.bean.BeanUtil;
import com.gls.athena.common.bean.security.SocialUser;
import com.gls.athena.security.servlet.authorization.authentication.PasswordAuthenticationConverter;
import com.gls.athena.security.servlet.authorization.authentication.PasswordAuthenticationProvider;
import com.gls.athena.security.servlet.authorization.authentication.SmsAuthenticationConverter;
import com.gls.athena.security.servlet.authorization.authentication.SmsAuthenticationProvider;
import jakarta.annotation.Resource;
import org.springframework.security.config.Customizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.*;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.stereotype.Component;

import java.security.Principal;

/**
 * OAuth2授权服务器自定义器
 *
 * @author george
 */
@Component
public class AuthorizationServerCustomizer implements Customizer<OAuth2AuthorizationServerConfigurer> {
    /**
     * 用户详情认证提供者
     */
    @Resource
    private UserDetailsService userDetailsService;
    /**
     * 密码编码器
     */
    @Resource
    private PasswordEncoder passwordEncoder;
    /**
     * 授权服务
     */
    @Resource
    private OAuth2AuthorizationService authorizationService;
    /**
     * 令牌生成器
     */
    @Resource
    private OAuth2TokenGenerator<? extends OAuth2Token> oauth2TokenGenerator;

    /**
     * 自定义
     *
     * @param configurer 配置器
     */
    @Override
    public void customize(OAuth2AuthorizationServerConfigurer configurer) {
        // 定制OAuth2客户端认证
        configurer.clientAuthentication(this::clientAuthentication);
        // 定制授权服务器元数据端点
        configurer.authorizationServerMetadataEndpoint(this::authorizationServerMetadataEndpoint);
        // 定制授权端点
        configurer.authorizationEndpoint(this::authorizationEndpoint);
        // 定制令牌端点
        configurer.tokenEndpoint(this::tokenEndpoint);
        // 定制令牌验证端点
        configurer.tokenIntrospectionEndpoint(this::tokenIntrospectionEndpoint);
        // 定制令牌撤销端点
        configurer.tokenRevocationEndpoint(this::tokenRevocationEndpoint);
        // 定制设备授权端点
        configurer.deviceAuthorizationEndpoint(this::deviceAuthorizationEndpoint);
        // 定制设备验证端点
        configurer.deviceVerificationEndpoint(this::deviceVerificationEndpoint);
        // 定制OpenID Connect
        configurer.oidc(this::oidc);
    }

    /**
     * 客户端认证
     *
     * @param configurer 客户端认证配置器
     */
    private void clientAuthentication(OAuth2ClientAuthenticationConfigurer configurer) {
    }

    /**
     * 授权服务器元数据端点
     *
     * @param configurer 授权服务器元数据端点配置器
     */
    private void authorizationServerMetadataEndpoint(OAuth2AuthorizationServerMetadataEndpointConfigurer configurer) {
    }

    /**
     * 授权端点
     *
     * @param configurer 授权端点配置器
     */
    private void authorizationEndpoint(OAuth2AuthorizationEndpointConfigurer configurer) {
    }

    /**
     * 令牌端点
     *
     * @param configurer 令牌端点配置器
     */
    private void tokenEndpoint(OAuth2TokenEndpointConfigurer configurer) {

        // 添加密码模式
        configurer.authenticationProvider(new PasswordAuthenticationProvider(authorizationService, oauth2TokenGenerator, userDetailsService, passwordEncoder));
        configurer.accessTokenRequestConverter(new PasswordAuthenticationConverter());

        // 添加短信模式
        configurer.authenticationProvider(new SmsAuthenticationProvider(authorizationService, oauth2TokenGenerator, userDetailsService));
        configurer.accessTokenRequestConverter(new SmsAuthenticationConverter());
    }

    /**
     * 令牌验证端点
     *
     * @param configurer 令牌验证端点配置器
     */
    private void tokenIntrospectionEndpoint(OAuth2TokenIntrospectionEndpointConfigurer configurer) {
    }

    /**
     * 令牌撤销端点
     *
     * @param configurer 令牌撤销端点配置器
     */
    private void tokenRevocationEndpoint(OAuth2TokenRevocationEndpointConfigurer configurer) {
    }

    /**
     * 设备授权端点
     *
     * @param configurer 设备授权端点配置器
     */
    private void deviceAuthorizationEndpoint(OAuth2DeviceAuthorizationEndpointConfigurer configurer) {
    }

    /**
     * 设备验证端点
     *
     * @param configurer 设备验证端点配置器
     */
    private void deviceVerificationEndpoint(OAuth2DeviceVerificationEndpointConfigurer configurer) {
    }

    /**
     * OpenID Connect
     *
     * @param configurer OpenID Connect配置器
     */
    private void oidc(OidcConfigurer configurer) {
        // OIDC提供者配置端点自定义
        configurer.providerConfigurationEndpoint(this::oidcProviderConfigurationEndpoint);
        // OIDC登出端点自定义
        configurer.logoutEndpoint(this::oidcLogoutEndpoint);
        // OIDC客户端注册端点自定义
        configurer.clientRegistrationEndpoint(this::oidcClientRegistrationEndpoint);
        // OIDC用户信息端点自定义
        configurer.userInfoEndpoint(this::oidcUserInfoEndpoint);
    }

    /**
     * OIDC提供者配置端点自定义
     *
     * @param configurer OIDC提供者配置端点配置器
     */
    private void oidcProviderConfigurationEndpoint(OidcProviderConfigurationEndpointConfigurer configurer) {
    }

    /**
     * OIDC登出端点自定义
     *
     * @param configurer OIDC登出端点配置器
     */
    private void oidcLogoutEndpoint(OidcLogoutEndpointConfigurer configurer) {
    }

    /**
     * OIDC客户端注册端点自定义
     *
     * @param configurer OIDC客户端注册端点配置器
     */
    private void oidcClientRegistrationEndpoint(OidcClientRegistrationEndpointConfigurer configurer) {
    }

    /**
     * OIDC用户信息端点自定义
     *
     * @param configurer OIDC用户信息端点配置器
     */
    private void oidcUserInfoEndpoint(OidcUserInfoEndpointConfigurer configurer) {
        configurer.userInfoMapper(this::userInfoMapper);
    }

    /**
     * 用户信息映射
     *
     * @param context OIDC用户信息认证上下文
     * @return 用户信息
     */
    private OidcUserInfo userInfoMapper(OidcUserInfoAuthenticationContext context) {
        OAuth2Authorization oauth2Authorization = context.getAuthorization();
        Authentication authentication = oauth2Authorization.getAttribute(Principal.class.getName());
        assert authentication != null;
        Object principal = authentication.getPrincipal();
        if (principal instanceof SocialUser socialUser) {
            return new OidcUserInfo(BeanUtil.beanToMap(socialUser.getUser()));
        }
        return new OidcUserInfo(BeanUtil.beanToMap(principal));
    }

}
