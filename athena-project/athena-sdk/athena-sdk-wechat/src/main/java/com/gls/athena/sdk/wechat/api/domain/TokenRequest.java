package com.gls.athena.sdk.wechat.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 微信接口调用凭证获取请求
 * 用于请求微信服务器获取access_token
 *
 * @author george
 * @see <a href="https://developers.weixin.qq.com/doc/offiaccount/Basic_Information/Get_access_token.html">获取access_token</a>
 */
@Data
public class TokenRequest {

    /**
     * 获取access_token填写client_credential
     * 目前仅支持client_credential模式
     */
    @JsonProperty("grant_type")
    private String grantType = "client_credential";

    /**
     * 第三方用户唯一凭证
     * 即微信公众号的AppID
     */
    @JsonProperty("appid")
    private String appId;

    /**
     * 第三方用户唯一凭证密钥
     * 即微信公众号的AppSecret
     */
    @JsonProperty("secret")
    private String secret;
}
