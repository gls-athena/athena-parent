package com.gls.athena.starter.word;

import com.gls.athena.starter.word.config.WordConfig;
import com.gls.athena.starter.word.config.WordProperties;
import com.gls.athena.starter.word.processor.WordResponseConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Word自动配置类
 *
 * @author athena
 */
@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties(WordProperties.class)
@Import({WordConfig.class, WordResponseConfiguration.class})
public class WordAutoConfig {

}
