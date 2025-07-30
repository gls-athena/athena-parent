package com.gls.athena.security.servlet.authorization.support;

import com.gls.athena.starter.data.redis.support.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2DeviceCode;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2UserCode;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;

import java.util.List;

/**
 * Redis OAuth2授权服务
 *
 * @author george
 */
@Slf4j
public class RedisOAuth2AuthorizationService implements OAuth2AuthorizationService {

    /**
     * 缓存名称
     */
    private static final String CACHE_NAME = "authorization";

    /**
     * 保存授权
     *
     * @param authorization oauth2授权
     */
    @Override
    public void save(OAuth2Authorization authorization) {
        log.debug("保存授权: {}", authorization);
        List<OAuth2Authorization> authorizations = RedisUtil.getCacheValueList(CACHE_NAME, OAuth2Authorization.class);
        authorizations.stream()
                .filter(auth -> auth.getPrincipalName().equals(authorization.getPrincipalName())
                        && auth.getRegisteredClientId().equals(authorization.getRegisteredClientId()))
                .forEach(this::remove);
        RedisUtil.setCacheValue(CACHE_NAME, authorization.getId(), authorization);
    }

    /**
     * 删除授权
     *
     * @param authorization oauth2授权
     */
    @Override
    public void remove(OAuth2Authorization authorization) {
        log.debug("删除授权: {}", authorization);
        RedisUtil.deleteCacheValue(CACHE_NAME, authorization.getId());
    }

    /**
     * 根据id查找授权
     *
     * @param id 授权id
     * @return 授权
     */
    @Override
    public OAuth2Authorization findById(String id) {
        log.debug("根据id查找授权: {}", id);
        return RedisUtil.getCacheValue(CACHE_NAME, id, OAuth2Authorization.class);
    }

    /**
     * 根据token查找授权
     *
     * @param token     令牌
     * @param tokenType 令牌类型
     * @return 授权
     */
    @Override
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        log.debug("根据token查找授权: {}, {}", token, tokenType);
        List<OAuth2Authorization> authorizations = RedisUtil.getCacheValueList(CACHE_NAME, OAuth2Authorization.class);
        return authorizations.stream()
                .filter(authorization -> hasToken(authorization, token, tokenType))
                .findFirst()
                .orElse(null);
    }

    /**
     * 根据客户端id和用户名称查找授权
     *
     * @param authorization oauth2授权
     * @param token         令牌
     * @param tokenType     令牌类型
     * @return 是否包含
     */
    private boolean hasToken(OAuth2Authorization authorization, String token,
                             OAuth2TokenType tokenType) {
        if (tokenType == null) {
            return matchesState(authorization, token) ||
                    matchesAuthorizationCode(authorization, token) ||
                    matchesAccessToken(authorization, token) ||
                    matchesIdToken(authorization, token) ||
                    matchesRefreshToken(authorization, token) ||
                    matchesDeviceCode(authorization, token) ||
                    matchesUserCode(authorization, token);
        }
        if (OAuth2ParameterNames.STATE.equals(tokenType.getValue())) {
            return matchesState(authorization, token);
        }
        if (OAuth2ParameterNames.CODE.equals(tokenType.getValue())) {
            return matchesAuthorizationCode(authorization, token);
        }
        if (OAuth2TokenType.ACCESS_TOKEN.equals(tokenType)) {
            return matchesAccessToken(authorization, token);
        }
        if (OidcParameterNames.ID_TOKEN.equals(tokenType.getValue())) {
            return matchesIdToken(authorization, token);
        }
        if (OAuth2TokenType.REFRESH_TOKEN.equals(tokenType)) {
            return matchesRefreshToken(authorization, token);
        }
        if (OAuth2ParameterNames.DEVICE_CODE.equals(tokenType.getValue())) {
            return matchesDeviceCode(authorization, token);
        }
        if (OAuth2ParameterNames.USER_CODE.equals(tokenType.getValue())) {
            return matchesUserCode(authorization, token);
        }
        return false;
    }

    /**
     * 匹配状态
     *
     * @param authorization 授权
     * @param token         令牌
     * @return 是否匹配
     */
    private boolean matchesState(OAuth2Authorization authorization, String token) {
        return token.equals(authorization.getAttribute(OAuth2ParameterNames.STATE));
    }

    /**
     * 匹配授权码
     *
     * @param authorization 授权
     * @param token         令牌
     * @return 是否匹配
     */
    private boolean matchesAuthorizationCode(OAuth2Authorization authorization, String token) {
        OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode = authorization
                .getToken(OAuth2AuthorizationCode.class);
        return authorizationCode != null && authorizationCode.getToken().getTokenValue().equals(token);
    }

    /**
     * 匹配访问令牌
     *
     * @param authorization 授权
     * @param token         令牌
     * @return 是否匹配
     */
    private boolean matchesAccessToken(OAuth2Authorization authorization, String token) {
        OAuth2Authorization.Token<OAuth2AccessToken> accessToken = authorization.getToken(OAuth2AccessToken.class);
        return accessToken != null && accessToken.getToken().getTokenValue().equals(token);
    }

    /**
     * 匹配刷新令牌
     *
     * @param authorization 授权
     * @param token         令牌
     * @return 是否匹配
     */
    private boolean matchesRefreshToken(OAuth2Authorization authorization, String token) {
        OAuth2Authorization.Token<OAuth2RefreshToken> refreshToken = authorization.getToken(OAuth2RefreshToken.class);
        return refreshToken != null && refreshToken.getToken().getTokenValue().equals(token);
    }

    /**
     * 匹配id令牌
     *
     * @param authorization 授权
     * @param token         令牌
     * @return 是否匹配
     */
    private boolean matchesIdToken(OAuth2Authorization authorization, String token) {
        OAuth2Authorization.Token<OidcIdToken> idToken = authorization.getToken(OidcIdToken.class);
        return idToken != null && idToken.getToken().getTokenValue().equals(token);
    }

    /**
     * 匹配设备令牌
     *
     * @param authorization 授权
     * @param token         令牌
     * @return 是否匹配
     */
    private boolean matchesDeviceCode(OAuth2Authorization authorization, String token) {
        OAuth2Authorization.Token<OAuth2DeviceCode> deviceCode = authorization.getToken(OAuth2DeviceCode.class);
        return deviceCode != null && deviceCode.getToken().getTokenValue().equals(token);
    }

    /**
     * 匹配用户令牌
     *
     * @param authorization 授权
     * @param token         令牌
     * @return 是否匹配
     */
    private boolean matchesUserCode(OAuth2Authorization authorization, String token) {
        OAuth2Authorization.Token<OAuth2UserCode> userCode = authorization.getToken(OAuth2UserCode.class);
        return userCode != null && userCode.getToken().getTokenValue().equals(token);
    }
}
