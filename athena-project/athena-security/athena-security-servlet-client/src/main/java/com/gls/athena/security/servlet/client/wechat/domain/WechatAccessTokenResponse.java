package com.gls.athena.security.servlet.client.wechat.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 微信访问令牌响应
 *
 * @author george
 */
@Data
public class WechatAccessTokenResponse {
    /**
     * 访问令牌
     */
    @JsonProperty("access_token")
    private String accessToken;
    /**
     * 超时时间
     */
    @JsonProperty("expires_in")
    private Long expiresIn;
    /**
     * 刷新令牌
     */
    @JsonProperty("refresh_token")
    private String refreshToken;
    /**
     * 用户标识
     */
    private String openid;
    /**
     * 授权范围
     */
    private String scope;
    /**
     * 用户统一标识
     */
    private String unionid;
}
