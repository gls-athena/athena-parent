package com.gls.athena.common.core.constant;

import com.gls.athena.common.bean.base.IEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 客户端类型枚举
 * 用于定义系统支持的各种客户端类型，包括Web端、App端、小程序端等
 *
 * @author george
 */
@Getter
@RequiredArgsConstructor
public enum ClientTypeEnums implements IEnum<String> {

    /**
     * WEB端客户端类型
     */
    WEB("WEB", "web端"),
    /**
     * APP端客户端类型
     */
    APP("APP", "app端"),
    /**
     * 小程序端客户端类型
     */
    MINI("MINI", "小程序端"),
    /**
     * H5端客户端类型
     */
    H5("H5", "H5端"),
    /**
     * 微信端客户端类型
     */
    WECHAT("WECHAT", "微信端"),
    /**
     * 支付宝端客户端类型
     */
    ALIPAY("ALIPAY", "支付宝端"),
    /**
     * Feign客户端类型
     */
    FEIGN("FEIGN", "feign端");

    /**
     * 客户端类型代码
     */
    private final String code;

    /**
     * 客户端类型描述
     */
    private final String name;
}
