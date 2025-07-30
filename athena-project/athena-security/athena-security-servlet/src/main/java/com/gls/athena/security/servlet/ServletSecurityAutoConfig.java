package com.gls.athena.security.servlet;

import com.gls.athena.security.servlet.captcha.CaptchaProperties;
import com.gls.athena.security.servlet.rest.RestProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * Servlet安全配置
 *
 * @author george
 */
@Configuration
@ComponentScan
@EnableWebSecurity
@EnableConfigurationProperties({CaptchaProperties.class, RestProperties.class})
public class ServletSecurityAutoConfig {
}
