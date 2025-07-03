package com.gls.athena.starter.word.config;

import cn.hutool.extra.template.TemplateConfig;
import com.gls.athena.common.core.constant.BaseProperties;
import com.gls.athena.common.core.constant.IConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = IConstants.BASE_PROPERTIES_PREFIX + ".word")
public class WordProperties extends BaseProperties {
    /**
     * Word模板配置
     */
    @NestedConfigurationProperty
    private TemplateConfig templateConfig = new TemplateConfig("template", TemplateConfig.ResourceMode.CLASSPATH);
}
