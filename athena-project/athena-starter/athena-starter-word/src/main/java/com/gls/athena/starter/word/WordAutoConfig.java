package com.gls.athena.starter.word;

import com.gls.athena.starter.word.config.WordProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author george
 */
@Configuration
@ComponentScan
@EnableConfigurationProperties(WordProperties.class)
public class WordAutoConfig {
}
