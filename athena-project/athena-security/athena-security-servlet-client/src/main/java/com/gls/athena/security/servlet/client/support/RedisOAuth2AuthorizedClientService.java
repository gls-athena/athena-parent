package com.gls.athena.security.servlet.client.support;

import cn.hutool.core.lang.TypeReference;
import com.gls.athena.starter.data.redis.support.RedisUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Component;

/**
 * 基于 Redis 的 OAuth2 授权客户端服务实现
 *
 * <p>该实现使用 Redis 存储 OAuth2AuthorizedClient 信息，提供授权客户端的持久化管理。
 * 主要用于处理客户端的授权信息的加载、保存和删除操作。</p>
 *
 * @author george
 */
@Component
public class RedisOAuth2AuthorizedClientService implements OAuth2AuthorizedClientService {

    /**
     * Redis 中存储授权客户端信息的键名前缀
     */
    private static final String CACHE_NAME = "oauth2:authorized:client";

    /**
     * 根据客户端注册ID和用户主体名称加载已授权的客户端
     *
     * @param clientRegistrationId 客户端注册ID，用于标识特定的 OAuth2 客户端
     * @param principalName        用户主体名称，通常是用户的唯一标识
     * @param <T>                  OAuth2AuthorizedClient 的具体类型
     * @return 如果找到则返回对应的授权客户端实例，否则返回 null
     */
    @Override
    public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId, String principalName) {
        return RedisUtil.getCacheValue(CACHE_NAME, clientRegistrationId + ":" + principalName, new TypeReference<>() {
        });
    }

    /**
     * 保存授权客户端信息到 Redis
     *
     * @param authorizedClient 需要保存的授权客户端实例
     * @param principal        与授权客户端关联的用户认证信息
     */
    @Override
    public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
        RedisUtil.setCacheValue(CACHE_NAME, authorizedClient.getClientRegistration().getRegistrationId() + ":" + principal.getName(), authorizedClient);
    }

    /**
     * 从 Redis 中移除指定的授权客户端信息
     *
     * @param clientRegistrationId 需要移除的客户端注册ID
     * @param principalName        关联的用户主体名称
     */
    @Override
    public void removeAuthorizedClient(String clientRegistrationId, String principalName) {
        RedisUtil.deleteCacheValue(CACHE_NAME, clientRegistrationId + ":" + principalName);
    }
}
