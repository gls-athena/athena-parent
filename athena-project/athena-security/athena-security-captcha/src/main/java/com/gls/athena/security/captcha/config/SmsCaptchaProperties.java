package com.gls.athena.security.captcha.config;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 短信验证码上下文
 *
 * @author george
 */
@Data
public class SmsCaptchaProperties implements Serializable {
    /**
     * 验证码参数名称
     */
    private String captchaParam = "smsCaptcha";
    /**
     * 验证码长度
     */
    private int captchaLength = 4;
    /**
     * 验证码过期时间
     */
    private int captchaExpire = 60000;
    /**
     * 验证码发送间隔
     */
    private long captchaInterval = 60000;
    /**
     * 手机号参数名称
     */
    private String mobileParam = "mobile";
    /**
     * 验证码发送模板ID
     */
    private String captchaTemplateId = "SMS_12345678";
    /**
     * 验证码发送URL
     */
    private String captchaSendUrl = "/captcha/sms";
    /**
     * 验证码校验URL
     */
    private List<String> captchaCheckUrls = new ArrayList<>();

}
