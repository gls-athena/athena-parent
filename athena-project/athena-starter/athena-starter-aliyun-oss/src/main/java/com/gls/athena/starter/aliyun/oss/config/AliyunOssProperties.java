package com.gls.athena.starter.aliyun.oss.config;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.gls.athena.common.core.constant.IConstants;
import com.gls.athena.starter.aliyun.core.config.AliyunCoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * 阿里云OSS服务配置属性类
 * 用于配置阿里云OSS客户端所需的各项参数
 *
 * @author george
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = IConstants.BASE_PROPERTIES_PREFIX + ".aliyun.oss")
public class AliyunOssProperties extends AliyunCoreProperties.Client {
    /**
     * OSS服务的地域节点地址
     * 例如：oss-cn-hangzhou.aliyuncs.com
     */
    private String endpoint;

    /**
     * OSS存储空间（Bucket）名称
     * 用于指定存储对象的容器
     */
    private String bucketName;

    /**
     * 文件存储路径前缀
     * 用于统一管理文件的存储目录结构
     */
    private String pathPrefix;

    /**
     * OSS客户端配置
     * 包含连接超时、最大连接数等配置项
     */
    @NestedConfigurationProperty
    private ClientBuilderConfiguration config = new ClientBuilderConfiguration();
}
