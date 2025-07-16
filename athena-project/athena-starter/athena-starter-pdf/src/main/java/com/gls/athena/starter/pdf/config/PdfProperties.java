package com.gls.athena.starter.pdf.config;

import cn.hutool.extra.template.TemplateConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * PDF配置属性（简化版）
 *
 * @author athena
 */
@Data
@ConfigurationProperties(prefix = "athena.pdf")
public class PdfProperties {
    /**
     * 模板配置
     */
    @NestedConfigurationProperty
    private TemplateConfig templateConfig = new TemplateConfig("", TemplateConfig.ResourceMode.CLASSPATH);
}
