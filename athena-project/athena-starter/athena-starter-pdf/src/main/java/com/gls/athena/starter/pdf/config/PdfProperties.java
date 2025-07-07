package com.gls.athena.starter.pdf.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * PDF配置属性
 *
 * @author athena
 */
@Data
@ConfigurationProperties(prefix = "athena.pdf")
public class PdfProperties {

    /**
     * 默认模板路径
     */
    private String defaultTemplatePath = "classpath:templates/pdf/";

    /**
     * 临时文件路径
     */
    private String tempPath = System.getProperty("java.io.tmpdir");

    /**
     * 默认文件名前缀
     */
    private String defaultFilePrefix = "document";

    /**
     * 是否启用缓存
     */
    private boolean cacheEnabled = true;

    /**
     * 缓存大小
     */
    private int cacheSize = 100;

    /**
     * 默认字体路径
     */
    private String defaultFontPath = "classpath:fonts/";

    /**
     * 页面设置
     */
    private PageSettings pageSettings = new PageSettings();

    /**
     * 页面设置
     */
    @Data
    public static class PageSettings {
        /**
         * 页面大小（A4, A3, LETTER等）
         */
        private String pageSize = "A4";

        /**
         * 页面方向（PORTRAIT, LANDSCAPE）
         */
        private String orientation = "PORTRAIT";

        /**
         * 页边距（毫米）
         */
        private float marginTop = 20;
        private float marginBottom = 20;
        private float marginLeft = 20;
        private float marginRight = 20;
    }
}
