package com.gls.athena.common.core.constant;

import com.gls.athena.common.bean.base.IEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 客户端类型枚举
 *
 * @author george
 */
@Getter
@RequiredArgsConstructor
public enum ClientTypeEnums implements IEnum<String> {

    /**
     * WEB
     */
    WEB("WEB", "web端"),
    /**
     * APP
     */
    APP("APP", "app端"),
    /**
     * MINI
     */
    MINI("MINI", "小程序端"),
    /**
     * H5
     */
    H5("H5", "H5端"),
    /**
     * WECHAT
     */
    WECHAT("WECHAT", "微信端"),
    /**
     * ALIPAY
     */
    ALIPAY("ALIPAY", "支付宝端"),
    /**
     * FEIGN
     */
    FEIGN("FEIGN", "feign端");
    /**
     * 代码
     */
    private final String code;
    /**
     * 描述
     */
    private final String name;
}
