package com.gls.athena.security.core.properties;

import com.gls.athena.common.core.constant.BaseProperties;
import com.gls.athena.common.core.constant.IConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 安全核心配置属性类
 * 用于配置系统安全相关的基础属性，如URL忽略列表等
 *
 * @author george
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = IConstants.BASE_PROPERTIES_PREFIX + ".security")
public class CoreSecurityProperties extends BaseProperties {
    /**
     * 安全框架忽略的URL列表
     * 这些URL将不会被安全框架拦截，可直接访问
     * 默认包含静态资源路径、API文档路径等
     */
    private String[] ignoreUrls = new String[]{"/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico", "/actuator/**", "/error", "/v3/api-docs"};

}
