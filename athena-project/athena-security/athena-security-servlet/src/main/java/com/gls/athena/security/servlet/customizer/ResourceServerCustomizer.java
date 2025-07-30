package com.gls.athena.security.servlet.customizer;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.stereotype.Component;

/**
 * 资源服务器自定义
 *
 * @author george
 */
@Component
public class ResourceServerCustomizer
        implements Customizer<OAuth2ResourceServerConfigurer<HttpSecurity>> {

    /**
     * 自定义
     *
     * @param configurer 配置器
     */
    @Override
    public void customize(OAuth2ResourceServerConfigurer<HttpSecurity> configurer) {
        // 默认配置
        configurer.opaqueToken(Customizer.withDefaults());
    }

}
