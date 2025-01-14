package com.gls.athena.sdk.wechat.api.domain;

import lombok.Data;

/**
 * 重置API调用次数请求
 *
 * @author george
 */
@Data
public class ClearQuotaRequest {
    /**
     * 要被清空的账号的appid
     */
    private String appid;
}
