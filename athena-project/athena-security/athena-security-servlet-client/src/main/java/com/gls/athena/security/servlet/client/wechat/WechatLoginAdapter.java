package com.gls.athena.security.servlet.client.wechat;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.gls.athena.security.servlet.client.delegate.ISocialLoginAdapter;
import com.gls.athena.security.servlet.client.wechat.domain.WechatAccessTokenRequest;
import com.gls.athena.security.servlet.client.wechat.domain.WechatAccessTokenResponse;
import com.gls.athena.security.servlet.client.wechat.domain.WechatUserInfoRequest;
import com.gls.athena.security.servlet.client.wechat.domain.WechatUserInfoResponse;
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
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 微信登录定制器
 *
 * @author george
 */
@Component
public class WechatLoginAdapter implements ISocialLoginAdapter {

    /**
     * 测试是否支持指定的注册标识
     *
     * @param registrationId 注册标识
     * @return 是否支持
     */
    @Override
    public boolean test(String registrationId) {
        return IWechatConstants.WECHAT_OPEN_PROVIDER_ID.equals(registrationId) || IWechatConstants.WECHAT_MP_PROVIDER_ID.equals(registrationId);
    }

    /**
     * 自定义 OAuth2 授权请求
     *
     * @param builder 构建器
     * @param request 请求
     */
    @Override
    public void accept(OAuth2AuthorizationRequest.Builder builder, HttpServletRequest request, ClientRegistration clientRegistration) {
        // 微信 OAuth2 授权请求参数处理
        builder.authorizationRequestUri(uriBuilder -> {
            // 构建 URI
            String uri = uriBuilder.build().getQuery();
            // 替换 client_id 为 appid
            uri = uri.replace("client_id", "appId");
            // 微信 OAuth2 授权请求参数添加 #wechat_redirect
            return uriBuilder.replaceQuery(uri).fragment("wechat_redirect").build();
        });
    }

    /**
     * 获取令牌响应
     *
     * @param authorizationGrantRequest 请求
     * @return 令牌响应
     */
    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest) {
        // 获取令牌请求
        WechatAccessTokenRequest request = convertAccessTokenRequest(authorizationGrantRequest);
        // 获取令牌请求 URI
        String accessTokenUri = authorizationGrantRequest.getClientRegistration().getProviderDetails().getTokenUri();
        // 获取令牌响应
        WechatAccessTokenResponse response = WechatHelper.getWechatAccessToken(request, accessTokenUri);
        // 转换令牌响应
        return convertAccessTokenResponse(response);
    }

    /**
     * 转换请求
     *
     * @param authorizationGrantRequest 请求
     * @return 微信访问令牌请求
     */
    private WechatAccessTokenRequest convertAccessTokenRequest(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest) {
        WechatAccessTokenRequest request = new WechatAccessTokenRequest();
        request.setAppid(authorizationGrantRequest.getClientRegistration().getClientId());
        request.setSecret(authorizationGrantRequest.getClientRegistration().getClientSecret());
        request.setCode(authorizationGrantRequest.getAuthorizationExchange().getAuthorizationResponse().getCode());
        request.setGrantType(authorizationGrantRequest.getGrantType().getValue());
        return request;
    }

    /**
     * 转换响应
     *
     * @param response 响应
     * @return 令牌响应
     */
    private OAuth2AccessTokenResponse convertAccessTokenResponse(WechatAccessTokenResponse response) {
        return OAuth2AccessTokenResponse.withToken(response.getAccessToken())
                .expiresIn(response.getExpiresIn())
                .refreshToken(response.getRefreshToken())
                .scopes(convertScopes(response.getScope()))
                .tokenType(OAuth2AccessToken.TokenType.BEARER)
                .additionalParameters(convertAdditionalParameters(response))
                .build();
    }

    /**
     * 转换权限集合
     *
     * @param scope 权限
     * @return 权限集合
     */
    private Set<String> convertScopes(String scope) {
        return StrUtil.isBlank(scope) ? Collections.emptySet() : new HashSet<>(StrUtil.split(scope, ","));
    }

    /**
     * 转换附加参数
     *
     * @param response 响应
     * @return 附加参数
     */
    private Map<String, Object> convertAdditionalParameters(WechatAccessTokenResponse response) {
        Map<String, Object> additionalParameters = new HashMap<>();
        additionalParameters.put("openid", response.getOpenid());
        additionalParameters.put("unionid", response.getUnionid());
        return additionalParameters;
    }

    /**
     * 加载用户
     *
     * @param userRequest 用户请求
     * @return OAuth2 用户
     * @throws OAuth2AuthenticationException OAuth2 认证异常
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 转换请求
        WechatUserInfoRequest request = convertUserInfoRequest(userRequest);
        // 获取用户信息 URI
        String userInfoUri = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri();
        // 获取用户信息响应
        WechatUserInfoResponse response = WechatHelper.getWechatUserInfo(request, userInfoUri);
        // 转换用户
        return convertUserInfoResponse(response, userRequest.getAccessToken().getScopes());
    }

    /**
     * 转换请求
     *
     * @param userRequest 用户请求
     * @return 微信用户信息请求
     */
    private WechatUserInfoRequest convertUserInfoRequest(OAuth2UserRequest userRequest) {
        Map<String, Object> metadata = userRequest.getClientRegistration().getProviderDetails().getConfigurationMetadata();
        String lang = MapUtil.getStr(metadata, "lang", "zh_CN");
        WechatUserInfoRequest request = new WechatUserInfoRequest();
        request.setAccessToken(userRequest.getAccessToken().getTokenValue());
        request.setOpenid(StrUtil.toString(userRequest.getAdditionalParameters().get("openid")));
        request.setLang(lang);
        return request;
    }

    /**
     * 转换用户
     *
     * @param response 微信用户信息响应
     * @param scopes   权限
     * @return OAuth2 用户
     */
    private OAuth2User convertUserInfoResponse(WechatUserInfoResponse response, Set<String> scopes) {
        // 转换为 OAuth2 用户
        Map<String, Object> attributes = BeanUtil.beanToMap(response);
        // 设置权限
        Set<GrantedAuthority> authorities = scopes.stream()
                .map(scope -> new SimpleGrantedAuthority("SCOPE_" + scope))
                .collect(Collectors.toSet());
        // 返回用户
        return new DefaultOAuth2User(authorities, attributes, "unionid");
    }
}
