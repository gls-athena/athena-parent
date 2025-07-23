package com.gls.athena.starter.word.config;

import com.gls.athena.common.core.constant.BaseProperties;
import com.gls.athena.common.core.constant.IConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Word配置属性
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = IConstants.BASE_PROPERTIES_PREFIX + ".word")
public class WordProperties extends BaseProperties {

    /**
     * 模板路径
     */
    private String templatePath = "classpath:/templates/word";
}
