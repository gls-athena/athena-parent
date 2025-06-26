package com.gls.athena.security.servlet.client.wechat;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.gls.athena.security.servlet.client.delegate.ISocialLoginAdapter;
import com.gls.athena.security.servlet.client.wechat.domain.MiniAccessTokenResponse;
import com.gls.athena.security.servlet.client.wechat.domain.MiniUserInfoRequest;
import com.gls.athena.security.servlet.client.wechat.domain.MiniUserInfoResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 小程序登录定制器
 *
 * @author george
 */
@Component
public class WechatMiniLoginAdapter implements ISocialLoginAdapter {

    /**
     * 测试是否支持
     *
     * @param registrationId 客户端注册 ID
     * @return 是否支持
     */
    @Override
    public boolean test(String registrationId) {
        return IWechatConstants.WECHAT_MINI_PROVIDER_ID.equals(registrationId);
    }

    /**
     * 自定义 OAuth2 授权请求
     *
     * @param builder 构建器
     * @param request 请求
     */
    @Override
    public void accept(OAuth2AuthorizationRequest.Builder builder, HttpServletRequest request, ClientRegistration clientRegistration) {
        // 小程序 OAuth2 授权请求参数处理
        builder.parameters(parameters -> {
            Map<String, Object> result = new HashMap<>(2);
            result.put("code", request.getParameter("code"));
            result.put("state", parameters.get("state"));
            parameters.clear();
            parameters.putAll(result);
        });
    }

    /**
     * 获取令牌响应
     *
     * @param authorizationGrantRequest 授权请求
     * @return 令牌响应
     */
    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest) {
        // 获取令牌请求
        String code = authorizationGrantRequest.getAuthorizationExchange().getAuthorizationResponse().getCode();
        // 获取小程序访问令牌
        String appId = authorizationGrantRequest.getClientRegistration().getClientId();
        // 获取小程序密钥
        String appSecret = authorizationGrantRequest.getClientRegistration().getClientSecret();
        // 获取小程序访问令牌 URI
        String appAccessTokenUri = authorizationGrantRequest.getClientRegistration().getProviderDetails().getTokenUri();
        // 获取小程序访问令牌响应
        MiniAccessTokenResponse response = WechatHelper.getMiniAccessToken(appId, appSecret, appAccessTokenUri);
        // 转换令牌响应
        Set<String> scopes = authorizationGrantRequest.getClientRegistration().getScopes();
        return convertResponse(response, scopes, code);
    }

    /**
     * 转换响应 令牌响应
     *
     * @param response 响应
     * @param code     授权码
     * @return 令牌响应
     */
    private OAuth2AccessTokenResponse convertResponse(MiniAccessTokenResponse response, Set<String> scopes, String code) {
        return OAuth2AccessTokenResponse.withToken(response.getAccessToken())
                .expiresIn(response.getExpiresIn())
                .scopes(scopes)
                .tokenType(OAuth2AccessToken.TokenType.BEARER)
                .additionalParameters(convertAdditionalParameters(code))
                .build();
    }

    /**
     * 转换附加参数
     *
     * @param code 授权码
     * @return 附加参数
     */
    private Map<String, Object> convertAdditionalParameters(String code) {
        Map<String, Object> additionalParameters = new HashMap<>();
        additionalParameters.put("js_code", code);
        return additionalParameters;
    }

    /**
     * 加载用户
     *
     * @param userRequest 用户请求
     * @return 用户
     * @throws OAuth2AuthenticationException OAuth2 认证异常
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        MiniUserInfoRequest request = convertUserInfoRequest(userRequest);
        // 获取用户信息地址
        ClientRegistration.ProviderDetails.UserInfoEndpoint userInfoEndpoint = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint();
        // 获取令牌
        OAuth2AccessToken accessToken = userRequest.getAccessToken();
        MiniUserInfoResponse response = WechatHelper.getMiniUserInfo(request, userInfoEndpoint.getUri());
        return convertUserInfoResponse(response, accessToken.getScopes(), userInfoEndpoint.getUserNameAttributeName());
    }

    /**
     * 转换小程序登录请求
     *
     * @param userRequest 用户请求
     * @return 小程序登录请求
     */
    private MiniUserInfoRequest convertUserInfoRequest(OAuth2UserRequest userRequest) {
        MiniUserInfoRequest request = new MiniUserInfoRequest();
        request.setAppId(userRequest.getClientRegistration().getClientId());
        request.setSecret(userRequest.getClientRegistration().getClientSecret());
        request.setJsCode(StrUtil.toString(userRequest.getAdditionalParameters().get("js_code")));
        return request;
    }

    /**
     * 转换小程序登录响应
     *
     * @param response 响应
     * @param scopes   权限
     * @return OAuth2 用户
     */
    private OAuth2User convertUserInfoResponse(MiniUserInfoResponse response, Set<String> scopes, String nameAttributeKey) {
        // 转换为 OAuth2 用户
        Map<String, Object> attributes = BeanUtil.beanToMap(response);
        if (MapUtil.getStr(attributes, nameAttributeKey, null) == null) {
            throw new OAuth2AuthenticationException(new OAuth2Error("wechat_mini_user_info_error", "获取小程序用户信息失败", null));
        }
        // 设置权限
        Set<GrantedAuthority> authorities = scopes.stream()
                .map(scope -> new SimpleGrantedAuthority("SCOPE_" + scope))
                .collect(Collectors.toSet());
        // 返回用户
        return new DefaultOAuth2User(authorities, attributes, nameAttributeKey);
    }
}
