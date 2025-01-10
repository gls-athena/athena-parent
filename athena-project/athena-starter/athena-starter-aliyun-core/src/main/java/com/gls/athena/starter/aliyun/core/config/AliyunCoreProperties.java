package com.gls.athena.starter.aliyun.core.config;

import com.gls.athena.common.core.constant.BaseProperties;
import com.gls.athena.common.core.constant.IConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * 阿里云服务核心配置属性类
 * 用于配置阿里云服务的认证信息和客户端参数
 *
 * @author george
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = IConstants.BASE_PROPERTIES_PREFIX + ".aliyun")
public class AliyunCoreProperties extends BaseProperties {
    /**
     * 阿里云客户端配置映射
     * key: 客户端标识
     * value: 对应的客户端配置
     */
    @NestedConfigurationProperty
    private Map<String, Client> clients = new HashMap<>();

    /**
     * 阿里云认证模式枚举
     * 定义了支持的认证方式类型
     */
    public enum AuthMode {
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

    /**
     * 阿里云客户端配置类
     * 包含了访问阿里云服务所需的认证和配置信息
     *
     * @author george
     * @since 1.0.0
     */
    @Data
    public static class Client {
        /**
         * 认证模式，默认使用AS_AK模式
         *
         * @see AuthMode
         */
        private AliyunCoreProperties.AuthMode authMode = AliyunCoreProperties.AuthMode.AS_AK;

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
    }
}
