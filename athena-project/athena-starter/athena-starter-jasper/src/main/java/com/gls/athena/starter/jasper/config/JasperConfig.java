package com.gls.athena.starter.jasper.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Jasper报告配置类 - 专门负责Bean的配置和注册
 *
 * @author george
 */
@AutoConfiguration
@EnableConfigurationProperties(JasperProperties.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class JasperConfig {

}
