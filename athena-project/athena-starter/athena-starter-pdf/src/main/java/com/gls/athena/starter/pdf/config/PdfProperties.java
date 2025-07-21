package com.gls.athena.starter.pdf.config;

import cn.hutool.extra.template.TemplateConfig;
import com.gls.athena.common.core.constant.BaseProperties;
import com.gls.athena.common.core.constant.IConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * PDF配置属性（简化版）
 *
 * @author athena
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = IConstants.BASE_PROPERTIES_PREFIX + ".pdf")
public class PdfProperties extends BaseProperties {
    /**
     * 字体路径
     */
    private String fontPath = "classpath:/fonts";
    /**
     * 字符集
     */
    private Charset charset = StandardCharsets.UTF_8;
    /**
     * 模板路径
     */
    private String templatePath = "classpath:/templates/pdf";
    /**
     * 资源模式
     */
    private TemplateConfig.ResourceMode resourceMode = TemplateConfig.ResourceMode.CLASSPATH;

}
