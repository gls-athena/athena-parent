package com.gls.athena.starter.pdf;

import com.gls.athena.starter.pdf.config.PdfProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * PDF自动配置类
 *
 * @author george
 */
@Configuration
@ComponentScan
@EnableConfigurationProperties(PdfProperties.class)
public class PdfAutoConfig {
}