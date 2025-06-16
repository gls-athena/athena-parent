package com.gls.athena.security.servlet.rest;

import jakarta.annotation.Resource;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

/**
 * 表单登录自定义器
 *
 * @author george
 */
@Component
public class RestCustomizer implements Customizer<RestConfigurer<HttpSecurity>> {

    /**
     * 核心安全属性配置
     */
    @Resource
    private RestProperties restProperties;

    /**
     * 自定义HTTP安全配置的登录页面和登录处理URL。
     * 该方法主要用于配置Spring Security的登录页面路径和登录处理URL路径，
     * 并将这些路径设置为对所有用户可访问（permitAll）。
     *
     * @param configurer RestConfigurer<HttpSecurity>实例，用于配置HTTP安全相关的设置
     */
    @Override
    public void customize(RestConfigurer<HttpSecurity> configurer) {
        // 配置登录页面路径、登录处理URL路径，并设置为对所有用户可访问
        configurer.loginPage(restProperties.getLoginPage())
                .loginProcessingUrl(restProperties.getLoginProcessingUrl())
                .permitAll();
    }

}
