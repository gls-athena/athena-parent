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
     * <p>根据认证模式(AS_AK/STS)构造不同的客户端实例，支持基础AK/SK认证和STS临时凭证认证两种方式</p>
     *
     * @param properties OSS配置属性，包含终端节点、认证信息、客户端配置等必要参数
     * @return OSS 客户端实例，已根据认证模式完成初始化配置
     * @throws IllegalArgumentException 当传入空参数、必要参数缺失或使用不支持的认证模式时抛出
     */
    @Bean
    @ConditionalOnMissingBean
    public OSS createOssClient(AliyunOssProperties properties) {
        // 基础参数校验
        Assert.notNull(properties, "OSS properties must not be null");
        Assert.hasText(properties.getEndpoint(), "OSS endpoint must not be empty");
        Assert.hasText(properties.getAccessKeyId(), "AccessKeyId must not be empty");
        Assert.hasText(properties.getAccessKeySecret(), "AccessKeySecret must not be empty");

        // 处理基础AK/SK认证模式
        if (AliyunCoreProperties.AuthMode.AS_AK.equals(properties.getAuthMode())) {
            return new OSSClientBuilder().build(
                    properties.getEndpoint(),
                    properties.getAccessKeyId(),
                    properties.getAccessKeySecret(),
                    properties.getConfig()
            );
        }

        // 处理STS临时凭证认证模式
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

        // 认证模式校验失败处理
        throw new IllegalArgumentException("Unsupported authentication mode: " + properties.getAuthMode());
    }

    /**
     * 创建并注册OSS协议解析器Bean到Spring容器
     * <p>
     * 该方法使用@Bean注解将OssProtocolResolver实例声明为Spring管理的Bean，
     * 并通过@ConditionalOnMissingBean确保仅当容器中不存在同类型Bean时才创建，
     * 避免Bean重复定义问题
     *
     * @return OSS协议解析器实例，该实例将用于解析OSS协议的资源定位
     */
    @Bean
    @ConditionalOnMissingBean
    public OssProtocolResolver createOssProtocolResolver() {
        // 直接实例化OSS协议解析器，该对象将被Spring容器接管生命周期管理
        return new OssProtocolResolver();
    }

    /**
     * 创建OSS端点实例
     * <p>
     * 使用@ConditionalOnMissingBean确保仅在当前Spring上下文中不存在OssEndpoint类型的Bean时才会创建。
     * 该条件检查机制可以避免Bean的重复定义，支持自定义实现覆盖默认配置。
     *
     * @return OssEndpoint 返回新创建的OSS端点实例，当且仅当容器中不存在该类型的Bean时创建
     * @see ConditionalOnMissingBean 当且仅当指定的bean类不在容器中存在时条件成立
     */
    @Bean
    @ConditionalOnMissingBean
    public OssEndpoint createOssEndpoint() {
        return new OssEndpoint();
    }

}
