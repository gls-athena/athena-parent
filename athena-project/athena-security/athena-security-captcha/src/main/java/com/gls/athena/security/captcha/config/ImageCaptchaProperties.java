package com.gls.athena.security.captcha.config;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 图形验证码配置
 *
 * @author george
 */
@Data
public class ImageCaptchaProperties implements Serializable {
    /**
     * 验证码参数名称
     */
    private String captchaParam = "imageCaptcha";
    /**
     * 验证码过期时间
     */
    private int captchaExpire = 60000;
    /**
     * 验证码发送间隔
     */
    private long captchaInterval = 60000;
    /**
     * 验证码图片宽度
     */
    private int width = 120;
    /**
     * 验证码图片高度
     */
    private int height = 40;
    /**
     * 验证码字符个数
     */
    private int codeCount = 4;
    /**
     * 验证码干扰线数
     */
    private int lineCount = 20;
    /**
     * 字体大小
     */
    private float size = 1.2f;
    /**
     * uuid参数名称
     */
    private String uuidParam = "uuid";
    /**
     * 获取图片验证码的url
     */
    private String captchaUrl = "/captcha/image";
    /**
     * 图片验证码校验URL
     */
    private List<String> captchaCheckUrls = new ArrayList<>();

}
