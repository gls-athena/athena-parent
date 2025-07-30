package com.gls.athena.security.servlet.client.wechat.domain;

import lombok.Data;

/**
 * 微信用户信息请求
 *
 * @author george
 */
@Data
public class WechatUserInfoRequest {
    /**
     * 访问令牌
     */
    private String accessToken;
    /**
     * 用户标识
     */
    private String openid;
    /**
     * 语言
     */
    private String lang = "zh_CN";
}
