package com.gls.athena.starter.file.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
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

}
