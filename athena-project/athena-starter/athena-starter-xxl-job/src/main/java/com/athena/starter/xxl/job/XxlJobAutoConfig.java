package com.athena.starter.xxl.job;

import com.athena.starter.xxl.job.config.XxlJobProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * xxl-job自动配置类
 */
@Configuration
@ComponentScan
@EnableConfigurationProperties({XxlJobProperties.class})
public class XxlJobAutoConfig {
}