package com.gls.athena.starter.aliyun.oss.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.gls.athena.starter.aliyun.oss.endpoint.OssEndpoint;
import com.gls.athena.starter.aliyun.oss.support.OssProtocolResolver;
import com.gls.athena.starter.aliyun.oss.support.OssResourceFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

/**
 * 阿里云OSS自动配置类
 * <p>
 * 提供OSS客户端、协议解析器和端点的自动配置。
 *
 * @author george
 */
@Configuration
public class AliyunOssConfig {

    @Bean
    @ConditionalOnMissingBean
    public OSS ossClient(AliyunOssProperties properties) {
        // 基础参数校验
        Assert.notNull(properties, "OSS properties must not be null");
        Assert.hasText(properties.getEndpoint(), "OSS endpoint must not be empty");
        Assert.hasText(properties.getAccessKeyId(), "AccessKeyId must not be empty");
        Assert.hasText(properties.getAccessKeySecret(), "AccessKeySecret must not be empty");

        // 处理基础AK/SK认证模式
        if (AuthenticationMode.AS_AK.equals(properties.getAuthMode())) {
            return new OSSClientBuilder().build(
                    properties.getEndpoint(),
                    properties.getAccessKeyId(),
                    properties.getAccessKeySecret(),
                    properties.getConfig()
            );
        }
        // 处理STS临时凭证认证模式
        else if (AuthenticationMode.STS.equals(properties.getAuthMode())) {
            Assert.hasText(properties.getSecurityToken(), "SecurityToken must not be empty for STS mode");
            return new OSSClientBuilder().build(
                    properties.getEndpoint(),
                    properties.getAccessKeyId(),
                    properties.getAccessKeySecret(),
                    properties.getSecurityToken(),
                    properties.getConfig()
            );
        }

        // 认证模式校验失败处理
        throw new IllegalArgumentException("Unsupported authentication mode: " + properties.getAuthMode());
    }

    @Bean
    @ConditionalOnMissingBean
    public OssProtocolResolver ossProtocolResolver(OssResourceFactory ossResourceFactory) {
        return new OssProtocolResolver(ossResourceFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public OssEndpoint ossEndpoint() {
        return new OssEndpoint();
    }
}
