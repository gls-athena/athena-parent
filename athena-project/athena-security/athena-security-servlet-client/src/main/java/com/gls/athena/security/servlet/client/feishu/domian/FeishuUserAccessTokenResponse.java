package com.gls.athena.security.servlet.client.feishu.domian;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 飞书用户访问令牌响应
 *
 * @author george
 */
@Data
public class FeishuUserAccessTokenResponse {
    /**
     * 访问令牌
     */
    @JsonProperty("access_token")
    private String accessToken;
    /**
     * 刷新令牌
     */
    @JsonProperty("refresh_token")
    private String refreshToken;
    /**
     * 令牌类型
     */
    @JsonProperty("token_type")
    private String tokenType;
    /**
     * 令牌有效期
     */
    @JsonProperty("expires_in")
    private Integer expiresIn;
    /**
     * 刷新令牌有效期
     */
    @JsonProperty("refresh_expires_in")
    private Integer refreshExpiresIn;
    /**
     * 用户授予的权限范围
     */
    private String scope;
}
