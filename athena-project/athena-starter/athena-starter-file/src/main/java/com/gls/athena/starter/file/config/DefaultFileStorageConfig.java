package com.gls.athena.starter.file.config;

import com.gls.athena.starter.file.manager.DefaultFileStorageManager;
import com.gls.athena.starter.file.manager.IFileStorageManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 本地文件存储配置类
 * 当配置项 athena.file.storage = local 或未配置时生效
 * 提供基于本地存储的文件存储管理器实现
 *
 * @author george
 */
@Configuration
@ConditionalOnProperty(prefix = "athena.file", name = "storage", havingValue = "local", matchIfMissing = true)
public class DefaultFileStorageConfig {

    /**
     * 创建本地文件存储管理器 Bean
     * 当容器中不存在 IFileStorageManager 类型的 Bean 时创建
     *
     * @param fileProperties 文件配置属性对象
     * @return 本地文件存储管理器实例
     */
    @Bean
    @ConditionalOnMissingBean(IFileStorageManager.class)
    public IFileStorageManager localFileStorageManager(FileProperties fileProperties) {
        return new DefaultFileStorageManager(fileProperties);
    }
}
