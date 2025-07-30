package com.gls.athena.security.servlet.client.wechat;

import com.gls.athena.security.servlet.client.wechat.domain.*;
import com.gls.athena.starter.data.redis.support.RedisUtil;
import lombok.experimental.UtilityClass;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * 微信服务工具类
 * 集成微信小程序、公众号及企业微信API调用功能
 * 包含访问令牌管理、用户信息获取等核心功能
 *
 * @author george
 */
@UtilityClass
public class WechatHelper {
    /**
     * Redis缓存键前缀 - 小程序访问令牌
     */
    private static final String CACHE_PREFIX_MINI = "wechat_mini:access_token";
    /**
     * Redis缓存键前缀 - 企业微信访问令牌
     */
    private static final String CACHE_PREFIX_WORK = "wechat_work:access_token";

    /**
     * 初始化RestTemplate，添加微信专用消息转换器
     */
    private RestTemplate createRestTemplate() {
        RestTemplate template = new RestTemplate();
        template.getMessageConverters().add(new WechatHttpMessageConverter());
        return template;
    }

    /**
     * 执行GET请求并处理响应
     *
     * @param uri          目标URI
     * @param responseType 期望的响应类型
     * @return 响应对象
     */
    private <T> T executeGet(URI uri, Class<T> responseType) {
        return createRestTemplate()
                .exchange(RequestEntity.get(uri).build(), responseType)
                .getBody();
    }

    /**
     * 获取小程序访问令牌（优先从缓存获取）
     *
     * @param appId             小程序APPID
     * @param appSecret         小程序密钥
     * @param appAccessTokenUri 获取令牌的API地址
     * @return 访问令牌响应对象
     */
    public MiniAccessTokenResponse getMiniAccessToken(String appId, String appSecret, String appAccessTokenUri) {
        // 尝试从缓存获取
        MiniAccessTokenResponse response = RedisUtil.getCacheValue(CACHE_PREFIX_MINI, appId, MiniAccessTokenResponse.class);
        if (response != null) {
            return response;
        }

        // 构建请求参数
        URI uri = UriComponentsBuilder.fromUriString(appAccessTokenUri)
                .queryParam("appid", appId)
                .queryParam("secret", appSecret)
                .queryParam("grant_type", "client_credential")
                .build().toUri();

        // 发起请求并缓存结果
        response = executeGet(uri, MiniAccessTokenResponse.class);
        if (response != null) {
            RedisUtil.setCacheValue(CACHE_PREFIX_MINI, appId, response,
                    response.getExpiresIn(), TimeUnit.SECONDS);
        }
        return response;
    }

    /**
     * 小程序用户登录
     *
     * @param request     包含code、appId等登录参数的请求对象
     * @param userInfoUri 登录验证API地址
     * @return 用户信息响应对象
     */
    public MiniUserInfoResponse getMiniUserInfo(MiniUserInfoRequest request, String userInfoUri) {
        URI uri = UriComponentsBuilder.fromUriString(userInfoUri)
                .queryParam("appid", request.getAppId())
                .queryParam("secret", request.getSecret())
                .queryParam("js_code", request.getJsCode())
                .queryParam("grant_type", request.getGrantType())
                .build().toUri();
        return executeGet(uri, MiniUserInfoResponse.class);
    }

    /**
     * 获取微信公众号访问令牌
     *
     * @param request        包含授权码等参数的请求对象
     * @param accessTokenUri 获取令牌的API地址
     * @return 访问令牌响应对象
     */
    public WechatAccessTokenResponse getWechatAccessToken(WechatAccessTokenRequest request, String accessTokenUri) {
        URI uri = UriComponentsBuilder.fromUriString(accessTokenUri)
                .queryParam("appid", request.getAppid())
                .queryParam("secret", request.getSecret())
                .queryParam("code", request.getCode())
                .queryParam("grant_type", request.getGrantType())
                .build().toUri();
        return executeGet(uri, WechatAccessTokenResponse.class);
    }

    /**
     * 获取微信公众号用户信息
     *
     * @param request     包含access_token和openid的请求对象
     * @param userInfoUri 用户信息API地址
     * @return 用户信息响应对象
     */
    public WechatUserInfoResponse getWechatUserInfo(WechatUserInfoRequest request, String userInfoUri) {
        URI uri = UriComponentsBuilder.fromUriString(userInfoUri)
                .queryParam("access_token", request.getAccessToken())
                .queryParam("openid", request.getOpenid())
                .queryParam("lang", request.getLang())
                .build().toUri();
        return executeGet(uri, WechatUserInfoResponse.class);
    }

    /**
     * 获取企业微信访问令牌（优先从缓存获取）
     *
     * @param corpid         企业微信ID
     * @param corpsecret     应用密钥
     * @param accessTokenUri 获取令牌的API地址
     * @return 访问令牌响应对象
     */
    public WorkAccessTokenResponse getWorkAccessToken(String corpid, String corpsecret, String accessTokenUri) {
        WorkAccessTokenResponse response = RedisUtil.getCacheValue(CACHE_PREFIX_WORK, corpid, WorkAccessTokenResponse.class);
        if (response != null) {
            return response;
        }
        URI uri = UriComponentsBuilder.fromUriString(accessTokenUri)
                .queryParam("corpid", corpid)
                .queryParam("corpsecret", corpsecret)
                .build().toUri();
        response = executeGet(uri, WorkAccessTokenResponse.class);
        if (response != null) {
            RedisUtil.setCacheValue(CACHE_PREFIX_WORK, corpid, response, response.getExpiresIn(), TimeUnit.SECONDS);
        }
        return response;
    }

    /**
     * 企业微信扫码登录身份验证
     *
     * @param request      包含临时授权码的请求对象
     * @param userLoginUri 身份验证API地址
     * @return 登录身份响应对象
     */
    public WorkUserLoginResponse getWorkUserLogin(WorkUserLoginRequest request, String userLoginUri) {
        URI uri = UriComponentsBuilder.fromUriString(userLoginUri)
                .queryParam("access_token", request.getAccessToken())
                .queryParam("code", request.getCode())
                .build().toUri();
        return executeGet(uri, WorkUserLoginResponse.class);
    }

    /**
     * 获取企业微信用户详细信息
     *
     * @param request     包含access_token和userid的请求对象
     * @param userInfoUri 用户信息API地址
     * @return 用户信息响应对象
     */
    public WorkUserInfoResponse getWorkUserInfo(WorkUserInfoRequest request, String userInfoUri) {
        URI uri = UriComponentsBuilder.fromUriString(userInfoUri)
                .queryParam("access_token", request.getAccessToken())
                .queryParam("userid", request.getUserid())
                .build().toUri();
        return executeGet(uri, WorkUserInfoResponse.class);
    }
}
