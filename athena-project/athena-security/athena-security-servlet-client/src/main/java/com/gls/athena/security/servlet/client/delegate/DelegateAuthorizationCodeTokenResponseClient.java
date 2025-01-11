package com.gls.athena.security.servlet.client.delegate;

import cn.hutool.core.map.MapUtil;
import com.gls.athena.security.servlet.client.config.IClientConstants;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * OAuth2授权码模式的令牌响应委托客户端
 * 用于处理不同提供商的OAuth2授权码换取令牌的请求
 *
 * @author george
 */
@Component
public class DelegateAuthorizationCodeTokenResponseClient implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {

    /**
     * 默认的OAuth2授权码令牌响应客户端
     * 当没有匹配的适配器时使用此默认客户端
     */
    private static final DefaultAuthorizationCodeTokenResponseClient DEFAULT = new DefaultAuthorizationCodeTokenResponseClient();

    /**
     * OAuth2客户端适配器提供者
     * 用于获取不同提供商的令牌响应处理适配器
     */
    @Resource
    private ObjectProvider<IAuthorizationCodeTokenResponseClientAdapter> adapters;

    /**
     * 处理OAuth2授权码换取访问令牌的请求
     *
     * @param authorizationCodeGrantRequest OAuth2授权码授权请求
     * @return OAuth2访问令牌响应
     */
    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationCodeGrantRequest) {
        Map<String, Object> metadata = authorizationCodeGrantRequest.getClientRegistration().getProviderDetails().getConfigurationMetadata();
        // 获取提供者
        String providerId = MapUtil.getStr(metadata, IClientConstants.PROVIDER_ID);
        // 获取适配器
        return adapters.stream()
                .filter(adapter -> adapter.test(providerId))
                .findFirst()
                .map(adapter -> adapter.getTokenResponse(authorizationCodeGrantRequest))
                .orElseGet(() -> DEFAULT.getTokenResponse(authorizationCodeGrantRequest));
    }

}
