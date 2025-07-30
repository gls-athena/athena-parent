package com.gls.athena.security.servlet.customizer;

import com.gls.athena.security.core.properties.CoreSecurityProperties;
import com.gls.athena.security.servlet.rest.RestProperties;
import jakarta.annotation.Resource;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

/**
 * 授权请求自定义器
 *
 * @author george
 */
@Component
public class AuthorizeHttpRequestsCustomizer
        implements Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> {

    /**
     * 核心安全属性配置
     */
    @Resource
    private CoreSecurityProperties coreSecurityProperties;
    /**
     * rest 安全属性配置
     */
    @Resource
    private RestProperties restProperties;

    /**
     * 自定义
     *
     * @param registry 注册器
     */
    @Override
    public void customize(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {

        registry
                // 静态资源和登录页面不需要认证
                .requestMatchers(coreSecurityProperties.getIgnoreUrls()).permitAll()
                // 登录页面和登录请求不需要认证
                .requestMatchers(restProperties.getLoginPage()).permitAll()
                // 所有请求都需要认证
                .anyRequest().authenticated();
    }
}
