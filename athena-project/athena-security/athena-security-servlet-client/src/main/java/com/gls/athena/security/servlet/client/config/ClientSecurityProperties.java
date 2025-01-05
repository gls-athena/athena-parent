package com.gls.athena.security.servlet.client.config;

import com.gls.athena.common.core.constant.BaseProperties;
import com.gls.athena.common.core.constant.IConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * 客户端安全属性配置
 * Client security properties configuration
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = IConstants.BASE_PROPERTIES_PREFIX + ".security.client")
public class ClientSecurityProperties extends BaseProperties {
    /**
     * 客户端注册信息库类型
     */
    private ClientRegistrationRepositoryType type = ClientRegistrationRepositoryType.IN_MEMORY;
    /**
     * 微信配置
     */
    private Map<String, WechatMp> wechatMp = new HashMap<>();
    /**
     * 微信开放平台
     */
    private Map<String, WechatOpen> wechatOpen = new HashMap<>();
    /**
     * 企业微信
     */
    private Map<String, WechatWork> wechatWork = new HashMap<>();

    /**
     * 客户端注册信息库
     */
    public enum ClientRegistrationRepositoryType {
        /**
         * 内存
         */
        IN_MEMORY,
        /**
         * JDBC
         */
        JDBC
    }

    /**
     * 登录类型
     */
    @Getter
    public enum LoginType {
        /**
         * 服务商登录
         */
        SERVICE_APP("ServiceApp"),
        /**
         * 企业自建/代开发应用登录
         */
        CORP_APP("CorpApp"),
        ;
        /**
         * 值
         */
        private final String value;

        LoginType(String value) {
            this.value = value;
        }

    }

    /**
     * 微信公众平台
     */
    @Data
    public static class WechatMp {
        /**
         * 语言
         */
        private String lang = "zh_CN";
    }

    /**
     * 微信开放平台
     */
    @Data
    public static class WechatOpen {
        /**
         * 语言
         */
        private String lang = "zh_CN";
    }

    /**
     * 企业微信
     */
    @Data
    public static class WechatWork {
        /**
         * 语言
         */
        private String lang = "zh";
        /**
         * 企业自建应用/服务商代开发应用 AgentID，当login_type=CorpApp时填写
         */
        private String agentId = "1000002";
        /**
         * 登录类型
         */
        private LoginType loginType = LoginType.CORP_APP;
    }
}
