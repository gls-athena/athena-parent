package com.gls.athena.starter.aliyun.oss.config;

import com.gls.athena.common.bean.base.IEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 阿里云OSS认证模式枚举。
 * <p>定义了支持的认证方式类型，包括AccessKey和STS。</p>
 *
 * @author george
 */
@Getter
@RequiredArgsConstructor
public enum AuthenticationMode implements IEnum<String> {

    /**
     * AccessKey认证模式（使用AccessKey ID和Secret进行身份验证）
     */
    AS_AK("as-ak", "AccessKey认证模式"),

    /**
     * STS认证模式（使用安全令牌服务进行临时授权）
     */
    STS("sts", "Security Token Service认证模式");

    /**
     * 认证模式编码
     */
    private final String code;
    /**
     * 认证模式名称
     */
    private final String name;
}

