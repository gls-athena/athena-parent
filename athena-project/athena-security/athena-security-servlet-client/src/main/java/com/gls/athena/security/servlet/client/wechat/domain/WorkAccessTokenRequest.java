package com.gls.athena.security.servlet.client.wechat.domain;

import lombok.Data;

/**
 * 企业微信访问令牌请求
 *
 * @author george
 */
@Data
public class WorkAccessTokenRequest {
    /**
     * 企业ID
     */
    private String corpid;
    /**
     * 应用密钥
     */
    private String corpsecret;
}
