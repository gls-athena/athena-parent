package com.gls.athena.starter.aliyun.oss.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.gls.athena.starter.aliyun.core.config.AliyunCoreProperties;
import com.gls.athena.starter.aliyun.oss.endpoint.OssEndpoint;
import com.gls.athena.starter.aliyun.oss.support.OssProtocolResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

/**
 * 阿里云OSS自动配置类
 * <p>
 * 提供OSS客户端、协议解析器和端点的自动配置
 *
 * @author george
 */
@Configuration
public class AliyunOssConfig {

    /**
     * 创建阿里云OSS客户端实例
     *
     * @param properties OSS配置属性
     * @return OSS客户端实例
     * @throws IllegalArgumentException 当认证模式不支持时抛出
     */
    @Bean
    @ConditionalOnMissingBean
    public OSS createOssClient(AliyunOssProperties properties) {
        Assert.notNull(properties, "OSS properties must not be null");
        Assert.hasText(properties.getEndpoint(), "OSS endpoint must not be empty");
        Assert.hasText(properties.getAccessKeyId(), "AccessKeyId must not be empty");
        Assert.hasText(properties.getAccessKeySecret(), "AccessKeySecret must not be empty");

        if (AliyunCoreProperties.AuthMode.AS_AK.equals(properties.getAuthMode())) {
            return new OSSClientBuilder().build(
                    properties.getEndpoint(),
                    properties.getAccessKeyId(),
                    properties.getAccessKeySecret(),
                    properties.getConfig()
            );
        }

        if (AliyunCoreProperties.AuthMode.STS.equals(properties.getAuthMode())) {
            Assert.hasText(properties.getSecurityToken(), "SecurityToken must not be empty for STS mode");
            return new OSSClientBuilder().build(
                    properties.getEndpoint(),
                    properties.getAccessKeyId(),
                    properties.getAccessKeySecret(),
                    properties.getSecurityToken(),
                    properties.getConfig()
            );
        }

        throw new IllegalArgumentException("Unsupported authentication mode: " + properties.getAuthMode());
    }

    /**
     * 创建OSS协议解析器
     *
     * @return OSS协议解析器实例
     */
    @Bean
    @ConditionalOnMissingBean
    public OssProtocolResolver createOssProtocolResolver() {
        return new OssProtocolResolver();
    }

    /**
     * 创建OSS端点
     *
     * @return OSS端点实例
     */
    @Bean
    @ConditionalOnMissingBean
    public OssEndpoint createOssEndpoint() {
        return new OssEndpoint();
    }
}
