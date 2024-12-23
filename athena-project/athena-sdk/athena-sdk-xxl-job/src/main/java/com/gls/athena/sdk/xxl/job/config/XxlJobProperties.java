package com.gls.athena.sdk.xxl.job.config;

import com.gls.athena.common.core.constant.BaseProperties;
import com.gls.athena.common.core.constant.IConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * xxl-job配置类
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = IConstants.BASE_PROPERTIES_PREFIX + ".xxl-job")
public class XxlJobProperties extends BaseProperties {

    /**
     * 调度中心地址 (选填)：如调度中心集群部署存在多个地址，用逗号分隔。
     */
    private String adminAddresses = "http://xxl-job-admin:8080/xxl-job-admin";
    /**
     * 执行器Token (选填)：非空时启用；
     */
    private String accessToken = "default_token";
    /**
     * 执行器AppName (选填)：执行器心跳注册分组依据；存在调度中心集群部署时，执行器AppName 要保持唯一；
     */
    private String appName;
    /**
     * 执行器注册地址 (选填)：默认为空，非空时优先使用；
     */
    private String address;
    /**
     * 执行器IP (选填)：默认为空，为空时自动获取；
     */
    private String ip;
    /**
     * 执行器端口号 (选填)：小于等于1024的端口号需root权限，为空时自动获取；
     */
    private Integer port;
    /**
     * 执行器运行日志文件存储磁盘路径 (选填)：需要对该路径拥有读写权限；为空则使用默认路径；
     */
    private String logPath = "logs/app-logs/xxl-job/job-handler";
    /**
     * 执行器日志保存天数 (选填)：值大于3时生效，启用执行器Log文件定期清理功能，否则不生效；
     */
    private Integer logRetentionDays = 30;

}
