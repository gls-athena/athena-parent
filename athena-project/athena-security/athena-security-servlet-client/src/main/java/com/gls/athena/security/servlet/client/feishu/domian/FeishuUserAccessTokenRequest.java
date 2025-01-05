package com.gls.athena.security.servlet.client.feishu.domian;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 飞书用户访问令牌请求
 *
 * @author george
 */
@Data
public class FeishuUserAccessTokenRequest {
    /**
     * 授权类型，固定值：authorization_code
     */
    @JsonProperty("grant_type")
    private String grantType = "authorization_code";
    /**
     * 登录预授权码 调用登录预授权码 获取code
     */
    private String code;
}
