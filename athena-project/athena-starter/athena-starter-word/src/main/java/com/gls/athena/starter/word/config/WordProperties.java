package com.gls.athena.starter.word.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Word配置属性
 *
 * @author athena
 */
@Data
@ConfigurationProperties(prefix = "athena.word")
public class WordProperties {

    /**
     * 默认模板路径
     */
    private String defaultTemplatePath = "classpath:templates/word/";

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
}
