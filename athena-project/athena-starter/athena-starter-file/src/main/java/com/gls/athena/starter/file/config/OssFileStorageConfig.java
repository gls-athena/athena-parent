package com.gls.athena.starter.file.config;

import com.gls.athena.starter.aliyun.oss.manager.OssFileManager;
import com.gls.athena.starter.file.manager.IFileStorageManager;
import com.gls.athena.starter.file.manager.OssFileStorageManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云 OSS 文件存储配置类
 * 当 OssFileManager 类存在且配置项 athena.file.storage = oss 时生效
 * 提供基于阿里云 OSS 的文件存储管理器实现
 *
 * @author george
 */
@Configuration
@ConditionalOnClass(OssFileManager.class)
@ConditionalOnProperty(prefix = "athena.file", name = "storage", havingValue = "oss")
public class OssFileStorageConfig {

    /**
     * 创建 OSS 文件存储管理器 Bean
     * 当容器中不存在 IFileStorageManager 类型的 Bean 时创建
     *
     * @param ossFileManager OSS 文件管理器对象
     * @return OSS 文件存储管理器实例
     */
    @Bean
    @ConditionalOnMissingBean(IFileStorageManager.class)
    public IFileStorageManager ossFileStorageManager(OssFileManager ossFileManager) {
        return new OssFileStorageManager(ossFileManager);
    }
}
