package com.gls.athena.security.servlet.client.support;

import com.gls.athena.security.servlet.client.config.IClientConstants;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * OAuth2 身份提供者(IdP)配置类
 * 用于配置 OAuth2 客户端注册时所需的基本参数,包括:
 * - 认证方式
 * - 授权方式
 * - 端点URL
 * - 授权范围等
 * 
 * @author george
 */
@Data
@Accessors(chain = true)
public class ProviderConfiguration {

    /**
     * 默认重定向URL模板
     * {baseUrl}: 应用基础URL
     * {action}: 动作标识符
     * {registrationId}: OAuth2客户端注册ID
     */
    private static final String DEFAULT_REDIRECT_URL = "{baseUrl}/{action}/oauth2/code/{registrationId}";

    /**
     * 客户端认证方法
     * 默认使用客户端密钥基本认证(Basic Authentication)
     */
    private ClientAuthenticationMethod clientAuthenticationMethod = ClientAuthenticationMethod.CLIENT_SECRET_BASIC;

    /**
     * 授权类型
     * 默认使用授权码模式(Authorization Code)
     */
    private AuthorizationGrantType authorizationGrantType = AuthorizationGrantType.AUTHORIZATION_CODE;

    /**
     * OAuth2回调地址
     * 默认使用标准的重定向URL模板
     */
    private String redirectUri = DEFAULT_REDIRECT_URL;

    /**
     * 身份提供者的唯一标识符
     * 用于在多个提供者场景下区分不同的IdP
     */
    private String providerId;

    /**
     * OAuth2客户端应用名称
     */
    private String clientName;

    /**
     * OAuth2授权端点地址
     * 用户将被重定向到此地址进行身份认证
     */
    private String authorizationUri;

    /**
     * OAuth2访问令牌端点地址
     * 用于获取访问令牌和刷新令牌
     */
    private String tokenUri;

    /**
     * OAuth2用户信息端点地址
     * 用于获取经过身份验证的用户详细信息
     */
    private String userInfoUri;

    /**
     * 用户标识属性名
     * 指定从用户信息响应中提取用户唯一标识的属性
     */
    private String userNameAttributeName;

    /**
     * OAuth2授权范围集合
     * 定义客户端请求的访问权限范围
     */
    private Set<String> scopes;

    /**
     * 提供者相关的扩展配置
     * 用于存储不在标准OAuth2规范中的自定义配置项
     */
    private Map<String, Object> metadata;

    /**
     * 创建OAuth2客户端注册构建器
     * 
     * @param registrationId 客户端注册ID,用于唯一标识一个OAuth2客户端注册
     * @return 预配置的ClientRegistration.Builder实例
     */
    public ClientRegistration.Builder createBuilder(String registrationId) {
        ClientRegistration.Builder builder = ClientRegistration.withRegistrationId(registrationId)
                .clientAuthenticationMethod(clientAuthenticationMethod)
                .authorizationGrantType(authorizationGrantType)
                .redirectUri(redirectUri)
                .clientName(clientName)
                .authorizationUri(authorizationUri)
                .tokenUri(tokenUri)
                .userInfoUri(userInfoUri)
                .userNameAttributeName(userNameAttributeName);

        Optional.ofNullable(scopes).ifPresent(builder::scope);

        Map<String, Object> providerMetadata = new HashMap<>();
        providerMetadata.put(IClientConstants.PROVIDER_ID, providerId);
        Optional.ofNullable(metadata).ifPresent(providerMetadata::putAll);
        builder.providerConfigurationMetadata(providerMetadata);

        return builder;
    }
}
