package com.gls.athena.security.servlet.authorization.support;

import cn.hutool.core.bean.BeanUtil;
import com.gls.athena.common.bean.security.SocialUser;
import com.gls.athena.common.bean.security.User;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.ArrayList;

/**
 * 默认不透明令牌解释器
 *
 * @author george
 */
@Slf4j
@Component
public class DefaultOpaqueTokenIntrospector implements OpaqueTokenIntrospector {
    /**
     * 授权服务
     */
    @Resource
    private OAuth2AuthorizationService authorizationService;

    /**
     * 不透明令牌解释
     *
     * @param token 令牌
     * @return OAuth2认证主体
     */
    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        // 查询授权
        OAuth2Authorization oauth2Authorization = authorizationService.findByToken(token, null);
        if (oauth2Authorization == null) {
            // 无效令牌
            log.warn("Invalid token: {}", token);
            throw new InvalidBearerTokenException(token);
        }
        // 查询令牌
        OAuth2Authorization.Token<OAuth2Token> authorizationToken = oauth2Authorization.getToken(token);
        if (authorizationToken == null || !authorizationToken.isActive()) {
            // 无效令牌
            log.warn("Invalid token: {}", token);
            throw new InvalidBearerTokenException(token);
        }
        // 返回令牌主体
        Authentication authentication = oauth2Authorization.getAttribute(Principal.class.getName());
        assert authentication != null;
        Object principal = authentication.getPrincipal();
        if (principal instanceof SocialUser socialUser) {
            return convertToPrincipal(socialUser);
        }
        return new DefaultOAuth2AuthenticatedPrincipal(authentication.getName(),
                BeanUtil.beanToMap(authentication.getPrincipal()),
                new ArrayList<>(authentication.getAuthorities()));
    }

    /**
     * 转换为OAuth2认证主体
     *
     * @param socialUser 社交用户
     * @return OAuth2认证主体
     */
    private OAuth2AuthenticatedPrincipal convertToPrincipal(SocialUser socialUser) {
        User user = socialUser.getUser();
        return new DefaultOAuth2AuthenticatedPrincipal(user.getUsername(),
                BeanUtil.beanToMap(user),
                new ArrayList<>(user.getAuthorities()));
    }
}
