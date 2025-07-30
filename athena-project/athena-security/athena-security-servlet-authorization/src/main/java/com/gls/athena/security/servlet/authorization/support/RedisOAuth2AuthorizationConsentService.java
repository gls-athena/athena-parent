package com.gls.athena.security.servlet.authorization.support;

import com.gls.athena.starter.data.redis.support.RedisUtil;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;

/**
 * Redis OAuth2授权同意服务
 *
 * @author george
 */
public class RedisOAuth2AuthorizationConsentService implements OAuth2AuthorizationConsentService {
    /**
     * 缓存名称
     */
    private static final String CACHE_NAME = "authorization-consent";

    /**
     * 保存
     *
     * @param authorizationConsent 授权同意
     */
    @Override
    public void save(OAuth2AuthorizationConsent authorizationConsent) {
        RedisUtil.setCacheValue(buildKey(authorizationConsent), authorizationConsent);
    }

    /**
     * 移除
     *
     * @param authorizationConsent 授权同意
     */
    @Override
    public void remove(OAuth2AuthorizationConsent authorizationConsent) {
        RedisUtil.deleteCacheValue(buildKey(authorizationConsent));
    }

    /**
     * 根据注册的客户端ID和主体名称查找
     *
     * @param registeredClientId 注册的客户端ID
     * @param principalName      主体名称
     * @return OAuth2AuthorizationConsent 授权同意
     */
    @Override
    public OAuth2AuthorizationConsent findById(String registeredClientId, String principalName) {
        return RedisUtil.getCacheValue(buildKey(registeredClientId, principalName), OAuth2AuthorizationConsent.class);
    }

    /**
     * 构建Key
     *
     * @param registeredClientId 注册的客户端ID
     * @param principalName      主体名称
     * @return Key
     */
    private String buildKey(String registeredClientId, String principalName) {
        return CACHE_NAME + RedisUtil.SEPARATOR + registeredClientId + RedisUtil.SEPARATOR + principalName;
    }

    /**
     * 构建Key
     *
     * @param authorizationConsent 授权同意
     * @return Key
     */
    private String buildKey(OAuth2AuthorizationConsent authorizationConsent) {
        return buildKey(authorizationConsent.getRegisteredClientId(), authorizationConsent.getPrincipalName());
    }
}
