package com.gls.athena.starter.word;

import com.gls.athena.starter.word.config.WordProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Word自动配置类
 *
 * @author athena
 */
@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties(WordProperties.class)
public class WordAutoConfig {

}
