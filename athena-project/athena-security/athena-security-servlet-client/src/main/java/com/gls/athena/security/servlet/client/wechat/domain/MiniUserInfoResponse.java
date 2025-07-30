package com.gls.athena.security.servlet.client.wechat.domain;

import lombok.Data;

/**
 * 小程序登录响应
 *
 * @author george
 */
@Data
public class MiniUserInfoResponse {
    /**
     * 会话密钥
     */
    private String sessionKey;
    /**
     * 用户在开放平台的唯一标识符，若当前小程序已绑定到微信开放平台账号下会返回，详见 UnionID 机制说明。
     */
    private String unionid;
    /**
     * 用户唯一标识
     */
    private String openid;
    /**
     * 错误码
     */
    private Integer errcode;
    /**
     * 错误消息
     */
    private String errmsg;
}
