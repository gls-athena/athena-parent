package com.gls.athena.starter.xxl.job;

import com.gls.athena.starter.xxl.job.config.XxlJobProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * xxl-job自动配置类
 *
 * @author george
 */
@Configuration
@ComponentScan
@EnableConfigurationProperties({XxlJobProperties.class})
public class XxlJobAutoConfig {
}