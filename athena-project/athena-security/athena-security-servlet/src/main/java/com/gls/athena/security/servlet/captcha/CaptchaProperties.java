package com.gls.athena.security.servlet.captcha;

import com.gls.athena.common.core.constant.BaseProperties;
import com.gls.athena.common.core.constant.IConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 验证码属性配置
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = IConstants.BASE_PROPERTIES_PREFIX + "security.captcha")
public class CaptchaProperties extends BaseProperties {

    /**
     * 短信验证码配置
     */
    private Sms sms = new Sms();

    /**
     * 图形验证码配置
     */
    private Image image = new Image();

    /**
     * 短信验证码配置
     */
    @Data
    public static class Sms {
        /**
         * 验证码长度
         */
        private int length = 6;
        /**
         * 过期时间
         */
        private int expireIn = 600;
        /**
         * 验证码参数名
         */
        private String codeParameterName = "smsCaptcha";
        /**
         * 手机号参数名
         */
        private String targetParameterName = "mobile";
        /**
         * 获取短信验证码url
         */
        private String url = "/captcha/sms";
        /**
         * 需要校验验证码的url
         */
        private List<String> urls = new ArrayList<>();
        /**
         * 登录处理 URL
         */
        private String loginProcessingUrl = "/login";
        /**
         * oauth2 token url
         */
        private String oauth2TokenUrl = "/oauth2/token";
        /**
         * 短信模板
         */
        private String templateCode = "SMS_LOGIN_TEMPLATE";
    }

    /**
     * 图形验证码配置
     */
    @Data
    public static class Image {
        /**
         * 验证码长度
         */
        private int length = 4;
        /**
         * 过期时间
         */
        private int expireIn = 600;
        /**
         * 图形验证码宽度
         */
        private int width = 100;
        /**
         * 图形验证码高度
         */
        private int height = 30;
        /**
         * 图形验证码干扰线数量
         */
        private int lineCount = 150;
        /**
         * 图形验证码字体大小
         */
        private float fontSize = 0.75f;
        /**
         * 验证码参数名
         */
        private String codeParameterName = "imageCaptcha";
        /**
         * 验证码key参数名
         */
        private String targetParameterName = "uuid";
        /**
         * 获取图形验证码url
         */
        private String url = "/captcha/image";
        /**
         * 需要校验验证码的url
         */
        private List<String> urls = new ArrayList<>();
        /**
         * 登录处理 URL
         */
        private String loginProcessingUrl = "/login";
        /**
         * oauth2 token url
         */
        private String oauth2TokenUrl = "/oauth2/token";
    }
}
