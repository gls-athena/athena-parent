package com.gls.athena.starter.file.config;

import com.gls.athena.starter.aliyun.oss.manager.OssFileManager;
import com.gls.athena.starter.data.redis.support.RedisUtil;
import com.gls.athena.starter.file.manager.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 文件配置类
 * 启用文件属性配置，用于初始化和管理文件相关的配置属性
 *
 * @author george
 */
@Configuration
@EnableConfigurationProperties(FileProperties.class)
public class FileConfig {

    /**
     * 创建基于内存的文件信息管理器Bean
     * 当容器中不存在IFileInfoManager类型的Bean且文件信息存储类型配置为memory时（默认值），
     * 创建并注册InMemoryFileInfoManager实例
     *
     * @return IFileInfoManager 文件信息管理器实例
     */
    @Bean
    @ConditionalOnMissingBean(IFileInfoManager.class)
    @ConditionalOnProperty(prefix = "athena.file", name = "info", havingValue = "memory", matchIfMissing = true)
    public IFileInfoManager inMemoryFileInfoManager() {
        return new InMemoryFileInfoManager();
    }

    /**
     * 创建基于Redis的文件信息管理器Bean
     * 当容器中不存在IFileInfoManager类型的Bean、RedisUtil类存在于classpath中，
     * 且文件信息存储类型配置为redis时，创建并注册RedisFileInfoManager实例
     *
     * @return IFileInfoManager 文件信息管理器实例
     */
    @Bean
    @ConditionalOnMissingBean(IFileInfoManager.class)
    @ConditionalOnClass(RedisUtil.class)
    @ConditionalOnProperty(prefix = "athena.file", name = "info", havingValue = "redis")
    public IFileInfoManager redisFileInfoManager() {
        return new RedisFileInfoManager();
    }

    /**
     * 创建本地文件存储管理器Bean
     * 当容器中不存在IFileStorageManager类型的Bean且文件存储类型配置为local时，
     * 创建并注册默认的文件存储管理器
     *
     * @param fileProperties 文件配置属性
     * @return IFileStorageManager 文件存储管理器实例
     */
    @Bean
    @ConditionalOnMissingBean(IFileStorageManager.class)
    @ConditionalOnProperty(prefix = "athena.file", name = "storage", havingValue = "local", matchIfMissing = true)
    public IFileStorageManager localFileStorageManager(FileProperties fileProperties) {
        return new DefaultFileStorageManager(fileProperties);
    }

    /**
     * 创建OSS文件存储管理器Bean
     * 当容器中不存在IFileStorageManager类型的Bean且文件存储类型配置为oss，
     * 并且OssFileManager类存在于classpath中时，创建并注册OSS文件存储管理器
     *
     * @param ossFileManager OSS文件管理器
     * @return IFileStorageManager 文件存储管理器实例
     */
    @Bean
    @ConditionalOnMissingBean(IFileStorageManager.class)
    @ConditionalOnClass(OssFileManager.class)
    @ConditionalOnProperty(prefix = "athena.file", name = "storage", havingValue = "oss")
    public IFileStorageManager ossFileStorageManager(OssFileManager ossFileManager) {
        return new OssFileStorageManager(ossFileManager);
    }
}
