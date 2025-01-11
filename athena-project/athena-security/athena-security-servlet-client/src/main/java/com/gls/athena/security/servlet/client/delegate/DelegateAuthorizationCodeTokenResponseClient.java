package com.gls.athena.security.servlet.client.delegate;

import cn.hutool.core.map.MapUtil;
import com.gls.athena.security.servlet.client.config.IClientConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OAuth2授权码模式的令牌响应委托客户端
 * <p>
 * 该客户端通过委托模式实现不同OAuth2提供商的令牌响应处理：
 * 1. 支持多个OAuth2提供商的适配器注册
 * 2. 使用缓存机制提高性能
 * 3. 当没有匹配的适配器时，降级使用默认客户端
 * </p>
 *
 * @author george
 */
@Slf4j
@Component
public class DelegateAuthorizationCodeTokenResponseClient implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {

    /**
     * 默认的令牌响应客户端，用于处理未匹配到专用适配器的情况
     */
    private static final DefaultAuthorizationCodeTokenResponseClient DEFAULT = new DefaultAuthorizationCodeTokenResponseClient();

    /**
     * OAuth2提供商适配器列表
     */
    private final List<IAuthorizationCodeTokenResponseClientAdapter> adapters;
    /**
     * 适配器缓存，用于提高查找性能 key:providerId, value:adapter
     */
    private final Map<String, IAuthorizationCodeTokenResponseClientAdapter> adapterCache = new ConcurrentHashMap<>();

    /**
     * 构造函数，注入所有可用的OAuth2适配器
     *
     * @param adapters OAuth2适配器列表
     */
    public DelegateAuthorizationCodeTokenResponseClient(List<IAuthorizationCodeTokenResponseClientAdapter> adapters) {
        this.adapters = adapters;
    }

    /**
     * 处理OAuth2授权码换取访问令牌的请求
     * <p>
     * 处理流程：
     * 1. 提取providerId
     * 2. 查找对应的适配器
     * 3. 使用适配器处理请求，如无适配器则使用默认客户端
     * </p>
     *
     * @param authorizationCodeGrantRequest OAuth2授权码授权请求
     * @return OAuth2访问令牌响应
     * @throws Exception 处理过程中的异常将被记录并向上传播
     */
    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationCodeGrantRequest) {
        try {
            String providerId = extractProviderId(authorizationCodeGrantRequest);
            IAuthorizationCodeTokenResponseClientAdapter adapter = findAdapter(providerId);

            if (adapter != null) {
                log.debug("Using adapter for provider: {}", providerId);
                return adapter.getTokenResponse(authorizationCodeGrantRequest);
            }

            log.debug("No adapter found for provider: {}, using default client", providerId);
            return DEFAULT.getTokenResponse(authorizationCodeGrantRequest);
        } catch (Exception e) {
            log.error("Error processing token response", e);
            throw e;
        }
    }

    /**
     * 从请求中提取OAuth2提供商ID
     *
     * @param request OAuth2授权码授权请求
     * @return 提供商ID
     */
    private String extractProviderId(OAuth2AuthorizationCodeGrantRequest request) {
        Map<String, Object> metadata = request.getClientRegistration().getProviderDetails().getConfigurationMetadata();
        return MapUtil.getStr(metadata, IClientConstants.PROVIDER_ID);
    }

    /**
     * 查找或缓存对应的OAuth2适配器
     * <p>
     * 使用ConcurrentHashMap确保线程安全，同时利用computeIfAbsent保证原子性操作
     * </p>
     *
     * @param providerId OAuth2提供商ID
     * @return 匹配的适配器，如果没有找到则返回null
     */
    private IAuthorizationCodeTokenResponseClientAdapter findAdapter(String providerId) {
        return adapterCache.computeIfAbsent(providerId, pid ->
                adapters.stream()
                        .filter(adapter -> adapter.test(pid))
                        .findFirst()
                        .orElse(null)
        );
    }
}
