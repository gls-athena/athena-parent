package com.gls.athena.starter.word;

import com.gls.athena.starter.word.config.WordConfig;
import com.gls.athena.starter.word.config.WordProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Word自动配置类
 *
 * @author athena
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(WebMvcConfigurer.class)
@EnableConfigurationProperties(WordProperties.class)
@Import({WordConfig.class})
public class WordAutoConfig {

}
