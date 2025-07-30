package com.gls.athena.security.servlet.client;

import com.gls.athena.security.servlet.client.config.ClientSecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 客户端安全自动配置
 *
 * @author george
 */
@Configuration
@ComponentScan
@EnableConfigurationProperties(ClientSecurityProperties.class)
public class ClientSecurityAutoConfig {
}