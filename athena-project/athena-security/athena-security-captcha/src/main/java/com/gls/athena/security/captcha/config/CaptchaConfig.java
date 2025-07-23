package com.gls.athena.security.captcha.config;

import com.gls.athena.security.captcha.provider.impl.ImageCaptchaProvider;
import com.gls.athena.security.captcha.provider.impl.SmsCaptchaProvider;
import com.gls.athena.security.captcha.repository.CaptchaRepository;
import com.gls.athena.security.captcha.repository.RedisCaptchaRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CaptchaConfig {

    @Bean
    @ConditionalOnMissingBean(CaptchaRepository.class)
    public CaptchaRepository captchaRepository() {
        return new RedisCaptchaRepository();
    }

    @Bean
    @ConditionalOnMissingBean(name = "imageCaptchaProvider")
    public ImageCaptchaProvider imageCaptchaProvider(CaptchaProperties properties, CaptchaRepository captchaRepository) {
        return new ImageCaptchaProvider(properties, captchaRepository);
    }

    @Bean
    @ConditionalOnMissingBean(name = "smsCaptchaProvider")
    public SmsCaptchaProvider smsCaptchaProvider(CaptchaProperties properties, CaptchaRepository captchaRepository) {
        return new SmsCaptchaProvider(properties, captchaRepository);
    }
}
