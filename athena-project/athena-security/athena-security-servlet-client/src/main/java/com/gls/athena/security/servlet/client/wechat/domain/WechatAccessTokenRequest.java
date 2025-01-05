package com.gls.athena.security.servlet.client.wechat.domain;

import lombok.Data;

/**
 * 微信访问令牌请求
 *
 * @author george
 */
@Data
public class WechatAccessTokenRequest {
    /**
     * 应用 ID
     */
    private String appid;
    /**
     * 应用密钥
     */
    private String secret;
    /**
     * 授权码
     */
    private String code;
    /**
     * 授权类型
     */
    private String grantType = "authorization_code";
}
