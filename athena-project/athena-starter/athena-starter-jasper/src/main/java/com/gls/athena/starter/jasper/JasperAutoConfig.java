package com.gls.athena.starter.jasper;

import com.gls.athena.starter.jasper.config.JasperProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * PDF自动配置类
 *
 * @author george
 */
@Configuration
@ComponentScan
@EnableConfigurationProperties(JasperProperties.class)
public class JasperAutoConfig {
}