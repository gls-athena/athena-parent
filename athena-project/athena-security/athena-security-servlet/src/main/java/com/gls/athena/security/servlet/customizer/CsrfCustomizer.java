package com.gls.athena.security.servlet.customizer;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.stereotype.Component;

/**
 * CSRF自定义器
 *
 * @author george
 */
@Component
public class CsrfCustomizer
        implements Customizer<CsrfConfigurer<HttpSecurity>> {
    /**
     * 自定义
     *
     * @param configurer 配置器
     */
    @Override
    public void customize(CsrfConfigurer<HttpSecurity> configurer) {
        configurer.disable();
    }
}
