package com.gls.athena.security.servlet.client.delegate;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.function.Predicate;

/**
 * 社交登录适配器
 *
 * @author lizy19
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
}
