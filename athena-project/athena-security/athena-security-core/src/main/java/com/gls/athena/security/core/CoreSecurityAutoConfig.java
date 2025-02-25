package com.gls.athena.security.core;

import com.gls.athena.security.core.properties.CoreSecurityProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 核心安全自动配置类
 * <p>
 * 提供系统核心安全配置,包括:
 * 1. 密码加密器配置
 * 2. 安全属性配置加载
 * 3. 组件自动扫描
 * </p>
 *
 * @author george
 * @since 1.0.0
 */
@Configuration
@ComponentScan
@EnableConfigurationProperties(CoreSecurityProperties.class)
public class CoreSecurityAutoConfig {

    /**
     * 配置默认密码编码器
     * <p>
     * 使用 Spring Security 5.0 后推荐的 DelegatingPasswordEncoder
     * 支持多种加密方式,并提供向后兼容性
     * </p>
     *
     * @return DelegatingPasswordEncoder 实例
     * @see PasswordEncoderFactories#createDelegatingPasswordEncoder()
     */
    @Bean
    @ConditionalOnMissingBean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
