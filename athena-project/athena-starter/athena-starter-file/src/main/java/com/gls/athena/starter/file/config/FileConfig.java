package com.gls.athena.starter.file.config;

import com.gls.athena.starter.file.manager.DefaultFileManager;
import com.gls.athena.starter.file.manager.IFileManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 文件配置类
 * 启用文件属性配置，用于初始化和管理文件相关的配置属性
 *
 * @author lizy19
 */
@Configuration
@EnableConfigurationProperties(FileProperties.class)
public class FileConfig {

    /**
     * 创建文件服务接口实例
     *
     * @param fileProperties 文件属性配置对象
     * @return 文件服务接口实例
     */
    @Bean
    @ConditionalOnMissingBean(IFileManager.class)
    public IFileManager fileManager(FileProperties fileProperties) {
        return new DefaultFileManager(fileProperties);
    }

}
