package com.gls.athena.sdk.message.domain;

import com.gls.athena.common.bean.base.IEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 消息类型
 *
 * @author george
 */
@Getter
@RequiredArgsConstructor
public enum MessageType implements IEnum<String> {
    /**
     * 短信
     */
    SMS("sms", "短信"),
    /**
     * 邮件
     */
    EMAIL("email", "邮件"),
    /**
     * 站内信
     */
    SITE_MESSAGE("site_message", "站内信"),
    /**
     * 微信
     */
    WECHAT("wechat", "微信"),
    /**
     * 钉钉
     */
    DING_TALK("ding_talk", "钉钉"),
    /**
     * 飞书
     */
    FEISHU("feishu", "飞书"),
    /**
     * 企业微信
     */
    WECHAT_WORK("wechat_work", "企业微信"),
    ;

    private final String code;

    private final String name;
}
