package com.gls.athena.starter.message;

import com.gls.athena.starter.message.config.MessageProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 消息自动配置
 *
 * @author george
 */
@Configuration
@ComponentScan
@EnableConfigurationProperties(MessageProperties.class)
public class MessageAutoConfig {
}