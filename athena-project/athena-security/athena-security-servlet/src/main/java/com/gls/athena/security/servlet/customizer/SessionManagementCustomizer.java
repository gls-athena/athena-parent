package com.gls.athena.security.servlet.customizer;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.stereotype.Component;

/**
 * 会话管理自定义器
 *
 * @author george
 */
@Component
public class SessionManagementCustomizer
        implements Customizer<SessionManagementConfigurer<HttpSecurity>> {
    /**
     * 自定义
     *
     * @param configurer 配置器
     */
    @Override
    public void customize(SessionManagementConfigurer<HttpSecurity> configurer) {
        configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}
