package com.gls.athena.sdk.xxl.job.config;

import cn.hutool.extra.spring.SpringUtil;
import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 定时任务配置类，用于配置和初始化XXL-JOB执行器
 *
 * @author george
 */
@AutoConfiguration
public class XxlJobConfig {
    /**
     * xxl-job-admin服务ID
     */
    private static final String XXL_JOB_ADMIN_SERVICE_ID = "xxl-job-admin";
    /**
     * xxl-job配置
     */
    @Resource
    private XxlJobProperties xxlJobProperties;
    /**
     * 服务发现客户端
     */
    @Resource
    private Optional<DiscoveryClient> discoveryClient;

    /**
     * xxl-job执行器
     *
     * @return xxl-job执行器
     */
    @Bean
    @ConditionalOnMissingBean
    public XxlJobExecutor xxlJobExecutor() {
        // 创建执行器
        XxlJobExecutor executor = new XxlJobSpringExecutor();
        // 定时任务中心地址
        executor.setAdminAddresses(getAdminAddresses());
        // 执行器AppName
        executor.setAppname(Optional.ofNullable(xxlJobProperties.getAppName()).orElse(SpringUtil.getApplicationName()));
        // 执行器注册地址
        Optional.ofNullable(xxlJobProperties.getAddress()).ifPresent(executor::setAddress);
        // 执行器IP
        Optional.ofNullable(xxlJobProperties.getIp()).ifPresent(executor::setIp);
        // 执行器端口号
        Optional.ofNullable(xxlJobProperties.getPort()).ifPresent(executor::setPort);
        // 执行器token
        Optional.ofNullable(xxlJobProperties.getAccessToken()).ifPresent(executor::setAccessToken);
        // 执行器运行日志文件存储磁盘路径
        Optional.ofNullable(xxlJobProperties.getLogPath()).ifPresent(executor::setLogPath);
        // 执行器日志保存天数
        Optional.ofNullable(xxlJobProperties.getLogRetentionDays()).ifPresent(executor::setLogRetentionDays);
        // 启动执行器
        return executor;
    }

    /**
     * 获取调度中心地址的方法，优先从服务发现客户端获取，否则从配置文件中获取
     *
     * @return 调度中心地址
     */
    private String getAdminAddresses() {
        return discoveryClient.map(client -> client.getServices().stream()
                        .filter(serviceId -> serviceId.contains(XXL_JOB_ADMIN_SERVICE_ID))
                        .flatMap(serviceId -> client.getInstances(serviceId).stream())
                        .map(serviceInstance -> serviceInstance.getUri().getAuthority() + "/" + XXL_JOB_ADMIN_SERVICE_ID)
                        .collect(Collectors.joining(",")))
                .orElse(xxlJobProperties.getAdminAddresses());
    }
}
