package com.gls.athena.security.servlet.resource.support;

import cn.hutool.core.bean.BeanUtil;
import com.gls.athena.common.bean.security.User;
import org.springframework.http.RequestEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * 默认不透明令牌解释器
 *
 * @author george
 */
@Component
public class DefaultOpaqueTokenIntrospector implements OpaqueTokenIntrospector {
    /**
     * 令牌解释地址
     */
    private static final String INTROSPECT_URI = "http://localhost:8082/userinfo";

    /**
     * 不透明令牌解释
     *
     * @param token 令牌
     * @return OAuth2认证主体
     */
    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        RestTemplate restTemplate = new RestTemplate();
        RequestEntity<Void> requestEntity = requestEntityConverter(token);
        return toPrincipal(restTemplate.exchange(requestEntity, User.class).getBody());
    }

    /**
     * 转换为OAuth2认证主体
     *
     * @param user 用户
     * @return OAuth2认证主体
     */
    private OAuth2AuthenticatedPrincipal toPrincipal(User user) {
        if (user == null) {
            throw new InvalidBearerTokenException("Invalid token");
        }
        String username = user.getUsername();
        Map<String, Object> attributes = BeanUtil.beanToMap(user);
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        return new DefaultOAuth2AuthenticatedPrincipal(username, attributes, new ArrayList<>(authorities));
    }

    /**
     * 获取请求实体
     *
     * @param token 令牌
     * @return 请求实体
     */
    private RequestEntity<Void> requestEntityConverter(String token) {
        return RequestEntity.get(INTROSPECT_URI)
                .header("Authorization", "Bearer " + token)
                .build();
    }
}
