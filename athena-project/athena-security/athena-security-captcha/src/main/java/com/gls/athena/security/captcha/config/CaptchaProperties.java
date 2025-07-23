package com.gls.athena.security.captcha.config;

import com.gls.athena.common.core.constant.BaseProperties;
import com.gls.athena.common.core.constant.IConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * 验证码配置
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = IConstants.BASE_PROPERTIES_PREFIX + ".security.captcha")
public class CaptchaProperties extends BaseProperties {
    /**
     * 验证码类型参数名
     */
    private String typeParam = "captchaType";
    /**
     * 短信验证码配置
     */
    @NestedConfigurationProperty
    private SmsCaptchaProperties sms = new SmsCaptchaProperties();
    /**
     * 图形验证码配置
     */
    @NestedConfigurationProperty
    private ImageCaptchaProperties image = new ImageCaptchaProperties();
}
