package com.gls.athena.starter.file.core.config;

import com.gls.athena.starter.aliyun.oss.manager.OssFileManager;
import com.gls.athena.starter.data.redis.support.RedisUtil;
import com.gls.athena.starter.file.core.manager.*;
import com.gls.athena.starter.file.core.support.FileResponseHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件配置类
 * 启用文件属性配置，用于初始化和管理文件相关的配置属性
 *
 * @author george
 */
@Configuration
@EnableConfigurationProperties(FileProperties.class)
public class FileConfig {

    @Resource
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;
    @Resource
    private FileResponseHandler fileResponseHandler;

    @PostConstruct
    public void init() {
        initReturnValueHandlers();
    }

    private void initReturnValueHandlers() {
        List<HandlerMethodReturnValueHandler> returnValueHandlers = requestMappingHandlerAdapter.getReturnValueHandlers();
        List<HandlerMethodReturnValueHandler> newHandlers = new ArrayList<>();
        newHandlers.add(fileResponseHandler);
        // 如果存在原有的处理器列表，将其全部添加到新的处理器列表中
        if (returnValueHandlers != null) {
            newHandlers.addAll(returnValueHandlers);
        }
        // 将新的处理器列表设置回请求映射处理器适配器
        requestMappingHandlerAdapter.setReturnValueHandlers(newHandlers);
    }

    /**
     * 本地文件存储配置类
     * 当配置项 athena.file.storage = local 或未配置时生效
     * 提供基于本地存储的文件存储管理器实现
     *
     * @author george
     */
    @Configuration
    @ConditionalOnProperty(prefix = "athena.file", name = "storage", havingValue = "local", matchIfMissing = true)
    public static class DefaultFileStorageConfig {

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

    /**
     * 内存文件信息配置类
     * 当配置项 athena.file.info = memory 或未配置时生效
     * 提供基于内存的文件信息管理器实现
     *
     * @author george
     */
    @Configuration
    @ConditionalOnProperty(prefix = "athena.file", name = "info", havingValue = "memory", matchIfMissing = true)
    public static class InMemoryFileInfoConfig {

        /**
         * 创建内存文件信息管理器 Bean
         * 当容器中不存在 IFileInfoManager 类型的 Bean 时创建
         *
         * @return 内存文件信息管理器实例
         */
        @Bean
        @ConditionalOnMissingBean(IFileInfoManager.class)
        public IFileInfoManager inMemoryFileInfoManager() {
            return new InMemoryFileInfoManager();
        }
    }

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
    public static class OssFileStorageConfig {

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

    /**
     * Redis 文件信息配置类
     * 当 RedisUtil 类存在且配置项 athena.file.info = redis 时生效
     * 提供基于 Redis 的文件信息管理器实现
     *
     * @author george
     */
    @Configuration
    @ConditionalOnClass(RedisUtil.class)
    @ConditionalOnProperty(prefix = "athena.file", name = "info", havingValue = "redis")
    public static class RedisFileInfoConfig {

        /**
         * 创建 Redis 文件信息管理器 Bean
         * 当容器中不存在 IFileInfoManager 类型的 Bean 时创建
         *
         * @return Redis 文件信息管理器实例
         */
        @Bean
        @ConditionalOnMissingBean(IFileInfoManager.class)
        public IFileInfoManager redisFileInfoManager() {
            return new RedisFileInfoManager();
        }
    }
}
