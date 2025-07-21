package com.gls.athena.starter.jasper.config;

import com.gls.athena.common.core.constant.BaseProperties;
import com.gls.athena.common.core.constant.IConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author lizy19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = IConstants.BASE_PROPERTIES_PREFIX + ".jasper")
public class JasperProperties extends BaseProperties {
    /**
     * 模板路径
     */
    private String templatePath = "classpath:/templates/jasper";
}
