package com.gls.athena.security.servlet.client.delegate;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.RestClientAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.stereotype.Component;

/**
 * 委托授权码令牌响应客户端
 *
 * @author george
 */
@Slf4j
@Component
public class DelegateAuthorizationCodeTokenResponseClient implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {

    /**
     * 默认的适配器，用于处理没有适配器的情况
     */
    private static final RestClientAuthorizationCodeTokenResponseClient DEFAULT = new RestClientAuthorizationCodeTokenResponseClient();

    /**
     * 适配器管理器，用于管理社交登录适配器
     */
    @Resource
    private ISocialLoginAdapterManager adapterManager;

    /**
     * 获取OAuth2访问令牌响应。
     * <p>
     * 根据客户端注册信息获取对应的适配器，并使用适配器获取令牌响应。如果没有找到适配器，
     * 则使用默认的DEFAULT适配器获取令牌响应。
     *
     * @param request OAuth2授权码授权请求，包含客户端注册信息和授权码等必要信息
     * @return OAuth2AccessTokenResponse 返回OAuth2访问令牌响应，包含访问令牌、刷新令牌等信息
     */
    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest request) {
        // 根据客户端注册信息获取适配器，并尝试获取令牌响应
        return adapterManager.getAdapter(request.getClientRegistration())
                .map(adapter -> adapter.getTokenResponse(request))
                .orElseGet(() -> DEFAULT.getTokenResponse(request));
    }

}
