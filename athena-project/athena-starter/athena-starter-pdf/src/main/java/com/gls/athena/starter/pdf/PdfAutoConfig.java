package com.gls.athena.starter.pdf;

import com.gls.athena.starter.pdf.config.PdfConfig;
import com.gls.athena.starter.pdf.config.PdfProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * PDF自动配置类（简化版）
 *
 * @author athena
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(WebMvcConfigurer.class)
@EnableConfigurationProperties(PdfProperties.class)
@Import({PdfConfig.class})
public class PdfAutoConfig {
}
