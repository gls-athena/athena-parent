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
     * 默认文档标题
     */
    private String defaultTitle = "";

    /**
     * 默认模板路径
     */
    private String defaultTemplate = "";

    /**
     * 默认是否分页
     */
    private boolean pagination = true;

    /**
     * 模板基础路径
     */
    private String templateBasePath = "templates/word/";
}
