package com.gls.athena.starter.aliyun.oss.config;

/**
 * 阿里云认证模式枚举
 * 定义了支持的认证方式类型
 */
public enum AliyunAuthEnums {
    /**
     * AccessKey认证模式
     * 使用访问密钥ID和密钥进行认证
     */
    AS_AK,

    /**
     * Security Token Service认证模式
     * 使用临时安全凭证进行认证
     */
    STS
}
