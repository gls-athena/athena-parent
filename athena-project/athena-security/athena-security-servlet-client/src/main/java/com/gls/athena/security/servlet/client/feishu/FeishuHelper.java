package com.gls.athena.security.servlet.client.feishu;

import com.gls.athena.security.servlet.client.feishu.domian.*;
import com.gls.athena.starter.data.redis.support.RedisUtil;
import lombok.experimental.UtilityClass;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * 飞书助手
 *
 * @author george
 */
@UtilityClass
public class FeishuHelper {
    /**
     * 飞书应用访问令牌缓存名称
     */
    private static final String APP_ACCESS_TOKEN_CACHE_NAME = "feishu:app_access_token";

    /**
     * 获取应用访问令牌
     *
     * @param clientId          客户端ID
     * @param clientSecret      客户端密钥
     * @param appAccessTokenUri 应用访问令牌地址
     * @return 应用访问令牌
     */
    public String getAppAccessToken(String clientId, String clientSecret, String appAccessTokenUri) {
        // 从缓存中获取应用访问令牌
        FeishuAppAccessTokenResponse response = RedisUtil.getCacheValue(APP_ACCESS_TOKEN_CACHE_NAME, clientId, FeishuAppAccessTokenResponse.class);
        if (response != null) {
            return response.getAppAccessToken();
        }
        // 请求应用访问令牌
        FeishuAppAccessTokenRequest request = new FeishuAppAccessTokenRequest();
        request.setAppId(clientId);
        request.setAppSecret(clientSecret);
        // 获取应用访问令牌
        response = getAppAccessToken(request, appAccessTokenUri);
        // 缓存应用访问令牌
        if (response != null) {
            RedisUtil.setCacheValue(APP_ACCESS_TOKEN_CACHE_NAME, clientId, response, response.getExpire(), TimeUnit.SECONDS);
            return response.getAppAccessToken();
        }
        // 返回空
        return null;
    }

    /**
     * 获取应用访问令牌
     *
     * @param request           应用访问令牌请求
     * @param appAccessTokenUri 应用访问令牌地址
     * @return 应用访问令牌
     */
    private FeishuAppAccessTokenResponse getAppAccessToken(FeishuAppAccessTokenRequest request, String appAccessTokenUri) {
        // 请求飞书接口
        RestTemplate restTemplate = new RestTemplate();
        // 请求实体
        RequestEntity<FeishuAppAccessTokenRequest> requestEntity = RequestEntity
                .post(URI.create(appAccessTokenUri))
                .header("Content-Type", "application/json; charset=UTF-8")
                .body(request);
        // 返回应用访问令牌
        return restTemplate.exchange(requestEntity, FeishuAppAccessTokenResponse.class).getBody();
    }

    /**
     * 获取用户访问令牌
     *
     * @param request            用户访问令牌请求
     * @param userAccessTokenUri 用户访问令牌地址
     * @param appAccessToken     应用访问令牌
     * @return 用户访问令牌
     */
    public FeishuUserAccessTokenResponse getUserAccessToken(FeishuUserAccessTokenRequest request, String userAccessTokenUri, String appAccessToken) {
        // 请求飞书接口
        RestTemplate restTemplate = new RestTemplate();
        // 请求实体
        RequestEntity<FeishuUserAccessTokenRequest> requestEntity = RequestEntity
                .post(URI.create(userAccessTokenUri))
                .header("Content-Type", "application/json; charset=UTF-8")
                .header("Authorization", "Bearer " + appAccessToken)
                .body(request);
        // 返回用户访问令牌
        FeishuResponse<FeishuUserAccessTokenResponse> response = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<FeishuResponse<FeishuUserAccessTokenResponse>>() {
        }).getBody();
        if (response != null) {
            return response.getData();
        }
        return null;
    }

    /**
     * 获取用户信息
     *
     * @param userAccessToken 用户访问令牌
     * @param userInfoUri     用户信息地址
     * @return 用户信息
     */
    public FeishuUserInfoResponse getUserInfo(String userAccessToken, String userInfoUri) {
        // 请求飞书接口
        RestTemplate restTemplate = new RestTemplate();
        // 请求实体
        RequestEntity<?> requestEntity = RequestEntity
                .get(URI.create(userInfoUri))
                .header("Content-Type", "application/json; charset=UTF-8")
                .header("Authorization", "Bearer " + userAccessToken)
                .build();
        // 返回用户信息
        FeishuResponse<FeishuUserInfoResponse> response = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<FeishuResponse<FeishuUserInfoResponse>>() {
        }).getBody();
        if (response != null) {
            return response.getData();
        }
        return null;
    }

}
