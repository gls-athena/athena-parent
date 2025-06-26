package com.gls.athena.security.servlet.client.feishu;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.gls.athena.security.servlet.client.delegate.ISocialLoginAdapter;
import com.gls.athena.security.servlet.client.feishu.domian.FeishuUserAccessTokenRequest;
import com.gls.athena.security.servlet.client.feishu.domian.FeishuUserAccessTokenResponse;
import com.gls.athena.security.servlet.client.feishu.domian.FeishuUserInfoResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 飞书登录定制器
 *
 * @author george
 */
@Component
public class FeishuLoginAdapter implements ISocialLoginAdapter {

    /**
     * 测试是否支持指定的注册标识
     *
     * @param providerId 提供者标识
     * @return 是否支持
     */
    @Override
    public boolean test(String providerId) {
        return IFeishuConstants.PROVIDER_ID.equals(providerId);
    }

    /**
     * 自定义 OAuth2 授权请求
     *
     * @param builder 构建器
     * @param request 请求
     */
    @Override
    public void accept(OAuth2AuthorizationRequest.Builder builder, HttpServletRequest request, ClientRegistration clientRegistration) {
        // 飞书 OAuth2 授权请求参数处理
        builder.authorizationRequestUri(uriBuilder -> {
            // 获取 URI
            String uri = uriBuilder.build().getQuery();
            // 替换 client_id 为 app_id
            uri = uri.replace(OAuth2ParameterNames.CLIENT_ID, "app_id");
            // 飞书 OAuth2 授权请求参数处理
            return uriBuilder.replaceQuery(uri).build();
        });
    }

    /**
     * 自定义 OAuth2 访问令牌响应
     *
     * @param authorizationGrantRequest 授权码授权请求
     * @return 访问令牌响应
     */
    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest) {
        // 获取用户访问令牌
        FeishuUserAccessTokenRequest request = convertAccessTokenRequest(authorizationGrantRequest);
        // 获取客户端id
        String clientId = authorizationGrantRequest.getClientRegistration().getClientId();
        // 获取客户端密钥
        String clientSecret = authorizationGrantRequest.getClientRegistration().getClientSecret();
        // 获取客户端请求uri
        Map<String, Object> metadata = authorizationGrantRequest.getClientRegistration().getProviderDetails().getConfigurationMetadata();
        String appAccessTokenUri = MapUtil.getStr(metadata, IFeishuConstants.APP_ACCESS_TOKEN_URL_NAME);
        // 获取应用访问令牌
        String appAccessToken = FeishuHelper.getAppAccessToken(clientId, clientSecret, appAccessTokenUri);
        // 获取用户访问令牌地址
        String uri = authorizationGrantRequest.getClientRegistration().getProviderDetails().getTokenUri();
        // 获取用户访问令牌响应
        FeishuUserAccessTokenResponse response = FeishuHelper.getUserAccessToken(request, uri, appAccessToken);
        // 转换响应
        return convertAccessTokenResponse(response);
    }

    /**
     * 转换访问令牌请求
     *
     * @param authorizationGrantRequest 授权码授权请求
     * @return 访问令牌请求
     */
    private FeishuUserAccessTokenRequest convertAccessTokenRequest(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest) {
        FeishuUserAccessTokenRequest request = new FeishuUserAccessTokenRequest();
        request.setCode(authorizationGrantRequest.getAuthorizationExchange().getAuthorizationResponse().getCode());
        request.setGrantType(authorizationGrantRequest.getGrantType().getValue());
        return request;
    }

    /**
     * 转换访问令牌响应
     *
     * @param response 访问令牌响应
     * @return 访问令牌响应
     */
    private OAuth2AccessTokenResponse convertAccessTokenResponse(FeishuUserAccessTokenResponse response) {
        return OAuth2AccessTokenResponse.withToken(response.getAccessToken())
                .tokenType(OAuth2AccessToken.TokenType.BEARER)
                .expiresIn(response.getExpiresIn())
                .refreshToken(response.getRefreshToken())
                .scopes(convertScopes(response.getScope()))
                .additionalParameters(convertAdditionalParameters(response))
                .build();
    }

    /**
     * 转换权限
     *
     * @param scope 权限
     * @return 权限
     */
    private Set<String> convertScopes(String scope) {
        return StrUtil.isBlank(scope) ? Collections.emptySet() : new HashSet<>(StrUtil.split(scope, " "));
    }

    /**
     * 转换附加参数
     *
     * @param response 访问令牌响应
     * @return 附加参数
     */
    private Map<String, Object> convertAdditionalParameters(FeishuUserAccessTokenResponse response) {
        Map<String, Object> additionalParameters = new HashMap<>();
        additionalParameters.put("refreshExpiresIn", response.getRefreshExpiresIn());
        return additionalParameters;
    }

    /**
     * 自定义 OAuth2 用户信息服务
     *
     * @param userRequest 用户请求
     * @return OAuth2 用户
     * @throws OAuth2AuthenticationException OAuth2 认证异常
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 获取用户信息地址
        ClientRegistration.ProviderDetails.UserInfoEndpoint userInfoEndpoint = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint();
        // 获取令牌
        OAuth2AccessToken accessToken = userRequest.getAccessToken();
        // 获取用户信息
        FeishuUserInfoResponse response = FeishuHelper.getUserInfo(accessToken.getTokenValue(), userInfoEndpoint.getUri());
        // 转换用户
        return convertUser(response, accessToken.getScopes(), userInfoEndpoint.getUserNameAttributeName());
    }

    /**
     * 转换用户
     *
     * @param response 用户信息响应
     * @param scopes   权限
     * @return OAuth2 用户
     */
    private OAuth2User convertUser(FeishuUserInfoResponse response, Set<String> scopes, String nameAttributeKey) {
        // 转换为 OAuth2 用户
        Map<String, Object> attributes = BeanUtil.beanToMap(response);
        // 设置权限
        Set<GrantedAuthority> authorities = scopes.stream()
                .map(scope -> new SimpleGrantedAuthority("SCOPE_" + scope))
                .collect(Collectors.toSet());
        // 返回用户
        return new DefaultOAuth2User(authorities, attributes, nameAttributeKey);
    }
}
