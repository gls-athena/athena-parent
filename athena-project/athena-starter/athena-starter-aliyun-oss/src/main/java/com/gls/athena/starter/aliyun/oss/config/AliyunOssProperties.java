package com.gls.athena.starter.aliyun.oss.config;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.gls.athena.common.core.constant.BaseProperties;
import com.gls.athena.common.core.constant.IConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.io.Serializable;

/**
 * 阿里云OSS服务配置属性类。
 * <p>
 * 用于配置阿里云OSS客户端所需的各项参数，包括认证模式、地域、访问密钥等。
 * 支持多种认证方式，详见 {@link AuthenticationMode}。
 * <ul>
 *   <li>AS_AK模式：使用AccessKeyId和AccessKeySecret进行认证</li>
 *   <li>STS模式：使用临时安全令牌进行认证</li>
 * </ul>
 * <p>
 * 典型配置示例：
 * <pre>
 * athena.aliyun.oss.auth-mode=AS_AK
 * athena.aliyun.oss.region-id=cn-hangzhou
 * athena.aliyun.oss.access-key-id=xxx
 * athena.aliyun.oss.access-key-secret=yyy
 * </pre>
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = IConstants.BASE_PROPERTIES_PREFIX + ".aliyun.oss")
public class AliyunOssProperties extends BaseProperties {
    /**
     * 认证模式，默认使用AS_AK模式
     *
     * @see AuthenticationMode
     */
    private AuthenticationMode authMode = AuthenticationMode.AS_AK;

    /**
     * 阿里云地域ID
     * 例如: cn-hangzhou, cn-beijing等
     */
    private String regionId;

    /**
     * 访问密钥ID (AccessKey ID)
     * 用于标识用户身份
     */
    private String accessKeyId;

    /**
     * 访问密钥密码 (AccessKey Secret)
     * 用于加密签名字符串和服务器端验证签名字符串
     */
    private String accessKeySecret;

    /**
     * STS临时安全令牌
     * 使用STS模式时的临时访问凭证
     */
    private String securityToken;

    /**
     * STS token有效期，单位为秒
     */
    private Long durationSeconds;

    /**
     * 访问策略
     * 定义访问权限的策略文档
     */
    private String policy;

    /**
     * 角色外部ID
     * 用于跨账号访问时的安全验证
     */
    private String externalId;

    /**
     * RAM角色ARN
     * 角色的全局资源描述符
     */
    private String roleArn;

    /**
     * 角色会话名称
     * 用于标识临时凭证的会话名称
     */
    private String roleSessionName;

    /**
     * OSS服务的地域节点地址。
     * <p>
     * 例如：oss-cn-hangzhou.aliyuncs.com
     */
    private String endpoint;

    /**
     * OSS存储空间（Bucket）名称。
     * <p>
     * 用于指定存储对象的容器。
     */
    private String bucketName;

    /**
     * 文件存储路径前缀。
     * <p>
     * 用于统一管理文件的存储目录结构。
     */
    private String pathPrefix;

    /**
     * 线程池执行器配置。
     * <p>
     * 用于配置异步任务执行所需的线程池参数。
     */
    private ExecutorConfig executor = new ExecutorConfig();

    /**
     * OSS客户端配置。
     * <p>
     * 包含连接超时、最大连接数等配置项。
     */
    @NestedConfigurationProperty
    private ClientBuilderConfiguration config = new ClientBuilderConfiguration();

    /**
     * 线程池执行器配置类。
     * <p>
     * 用于配置异步任务执行所需的线程池参数。
     */
    @Data
    public static class ExecutorConfig implements Serializable {

        /**
         * 核心线程数
         */
        private int corePoolSize = 10;

        /**
         * 最大线程数
         */
        private int maxPoolSize = 100;

        /**
         * 队列容量
         */
        private int queueCapacity = 100;

        /**
         * 是否在关闭时等待任务完成
         */
        private boolean waitForTasksToCompleteOnShutdown = true;

        /**
         * 等待终止的时间（秒）
         */
        private int awaitTerminationSeconds = 60;

    }
}
