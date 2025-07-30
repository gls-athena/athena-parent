package com.gls.athena.security.servlet.client.delegate;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.function.Predicate;

/**
 * 社交登录适配器
 *
 * @author george
 */
public interface ISocialLoginAdapter extends OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest>,
        OAuth2UserService<OAuth2UserRequest, OAuth2User>, Predicate<String> {

    /**
     * 社交登录适配器
     *
     * @param builder            社交登录请求构建器
     * @param request            请求
     * @param clientRegistration 社交登录客户端注册信息
     */
    void accept(OAuth2AuthorizationRequest.Builder builder, HttpServletRequest request, ClientRegistration clientRegistration);

    /**
     * 根据OIDC用户请求加载OIDC用户信息
     * 此方法用于处理OIDC（OpenID Connect）流程中的用户信息加载步骤
     * 它接收一个包含用户信息请求的OidcUserRequest对象，并应返回一个OidcUser对象
     * 注意：当前实现尚未完成，需要开发者根据实际需求进行实现
     *
     * @param userRequest 包含用户信息请求的OidcUserRequest对象，用于加载用户信息
     * @return OidcUser对象，代表加载的用户信息当前返回null表示未实现
     */
    default OidcUser loadOidcUser(OidcUserRequest userRequest) {
        return null;
    }
}
