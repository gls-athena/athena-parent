package com.gls.athena.security.servlet.client.feishu.domian;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 应用访问令牌响应
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FeishuAppAccessTokenResponse extends FeishuResponse<String> {
    /**
     * 应用访问令牌
     */
    @JsonProperty("app_access_token")
    private String appAccessToken;
    /**
     * 过期时间
     */
    private Integer expire;
    /**
     * 租户访问令牌
     */
    @JsonProperty("tenant_access_token")
    private String tenantAccessToken;
}
