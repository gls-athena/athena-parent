package com.gls.athena.starter.core;

import com.gls.athena.common.bean.security.IUserHelper;
import com.gls.athena.starter.core.async.ThreadPoolTaskProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 核心自动配置类
 *
 * @author george
 */
@Configuration
@ComponentScan
@EnableConfigurationProperties({ThreadPoolTaskProperties.class})
public class CoreAutoConfig {

    /**
     * 用户助手
     *
     * @return IUserHelper 用户助手
     */
    @Bean
    @ConditionalOnMissingBean
    public IUserHelper userHelper() {
        return IUserHelper.withDefaults();
    }
}