package com.gls.athena.security.servlet.rest;

import com.gls.athena.common.core.constant.BaseProperties;
import com.gls.athena.common.core.constant.IConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * REST 安全属性配置
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = IConstants.BASE_PROPERTIES_PREFIX + ".security.rest")
public class RestProperties extends BaseProperties {

    /**
     * 登录页面
     */
    private String loginPage = "/login.html";
    /**
     * 登录处理 URL
     */
    private String loginProcessingUrl = "/rest/login";
    /**
     * 用户名参数名
     */
    private String usernameParameter = "username";
    /**
     * 密码参数名
     */
    private String passwordParameter = "password";
    /**
     * 手机号参数名
     */
    private String mobileParameter = "mobile";
}
