package com.gls.athena.sdk.wechat.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 查询API调用额度请求对象
 *
 * @author george
 */
@Data
public class GetQuotaRequest {
    /**
     * 接口调用凭证
     */
    @JsonProperty("access_token")
    private String accessToken;

    /**
     * API的请求路径，例如"/cgi-bin/message/custom/send"
     */
    @JsonProperty("cgi_path")
    private String cgiPath;
}
