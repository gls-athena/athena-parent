package com.gls.athena.starter.log;

import com.gls.athena.starter.log.config.LogProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 日志自动配置
 *
 * @author george
 */
@Configuration
@ComponentScan
@EnableAspectJAutoProxy
@EnableConfigurationProperties(LogProperties.class)
public class LogAutoConfig {
}
