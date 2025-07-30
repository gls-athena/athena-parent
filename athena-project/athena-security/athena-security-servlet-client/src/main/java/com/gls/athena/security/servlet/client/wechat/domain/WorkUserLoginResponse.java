package com.gls.athena.security.servlet.client.wechat.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 企业微信用户登录响应
 *
 * @author george
 */
@Data
public class WorkUserLoginResponse {
    /**
     * 错误码
     */
    private Integer errcode;
    /**
     * 错误消息
     */
    private String errmsg;
    /**
     * 成员userid
     */
    private String userid;
    /**
     * 非企业成员的标识，对当前企业唯一。不超过64字节
     */
    private String openid;
    /**
     * 外部联系人userid
     */
    @JsonProperty("external_userid")
    private String externalUserid;
}
