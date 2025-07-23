package com.gls.athena.security.captcha;

import com.gls.athena.security.captcha.config.CaptchaProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 验证码自动配置
 *
 * @author george
 */
@Configuration
@ComponentScan
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(CaptchaProperties.class)
public class CaptchaAutoConfig {
}
