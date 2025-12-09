package com.gls.athena.starter.file.core;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 文件自动配置类
 * <p>
 * 该类用于启用文件相关的自动配置功能，通过Spring的组件扫描机制
 * 自动注册文件处理相关的Bean组件。
 *
 * @author george
 * @since 1.0.0
 */
@Configuration
@ComponentScan
@EnableScheduling
public class FileAutoConfig {
}
