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
 * OAuth2 登录适配器接口
 * 用于处理 OAuth2 登录的自定义逻辑
 * 包括授权请求的定制、访问令牌响应的处理以及用户信息的获取
 *
 * @author george
 */
public interface IOAuth2LoginAdapter extends OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest>, OAuth2UserService<OAuth2UserRequest, OAuth2User>, Predicate<String> {

    /**
     * 自定义授权请求
     *
     * @param request            请求对象
     * @param clientRegistration 客户端注册信息
     * @return 自定义的授权请求对象
     */
    void accept(OAuth2AuthorizationRequest.Builder builder, HttpServletRequest request, ClientRegistration clientRegistration);
}
