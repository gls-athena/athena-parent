package com.gls.athena.security.servlet.authorization.config;

import cn.hutool.core.collection.CollUtil;
import com.gls.athena.common.bean.security.Role;
import com.gls.athena.common.bean.security.User;
import com.gls.athena.security.servlet.authorization.support.IUserService;
import com.gls.athena.security.servlet.authorization.support.InMemoryUserService;
import com.gls.athena.security.servlet.authorization.support.RedisOAuth2AuthorizationConsentService;
import com.gls.athena.security.servlet.authorization.support.RedisOAuth2AuthorizationService;
import com.gls.athena.security.servlet.client.social.ISocialUserService;
import com.gls.athena.security.servlet.client.social.InMemorySocialUserService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.util.UUID;

/**
 * 默认认证配置
 *
 * @author george
 */
@AutoConfiguration
public class DefaultAuthConfig {

    /**
     * oauth2授权服务
     *
     * @return oauth2授权服务
     */
    @Bean
    @ConditionalOnMissingBean
    public OAuth2AuthorizationService authorizationService() {
        return new RedisOAuth2AuthorizationService();
    }

    /**
     * 授权同意服务
     *
     * @return 授权同意服务
     */
    @Bean
    @ConditionalOnMissingBean
    public OAuth2AuthorizationConsentService authorizationConsentService() {
        return new RedisOAuth2AuthorizationConsentService();
    }

    /**
     * 默认用户信息
     *
     * @return 用户信息
     */
    @Bean
    @ConditionalOnMissingBean
    public IUserService userService() {
        User user = new User();
        user.setUsername("admin");
        user.setPassword("{noop}admin");
        user.setMobile("13800000000");
        Role role = new Role();
        role.setCode("admin");
        role.setName("管理员");
        user.setRoles(CollUtil.newArrayList(role));
        return new InMemoryUserService(user);
    }

    /**
     * 注册客户端信息
     *
     * @return 注册客户端信息
     */
    @Bean
    @ConditionalOnMissingBean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient messagingClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("messaging-client")
                .clientSecret("{noop}secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .authorizationGrantType(IAuthorizationConstants.PASSWORD)
                .authorizationGrantType(IAuthorizationConstants.SMS)
                .redirectUri("http://localhost:8080/login/oauth2/code/messaging-client-oidc")
                .redirectUri("http://localhost:8080/authorized")
                .redirectUri("https://www.baidu.com")
                .postLogoutRedirectUri("http://localhost:8080/logged-out")
                .scope("openid")
                .scope("profile")
                .scope("message.read")
                .scope("message.write")
                .scope("user.read")
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .tokenSettings(TokenSettings.builder().accessTokenFormat(OAuth2TokenFormat.REFERENCE).build())
                .build();
        return new InMemoryRegisteredClientRepository(messagingClient);
    }

    /**
     * 社交用户服务
     *
     * @return 社交用户服务
     */
    @Bean
    @ConditionalOnMissingBean
    public ISocialUserService socialUserService() {
        return new InMemorySocialUserService();
    }
}
