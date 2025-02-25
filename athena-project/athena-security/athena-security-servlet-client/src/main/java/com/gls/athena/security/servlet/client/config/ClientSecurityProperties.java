package com.gls.athena.security.servlet.client.config;

import com.gls.athena.common.core.constant.BaseProperties;
import com.gls.athena.common.core.constant.IConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * 客户端安全配置属性类
 * Client security configuration properties class
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = IConstants.BASE_PROPERTIES_PREFIX + ".security.client")
public class ClientSecurityProperties extends BaseProperties {
    /**
     * 客户端注册信息存储类型
     * Client registration repository type
     */
    private ClientRegistrationRepositoryType type = ClientRegistrationRepositoryType.IN_MEMORY;

    /**
     * 微信公众平台配置映射
     * WeChat MP configuration mapping
     */
    private Map<String, WechatMp> wechatMp = new HashMap<>();

    /**
     * 微信开放平台配置映射
     * WeChat Open Platform configuration mapping
     */
    private Map<String, WechatOpen> wechatOpen = new HashMap<>();

    /**
     * 企业微信配置映射
     * WeChat Work configuration mapping
     */
    private Map<String, WechatWork> wechatWork = new HashMap<>();

    /**
     * 客户端注册信息存储类型枚举
     * Client registration repository type enumeration
     */
    public enum ClientRegistrationRepositoryType {
        /**
         * 内存存储方式
         * In-memory storage
         */
        IN_MEMORY,

        /**
         * JDBC存储方式
         * JDBC storage
         */
        JDBC
    }

    /**
     * 微信登录类型枚举
     * WeChat login type enumeration
     */
    @Getter
    @RequiredArgsConstructor
    public enum LoginType {
        /**
         * 服务商登录模式
         * Service provider login mode
         */
        SERVICE_APP("ServiceApp"),

        /**
         * 企业自建应用/代开发应用登录模式
         * Corporate self-built/agency development application login mode
         */
        CORP_APP("CorpApp");

        /**
         * 登录类型值
         * Login type value
         */
        private final String value;

    }

    /**
     * 微信公众平台配置类
     * WeChat MP configuration class
     */
    @Data
    public static class WechatMp {
        /**
         * 语言设置，默认简体中文
         * Language setting, default is Simplified Chinese
         */
        private String lang = "zh_CN";
    }

    /**
     * 微信开放平台配置类
     * WeChat Open Platform configuration class
     */
    @Data
    public static class WechatOpen {
        /**
         * 语言设置，默认简体中文
         * Language setting, default is Simplified Chinese
         */
        private String lang = "zh_CN";
    }

    /**
     * 企业微信配置类
     * WeChat Work configuration class
     */
    @Data
    public static class WechatWork {
        /**
         * 语言设置，默认简体中文
         * Language setting, default is Simplified Chinese
         */
        private String lang = "zh";

        /**
         * 企业应用ID，仅在loginType=CorpApp时需要
         * Corporate application ID, required only when loginType=CorpApp
         */
        private String agentId = "1000002";

        /**
         * 登录类型，默认为企业自建应用登录
         * Login type, default is corporate self-built application login
         */
        private LoginType loginType = LoginType.CORP_APP;
    }
}
