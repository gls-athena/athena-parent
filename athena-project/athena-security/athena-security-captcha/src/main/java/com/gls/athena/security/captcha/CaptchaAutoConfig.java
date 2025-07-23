package com.gls.athena.security.captcha;

import com.gls.athena.security.captcha.config.CaptchaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@EnableConfigurationProperties(CaptchaProperties.class)
public class CaptchaAutoConfig {
}
