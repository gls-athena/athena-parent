package com.gls.athena.security.servlet.client.wechat.domain;

import lombok.Data;

/**
 * 小程序登录请求
 *
 * @author george
 */
@Data
public class MiniUserInfoRequest {
    /**
     * 小程序 appId
     */
    private String appId;
    /**
     * 小程序 appSecret
     */
    private String secret;
    /**
     * 登录时获取的 code，可通过wx.login获取
     */
    private String jsCode;
    /**
     * 授权类型，此处只需填写 authorization_code
     */
    private String grantType = "authorization_code";

}
