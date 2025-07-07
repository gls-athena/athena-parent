package com.gls.athena.starter.pdf;

import com.gls.athena.starter.pdf.config.PdfConfig;
import com.gls.athena.starter.pdf.config.PdfProperties;
import com.gls.athena.starter.pdf.generator.DefaultPdfGenerator;
import com.gls.athena.starter.pdf.generator.HtmlPdfGenerator;
import com.gls.athena.starter.pdf.generator.PdfGeneratorManager;
import com.gls.athena.starter.pdf.handler.PdfResponseHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * PDF自动配置类
 *
 * @author athena
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(WebMvcConfigurer.class)
@EnableConfigurationProperties(PdfProperties.class)
@Import({PdfConfig.class})
public class PdfAutoConfig {

    @Bean
    @ConditionalOnMissingBean
    public DefaultPdfGenerator defaultPdfGenerator(PdfProperties pdfProperties) {
        return new DefaultPdfGenerator(pdfProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public HtmlPdfGenerator htmlPdfGenerator(PdfProperties pdfProperties) {
        return new HtmlPdfGenerator(pdfProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public PdfGeneratorManager pdfGeneratorManager(List<com.gls.athena.starter.pdf.generator.PdfGenerator> generators) {
        return new PdfGeneratorManager(generators);
    }

    @Bean
    @ConditionalOnMissingBean
    public PdfResponseHandler pdfResponseHandler(PdfGeneratorManager generatorManager,
                                                 PdfProperties pdfProperties,
                                                 ApplicationContext applicationContext) {
        return new PdfResponseHandler(generatorManager, pdfProperties, applicationContext);
    }
}
