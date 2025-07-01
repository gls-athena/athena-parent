package com.gls.athena.security.servlet.client.delegate;

import cn.hutool.core.map.MapUtil;
import com.gls.athena.security.servlet.client.config.IClientConstants;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.RestClientAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 社交登录适配器管理器
 *
 * @author george
 */
@Component
public class ISocialLoginAdapterManager {
    /**
     * 访问令牌响应客户端
     */
    private final RestClientAuthorizationCodeTokenResponseClient tokenResponseClient = new RestClientAuthorizationCodeTokenResponseClient();
    /**
     * 默认OAuth2用户服务
     */
    private final DefaultOAuth2UserService oauth2UserService = new DefaultOAuth2UserService();
    /**
     * 社交登录适配器列表
     */
    @Resource
    private List<ISocialLoginAdapter> adapters;

    /**
     * 根据提供商ID获取相应的社交登录适配器
     *
     * @param providerId 社交登录提供商的唯一标识符
     * @return 如果找到匹配的适配器，则返回一个包含适配器的Optional对象，否则返回空Optional
     */
    public Optional<ISocialLoginAdapter> getAdapter(String providerId) {
        // 从适配器列表中筛选并返回第一个匹配providerId的适配器
        return this.adapters.stream()
                .filter(adapter -> adapter.test(providerId))
                .findFirst();
    }

    /**
     * 根据客户端注册信息获取对应的社交登录适配器
     * <p>
     * 该方法通过客户端注册信息(ClientRegistration)逐层解析，最终获取提供商标识(PROVIDER_ID)，
     * 并调用内部方法查找对应的社交登录适配器。如果任一步骤结果为null，则返回空的Optional。
     *
     * @param clientRegistration 客户端注册信息，包含提供商的详细配置信息
     * @return Optional包装的社交登录适配器，可能为空(如果无法匹配或参数为null)
     */
    public Optional<ISocialLoginAdapter> getAdapter(ClientRegistration clientRegistration) {
        return Optional.ofNullable(clientRegistration)
                .map(ClientRegistration::getProviderDetails)
                .map(ClientRegistration.ProviderDetails::getConfigurationMetadata)
                .map(metadata -> MapUtil.getStr(metadata, IClientConstants.PROVIDER_ID, "default"))
                .flatMap(this::getAdapter);
    }

    /**
     * 根据OAuth2授权码请求获取访问令牌响应
     * <p>
     * 此方法首先尝试通过客户端注册信息获取相应的适配器如果存在适配器，则使用适配器处理请求并获取令牌响应；
     * 否则，使用通用的令牌响应客户端处理请求并获取令牌响应这种方式允许系统在有特定适配器的情况下使用适配器，
     * 同时也为未定义适配器的情况提供了备用方案
     *
     * @param request 包含客户端注册信息和授权码的请求对象
     * @return 令牌响应对象，包含访问令牌和其他相关信息
     */
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest request) {
        // 尝试获取与客户端注册信息相关的适配器
        return this.getAdapter(request.getClientRegistration())
                .map(adapter -> adapter.getTokenResponse(request))
                .orElseGet(() -> tokenResponseClient.getTokenResponse(request));
    }

    /**
     * 加载OAuth2用户信息
     * 该方法首先尝试使用自定义的适配器处理用户请求，如果适配器不存在或无法处理请求，
     * 则回退到默认的OAuth2用户服务处理
     *
     * @param userRequest 用户请求信息，包含客户端注册信息和授权信息
     * @return 返回一个OAuth2用户对象，包含用户属性信息
     */
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        // 尝试获取与客户端注册信息相匹配的适配器
        // 如果适配器存在，则使用适配器处理用户请求
        // 否则，使用默认的OAuth2用户服务处理请求
        return this.getAdapter(userRequest.getClientRegistration())
                .map(adapter -> adapter.loadUser(userRequest))
                .orElseGet(() -> oauth2UserService.loadUser(userRequest));
    }

    /**
     * 处理OAuth2授权请求
     * <p>
     * 该方法通过获取与客户端注册信息相关的适配器，并调用适配器的accept方法来处理OAuth2授权请求
     * 主要目的是为了根据不同的客户端注册信息，使用合适的适配器进行授权流程
     *
     * @param builder            OAuth2授权请求的构建器，用于定制授权请求
     * @param request            HTTP请求对象，包含来自用户的请求信息
     * @param clientRegistration 客户端注册信息，包括客户端的配置详情
     */
    public void accept(OAuth2AuthorizationRequest.Builder builder, HttpServletRequest request, ClientRegistration clientRegistration) {
        // 根据客户端注册信息获取相应的适配器，并执行其accept方法
        this.getAdapter(clientRegistration)
                .ifPresent(adapter -> adapter.accept(builder, request, clientRegistration));
    }
}
