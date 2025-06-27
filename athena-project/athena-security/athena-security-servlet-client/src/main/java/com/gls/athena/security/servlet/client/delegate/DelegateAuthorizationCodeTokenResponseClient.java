package com.gls.athena.security.servlet.client.delegate;

import cn.hutool.core.map.MapUtil;
import com.gls.athena.security.servlet.client.config.IClientConstants;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.RestClientAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 委托授权码令牌响应客户端
 *
 * @author george
 */
@Slf4j
@Component
public class DelegateAuthorizationCodeTokenResponseClient implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {

    private static final RestClientAuthorizationCodeTokenResponseClient DEFAULT = new RestClientAuthorizationCodeTokenResponseClient();

    @Resource
    private ISocialLoginAdapterManager adapterManager;

    /**
     * 获取OAuth2访问令牌响应。
     * 根据授权码授权请求，从适配器管理器中获取对应提供商的适配器，并获取令牌响应。
     * 如果找不到对应提供商的适配器，则使用默认的RestClientAuthorizationCodeTokenResponseClient进行处理。
     *
     * @param authorizationCodeGrantRequest OAuth2授权码授权请求，包含授权码等信息
     * @return OAuth2AccessTokenResponse OAuth2访问令牌响应
     */
    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationCodeGrantRequest) {
        // 从授权请求中提取提供商ID
        String providerId = extractProviderId(authorizationCodeGrantRequest);
        log.debug("Processing token response for provider: {}", providerId);

        // 获取对应提供商的适配器并处理令牌响应，若不存在则使用默认处理
        return adapterManager.getAdapter(providerId)
                .map(adapter -> adapter.getTokenResponse(authorizationCodeGrantRequest))
                .orElseGet(() -> DEFAULT.getTokenResponse(authorizationCodeGrantRequest));
    }

    /**
     * 从OAuth2授权码请求中提取提供商标识(providerId)
     *
     * @param request OAuth2授权码请求对象，包含客户端注册信息和提供方配置元数据
     * @return 提供商标识字符串，从配置元数据中获取的{@link IClientConstants#PROVIDER_ID}对应值
     * 如果元数据中不存在该键则可能返回null
     */
    private String extractProviderId(OAuth2AuthorizationCodeGrantRequest request) {
        // 从客户端注册信息的提供方详情中获取配置元数据
        Map<String, Object> metadata = request.getClientRegistration().getProviderDetails().getConfigurationMetadata();
        // 使用工具类从元数据Map中获取指定键的字符串值
        return MapUtil.getStr(metadata, IClientConstants.PROVIDER_ID);
    }

}
