package com.gls.athena.starter.word.config;

import com.gls.athena.starter.word.generator.WordDocumentGenerator;
import com.gls.athena.starter.word.generator.WordDocumentGeneratorFactory;
import com.gls.athena.starter.word.generator.impl.DefaultWordDocumentGenerator;
import com.gls.athena.starter.word.generator.impl.PoiTlTemplateWordDocumentGenerator;
import com.gls.athena.starter.word.generator.impl.TemplateWordDocumentGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

/**
 * Word配置类
 *
 * @author athena
 */
@Configuration
@EnableConfigurationProperties(WordProperties.class)
public class WordConfig {

    @Bean
    @ConditionalOnMissingBean
    public DefaultWordDocumentGenerator defaultWordDocumentGenerator() {
        return new DefaultWordDocumentGenerator();
    }

    @Bean
    @ConditionalOnMissingBean
    public TemplateWordDocumentGenerator templateWordDocumentGenerator() {
        return new TemplateWordDocumentGenerator();
    }

    @Bean
    @ConditionalOnMissingBean
    @Primary
    public PoiTlTemplateWordDocumentGenerator poiTlTemplateWordDocumentGenerator() {
        return new PoiTlTemplateWordDocumentGenerator();
    }

    @Bean
    @ConditionalOnMissingBean
    public WordDocumentGeneratorFactory wordDocumentGeneratorFactory(
            List<WordDocumentGenerator> generators, WordProperties properties) {
        return new WordDocumentGeneratorFactory(generators, properties);
    }
}
