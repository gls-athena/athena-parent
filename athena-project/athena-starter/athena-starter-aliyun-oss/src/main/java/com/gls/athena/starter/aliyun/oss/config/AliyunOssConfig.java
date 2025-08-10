package com.gls.athena.starter.aliyun.oss.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.gls.athena.starter.aliyun.oss.endpoint.OssEndpoint;
import com.gls.athena.starter.aliyun.oss.support.OssProtocolResolver;
import com.gls.athena.starter.aliyun.oss.support.OssResourceFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.Assert;

import java.util.concurrent.ExecutorService;

/**
 * 阿里云OSS自动配置类
 * <p>
 * 提供OSS客户端、协议解析器和端点的自动配置。
 *
 * @author george
 */
@Configuration
public class AliyunOssConfig {

    /**
     * 创建并返回阿里云OSS客户端实例。
     * <p>
     * 支持以下两种认证模式：
     * <ul>
     *   <li>AK/SK模式：使用AccessKeyId和AccessKeySecret进行认证</li>
     *   <li>STS模式：使用临时安全令牌（SecurityToken）进行认证</li>
     * </ul>
     * <p>
     * 参数校验：
     * <ul>
     *   <li>properties、endpoint、accessKeyId、accessKeySecret不能为空</li>
     *   <li>STS模式下securityToken不能为空</li>
     * </ul>
     *
     * @param properties OSS配置属性，包含终端节点、认证信息、客户端配置等
     * @return 已初始化的OSS客户端实例
     * @throws IllegalArgumentException 参数为空、缺失或认证模式不支持时抛出
     */
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

    /**
     * 创建OSS任务执行器，用于异步上传等操作。
     *
     * @param properties OSS配置属性，从中获取线程池相关配置
     * @return ExecutorService 线程池执行器
     */
    @Bean("ossTaskExecutor")
    @ConditionalOnMissingBean(name = "ossTaskExecutor")
    public ExecutorService ossTaskExecutor(AliyunOssProperties properties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.getExecutor().getCorePoolSize());
        executor.setMaxPoolSize(properties.getExecutor().getMaxPoolSize());
        executor.setQueueCapacity(properties.getExecutor().getQueueCapacity());
        executor.setThreadNamePrefix("oss-task-");
        executor.setWaitForTasksToCompleteOnShutdown(properties.getExecutor().isWaitForTasksToCompleteOnShutdown());
        executor.setAwaitTerminationSeconds(properties.getExecutor().getAwaitTerminationSeconds());
        executor.initialize();
        return executor.getThreadPoolExecutor();
    }

    /**
     * 创建并注册OSS协议解析器Bean到Spring容器。
     *
     * @param ossResourceFactory OSS资源工厂
     * @return OSS协议解析器实例，该实例将用于解析OSS协议的资源定位
     */
    @Bean
    @ConditionalOnMissingBean
    public OssProtocolResolver ossProtocolResolver(OssResourceFactory ossResourceFactory) {
        return new OssProtocolResolver(ossResourceFactory);
    }

    /**
     * 创建OSS端点实例。
     *
     * @return OssEndpoint 返回新创建的OSS端点实例，当且仅当容器中不存在该类型的Bean时创建
     */
    @Bean
    @ConditionalOnMissingBean
    public OssEndpoint ossEndpoint() {
        return new OssEndpoint();
    }
}
