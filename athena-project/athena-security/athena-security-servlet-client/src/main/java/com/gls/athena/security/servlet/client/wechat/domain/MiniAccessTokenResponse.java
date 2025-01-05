package com.gls.athena.security.servlet.client.wechat.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 应用访问令牌响应
 *
 * @author George
 */
@Data
public class MiniAccessTokenResponse {
    /**
     * 应用访问令牌
     */
    @JsonProperty("access_token")
    private String accessToken;
    /**
     * 过期时间
     */
    @JsonProperty("expires_in")
    private Integer expiresIn;
}
