package com.gls.athena.starter.pdf.config;

import cn.hutool.extra.template.TemplateConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * PDF配置属性（简化版）
 *
 * @author athena
 */
@Data
@ConfigurationProperties(prefix = "athena.pdf")
public class PdfProperties {
    /**
     * 字体路径
     */
    private String fontPath = "fonts";
    /**
     * 字符集
     */
    private Charset charset = StandardCharsets.UTF_8;
    /**
     * 模板路径
     */
    private String templatePath = "templates/pdf";
    /**
     * 资源模式
     */
    private TemplateConfig.ResourceMode resourceMode = TemplateConfig.ResourceMode.CLASSPATH;

}
