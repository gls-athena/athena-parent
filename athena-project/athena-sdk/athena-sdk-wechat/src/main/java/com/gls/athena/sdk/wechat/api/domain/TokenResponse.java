package com.gls.athena.sdk.wechat.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 微信访问令牌响应实体类
 * 用于封装微信接口返回的访问令牌信息
 *
 * @author george
 * @since 1.0.0
 */
@Data
public class TokenResponse {

    /**
     * 访问令牌
     * 调用微信接口时需要使用的凭证
     */
    @JsonProperty("access_token")
    private String accessToken;

    /**
     * 凭证有效期，单位：秒
     * access_token接口调用凭证超时时间，单位（秒）
     */
    @JsonProperty("expires_in")
    private Long expiresIn;
}
