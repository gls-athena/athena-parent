package com.gls.athena.security.servlet.client.wechat;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.gls.athena.security.servlet.client.delegate.IOAuth2LoginAdapter;
import com.gls.athena.security.servlet.client.wechat.domain.*;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 企业微信登录定制器
 *
 * @author george
 */
@Component
public class WechatWorkLoginAdapter implements IOAuth2LoginAdapter {

    /**
     * 测试是否支持指定的注册标识
     *
     * @param registrationId 注册标识
     * @return 是否支持
     */
    @Override
    public boolean test(String registrationId) {
        return IWechatConstants.WECHAT_WORK_PROVIDER_ID.equals(registrationId);
    }

    /**
     * 自定义 OAuth2 授权请求
     *
     * @param builder 构建器
     * @param request 请求
     */
    @Override
    public void accept(OAuth2AuthorizationRequest.Builder builder, HttpServletRequest request, ClientRegistration clientRegistration) {
        Map<String, Object> metadata = clientRegistration.getProviderDetails().getConfigurationMetadata();
        // 企业微信 OAuth2 授权请求参数处理
        builder.parameters(parameters -> {
            // 企业微信 OAuth2 授权请求参数处理
            Map<String, Object> map = new HashMap<>(6);
            map.put("login_type", MapUtil.getStr(metadata, "login_type"));
            map.put("appId", parameters.get(OAuth2ParameterNames.CLIENT_ID));
            map.put("agentid", MapUtil.getStr(metadata, "agentid"));
            map.put("redirect_uri", parameters.get(OAuth2ParameterNames.REDIRECT_URI));
            map.put("state", parameters.get(OAuth2ParameterNames.STATE));
            map.put("lang", MapUtil.getStr(metadata, "lang"));
            parameters.clear();
            parameters.putAll(map);
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
        String code = authorizationGrantRequest.getAuthorizationExchange().getAuthorizationResponse().getCode();
        String corpId = authorizationGrantRequest.getClientRegistration().getClientId();
        String corpSecret = authorizationGrantRequest.getClientRegistration().getClientSecret();
        String accessTokenUri = authorizationGrantRequest.getClientRegistration().getProviderDetails().getTokenUri();
        WorkAccessTokenResponse response = WechatHelper.getWorkAccessToken(corpId, corpSecret, accessTokenUri);
        Set<String> scopes = authorizationGrantRequest.getClientRegistration().getScopes();
        return convertResponse(response, scopes, code);
    }

    /**
     * 转换响应
     *
     * @param response 响应
     * @param code     授权码
     * @return 令牌响应
     */
    private OAuth2AccessTokenResponse convertResponse(WorkAccessTokenResponse response, Set<String> scopes, String code) {
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
        additionalParameters.put("code", code);
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
        WorkUserLoginRequest request = convertUserLoginRequest(userRequest);
        Map<String, Object> metadata = userRequest.getClientRegistration().getProviderDetails().getConfigurationMetadata();
        String userLoginUri = MapUtil.getStr(metadata, IWechatConstants.WECHAT_WORK_USER_LOGIN_URI_NAME);
        WorkUserLoginResponse response = WechatHelper.getWorkUserLogin(request, userLoginUri);
        if (StrUtil.isBlank(response.getUserid())) {
            throw new OAuth2AuthenticationException("获取企业微信用户登录身份失败");
        }
        WorkUserInfoRequest userInfoRequest = convertUserInfoRequest(userRequest, response.getUserid());
        String userInfoUri = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri();
        WorkUserInfoResponse userInfoResponse = WechatHelper.getWorkUserInfo(userInfoRequest, userInfoUri);
        return convertUser(userInfoResponse, userRequest.getAccessToken().getScopes());
    }

    /**
     * 转换请求
     *
     * @param userRequest 用户请求
     * @return 企业微信用户登录请求
     */
    private WorkUserLoginRequest convertUserLoginRequest(OAuth2UserRequest userRequest) {
        WorkUserLoginRequest request = new WorkUserLoginRequest();
        request.setAccessToken(userRequest.getAccessToken().getTokenValue());
        request.setCode(StrUtil.toString(userRequest.getAdditionalParameters().get("code")));
        return request;
    }

    /**
     * 转换用户
     *
     * @param userRequest 用户请求
     * @param userid      用户标识
     * @return 企业微信用户信息请求
     */
    private WorkUserInfoRequest convertUserInfoRequest(OAuth2UserRequest userRequest, String userid) {
        WorkUserInfoRequest request = new WorkUserInfoRequest();
        request.setAccessToken(userRequest.getAccessToken().getTokenValue());
        request.setUserid(userid);
        return request;
    }

    /**
     * 转换用户
     *
     * @param response 微信用户信息响应
     * @param scopes   权限
     * @return OAuth2 用户
     */
    private OAuth2User convertUser(WorkUserInfoResponse response, Set<String> scopes) {
        // 转换为 OAuth2 用户
        Map<String, Object> attributes = BeanUtil.beanToMap(response);
        // 设置权限
        Set<GrantedAuthority> authorities = scopes.stream()
                .map(scope -> new SimpleGrantedAuthority("SCOPE_" + scope))
                .collect(Collectors.toSet());
        // 返回用户
        return new DefaultOAuth2User(authorities, attributes, "userid");
    }
}
