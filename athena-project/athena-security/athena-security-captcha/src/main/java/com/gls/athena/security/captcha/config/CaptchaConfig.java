package com.gls.athena.security.captcha.config;

import com.gls.athena.security.captcha.provider.impl.ImageCaptchaProvider;
import com.gls.athena.security.captcha.provider.impl.SmsCaptchaProvider;
import com.gls.athena.security.captcha.repository.CaptchaRepository;
import com.gls.athena.security.captcha.repository.RedisCaptchaRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 验证码配置类
 * 用于定义与验证码相关的Bean，包括验证码仓库和验证码提供者
 *
 * @author george
 */
@Configuration
public class CaptchaConfig {

    /**
     * 配置验证码仓库Bean
     * 如果容器中没有其他CaptchaRepository类型的Bean，则创建一个RedisCaptchaRepository实例
     *
     * @return 返回一个CaptchaRepository接口的实现对象
     */
    @Bean
    @ConditionalOnMissingBean(CaptchaRepository.class)
    public CaptchaRepository captchaRepository() {
        return new RedisCaptchaRepository();
    }

    /**
     * 配置图片验证码提供者Bean
     * 如果容器中没有名为"imageCaptchaProvider"的Bean，则创建一个ImageCaptchaProvider实例
     *
     * @param properties        验证码配置属性
     * @param captchaRepository 验证码仓库
     * @return 返回一个ImageCaptchaProvider实例
     */
    @Bean
    @ConditionalOnMissingBean(name = "imageCaptchaProvider")
    public ImageCaptchaProvider imageCaptchaProvider(CaptchaProperties properties, CaptchaRepository captchaRepository) {
        return new ImageCaptchaProvider(properties, captchaRepository);
    }

    /**
     * 配置短信验证码提供者Bean
     * 如果容器中没有名为"smsCaptchaProvider"的Bean，则创建一个SmsCaptchaProvider实例
     *
     * @param properties        验证码配置属性
     * @param captchaRepository 验证码仓库
     * @return 返回一个SmsCaptchaProvider实例
     */
    @Bean
    @ConditionalOnMissingBean(name = "smsCaptchaProvider")
    public SmsCaptchaProvider smsCaptchaProvider(CaptchaProperties properties, CaptchaRepository captchaRepository) {
        return new SmsCaptchaProvider(properties, captchaRepository);
    }
}
