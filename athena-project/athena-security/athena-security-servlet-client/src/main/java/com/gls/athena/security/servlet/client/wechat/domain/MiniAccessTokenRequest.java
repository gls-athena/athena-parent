package com.gls.athena.security.servlet.client.wechat.domain;

import lombok.Data;

/**
 * 应用访问令牌请求
 *
 * @author george
 */
@Data
public class MiniAccessTokenRequest {
    /**
     * 应用标识
     */
    private String appid;
    /**
     * 应用密钥
     */
    private String secret;
    /**
     * 授权类型
     */
    private String grantType;
}
