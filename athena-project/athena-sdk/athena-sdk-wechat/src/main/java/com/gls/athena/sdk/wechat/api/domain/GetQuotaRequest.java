package com.gls.athena.sdk.wechat.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 查询API调用额度请求对象，包含接口调用凭证和API请求路径
 *
 * @author george
 * @see <a href="https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/openApi-mgnt/getApiQuota.html">查询API调用额度接口说明</a>
 */
@Data
public class GetQuotaRequest {
    /**
     * 接口调用凭证
     * 用于验证调用者的身份和权限
     */
    @JsonProperty("access_token")
    private String accessToken;

    /**
     * API的请求路径
     * 例如："/cgi-bin/message/custom/send"
     * 用于指定需要查询调用额度的具体API接口
     */
    @JsonProperty("cgi_path")
    private String cgiPath;
}
