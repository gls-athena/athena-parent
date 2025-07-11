package com.gls.athena.starter.pdf.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * PDF配置属性（简化版）
 *
 * @author athena
 */
@Data
@ConfigurationProperties(prefix = "athena.pdf")
public class PdfProperties {

    /**
     * 默认模板路径
     */
    private String templatePath = "classpath:templates/pdf/";

    /**
     * 默认文件名前缀
     */
    private String filePrefix = "document";

    /**
     * 页面大小（A4, A3, LETTER等）
     */
    private String pageSize = "A4";

    /**
     * 页面方向（PORTRAIT, LANDSCAPE）
     */
    private String orientation = "PORTRAIT";
}
