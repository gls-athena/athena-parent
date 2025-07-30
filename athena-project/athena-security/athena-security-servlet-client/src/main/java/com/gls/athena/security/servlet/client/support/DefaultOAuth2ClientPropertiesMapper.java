package com.gls.athena.security.servlet.client.support;

import com.gls.athena.security.servlet.client.config.IClientConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;
import org.springframework.security.oauth2.core.AuthenticationMethod;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * OAuth2客户端属性映射器的默认实现类
 * 主要职责：
 * 1. 将OAuth2ClientProperties配置属性转换为ClientRegistration对象
 * 2. 处理不同OAuth2提供者的配置映射
 * 3. 支持自定义提供者、通用提供者和默认提供者的配置转换
 *
 * @author george
 */
@RequiredArgsConstructor
public class DefaultOAuth2ClientPropertiesMapper {

    private final OAuth2ClientProperties properties;

    /**
     * 获取所有OAuth2客户端注册信息
     *
     * @return 返回注册ID到客户端注册信息的映射关系
     * key: 注册ID
     * value: 对应的客户端注册信息
     */
    public Map<String, ClientRegistration> getClientRegistrations() {
        return this.properties.getRegistration().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry ->
                        getClientRegistration(entry.getKey(), entry.getValue())));
    }

    /**
     * 获取OAuth2服务提供者标识
     *
     * @param registrationId 客户端注册ID
     * @return 如果显式配置了provider则返回配置值，否则返回registrationId作为默认provider
     */
    public String getProvider(String registrationId) {
        if (registrationId == null) {
            return null;
        }
        String provider = this.properties.getRegistration().get(registrationId).getProvider();
        if (provider == null) {
            return registrationId;
        }
        return provider;
    }

    /**
     * 将OAuth2客户端注册配置转换为ClientRegistration对象
     *
     * @param registrationId 注册ID，用于唯一标识客户端注册
     * @param registration   OAuth2客户端注册配置信息
     * @return 构建完成的ClientRegistration实例
     */
    private ClientRegistration getClientRegistration(String registrationId, OAuth2ClientProperties.Registration registration) {
        String providerId = getProvider(registrationId);
        ClientRegistration.Builder builder = getBuilderByProvider(registrationId, providerId);

        // 映射配置属性到builder
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        map.from(registrationId).to(builder::registrationId);
        map.from(registration::getClientId).to(builder::clientId);
        map.from(registration::getClientSecret).to(builder::clientSecret);
        map.from(registration::getClientAuthenticationMethod)
                .as(ClientAuthenticationMethod::new)
                .to(builder::clientAuthenticationMethod);
        map.from(registration::getAuthorizationGrantType)
                .as(AuthorizationGrantType::new)
                .to(builder::authorizationGrantType);
        map.from(registration::getRedirectUri).to(builder::redirectUri);
        map.from(registration::getScope).as(StringUtils::toStringArray).to(builder::scope);
        map.from(registration::getClientName).to(builder::clientName);

        return builder.build();
    }

    /**
     * 根据提供者信息获取ClientRegistration构建器
     * 查找顺序：
     * 1. 优先从配置文件中的provider配置获取
     * 2. 尝试从CommonOAuth2Provider内置提供者获取
     * 3. 最后尝试从自定义DefaultOAuth2Provider获取
     *
     * @param registrationId 客户端注册ID
     * @param providerId     提供者ID
     * @return ClientRegistration的构建器实例
     * @throws IllegalArgumentException 当无法找到对应的provider配置时抛出
     */
    private ClientRegistration.Builder getBuilderByProvider(String registrationId, String providerId) {
        // 获取构建器 - 从提供者属性
        ClientRegistration.Builder builder = getBuilderByProperties(registrationId, providerId);
        if (builder == null) {
            // 获取构建器 - 从通用提供者
            builder = getBuilderByCommon(registrationId, providerId);
        }
        if (builder == null) {
            // 获取构建器 - 从默认提供者
            builder = getBuilderByDefault(registrationId, providerId);
        }
        if (builder == null) {
            // 未知提供者
            throw new IllegalArgumentException("Unknown provider: " + providerId);
        }
        // 映射属性
        return builder;
    }

    /**
     * 从DefaultOAuth2Provider获取构建器
     *
     * @param registrationId 客户端注册ID
     * @param providerId     提供者ID
     * @return 成功则返回构建器实例，失败则返回null
     */
    private ClientRegistration.Builder getBuilderByDefault(String registrationId, String providerId) {
        try {
            DefaultOAuth2Provider defaultProvider = DefaultOAuth2Provider.valueOf(providerId.toUpperCase());
            return defaultProvider.getBuilder(registrationId);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从CommonOAuth2Provider获取构建器
     *
     * @param registrationId 客户端注册ID
     * @param providerId     提供者ID
     * @return 成功则返回构建器实例，失败则返回null
     */
    private ClientRegistration.Builder getBuilderByCommon(String registrationId, String providerId) {
        try {
            CommonOAuth2Provider commonProvider = CommonOAuth2Provider.valueOf(providerId.toUpperCase());
            ClientRegistration.Builder builder = commonProvider.getBuilder(registrationId);
            Map<String, Object> metadata = new HashMap<>(1);
            metadata.put(IClientConstants.PROVIDER_ID, providerId);
            builder.providerConfigurationMetadata(metadata);
            return builder;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从配置文件的provider属性创建构建器
     * 支持两种方式：
     * 1. 通过issuerUri自动发现配置
     * 2. 通过详细的provider配置手动创建
     *
     * @param registrationId 客户端注册ID
     * @param providerId     提供者ID
     * @return 成功则返回构建器实例，失败则返回null
     */
    private ClientRegistration.Builder getBuilderByProperties(String registrationId, String providerId) {
        // 获取提供者属性
        OAuth2ClientProperties.Provider provider = this.properties.getProvider().get(providerId);
        if (provider == null) {
            return null;
        }
        if (provider.getIssuerUri() != null) {
            // 从发行者位置获取构建器
            ClientRegistration.Builder builder = ClientRegistrations.fromIssuerLocation(provider.getIssuerUri())
                    .registrationId(registrationId);
            return copyProviderToBuilder(builder, provider);
        }
        // 从提供者属性获取构建器
        ClientRegistration.Builder builder = ClientRegistration.withRegistrationId(registrationId);
        return copyProviderToBuilder(builder, provider);
    }

    /**
     * 将Provider配置属性复制到Builder实例
     *
     * @param builder  ClientRegistration构建器
     * @param provider OAuth2提供者配置
     * @return 更新后的构建器实例
     */
    private ClientRegistration.Builder copyProviderToBuilder(ClientRegistration.Builder builder, OAuth2ClientProperties.Provider provider) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        map.from(provider::getAuthorizationUri).to(builder::authorizationUri);
        map.from(provider::getTokenUri).to(builder::tokenUri);
        map.from(provider::getUserInfoUri).to(builder::userInfoUri);
        map.from(provider::getUserInfoAuthenticationMethod)
                .as(AuthenticationMethod::new)
                .to(builder::userInfoAuthenticationMethod);
        map.from(provider::getJwkSetUri).to(builder::jwkSetUri);
        map.from(provider::getUserNameAttribute).to(builder::userNameAttributeName);
        return builder;
    }

}
