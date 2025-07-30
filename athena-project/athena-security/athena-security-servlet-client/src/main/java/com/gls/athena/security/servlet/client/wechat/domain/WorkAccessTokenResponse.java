package com.gls.athena.security.servlet.client.wechat.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 企业微信访问令牌响应
 *
 * @author george
 */
@Data
public class WorkAccessTokenResponse {
    /**
     * 错误码
     */
    private Integer errcode;
    /**
     * 错误消息
     */
    private String errmsg;
    /**
     * 访问令牌
     */
    @JsonProperty("access_token")
    private String accessToken;
    /**
     * 有效时间
     */
    @JsonProperty("expires_in")
    private Integer expiresIn;
}
