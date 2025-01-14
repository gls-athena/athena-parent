package com.gls.athena.sdk.wechat.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 重置API调用次数响应
 *
 * @author george
 */
@Data
public class ClearQuotaResponse {
    /**
     * 错误码
     */
    @JsonProperty("errcode")
    private Integer errCode;
    /**
     * 错误信息
     */
    @JsonProperty("errmsg")
    private String errMsg;
}
