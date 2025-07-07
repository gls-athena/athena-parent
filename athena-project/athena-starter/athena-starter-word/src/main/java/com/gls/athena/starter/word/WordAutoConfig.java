package com.gls.athena.starter.word;

import com.gls.athena.starter.word.config.WordConfig;
import com.gls.athena.starter.word.config.WordProperties;
import com.gls.athena.starter.word.generator.DefaultWordGenerator;
import com.gls.athena.starter.word.generator.TemplateWordGenerator;
import com.gls.athena.starter.word.generator.WordGeneratorManager;
import com.gls.athena.starter.word.handler.WordResponseHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
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

    @Bean
    @ConditionalOnMissingBean
    public DefaultWordGenerator defaultWordGenerator() {
        return new DefaultWordGenerator();
    }

    @Bean
    @ConditionalOnMissingBean
    public TemplateWordGenerator templateWordGenerator() {
        return new TemplateWordGenerator();
    }

    @Bean
    @ConditionalOnMissingBean
    public WordGeneratorManager wordGeneratorManager(DefaultWordGenerator defaultWordGenerator,
                                                     TemplateWordGenerator templateWordGenerator) {
        return new WordGeneratorManager(java.util.List.of(templateWordGenerator, defaultWordGenerator));
    }

    @Bean
    @ConditionalOnMissingBean
    public WordResponseHandler wordResponseHandler(WordGeneratorManager generatorManager,
                                                   WordProperties wordProperties,
                                                   ApplicationContext applicationContext) {
        return new WordResponseHandler(generatorManager, wordProperties, applicationContext);
    }
}
