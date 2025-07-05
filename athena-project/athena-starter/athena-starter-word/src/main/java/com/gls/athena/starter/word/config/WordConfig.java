package com.gls.athena.starter.word.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Word配置类
 *
 * @author athena
 */
@Configuration
@EnableConfigurationProperties(WordProperties.class)
public class WordConfig {
}
