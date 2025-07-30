package com.gls.athena.security.servlet.client.feishu.domian;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 应用访问令牌请求
 *
 * @author george
 */
@Data
public class FeishuAppAccessTokenRequest {
    /**
     * 应用 ID
     */
    @JsonProperty("app_id")
    private String appId;
    /**
     * 应用密钥
     */
    @JsonProperty("app_secret")
    private String appSecret;
}
