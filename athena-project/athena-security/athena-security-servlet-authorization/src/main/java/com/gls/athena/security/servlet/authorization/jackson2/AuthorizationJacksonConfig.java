package com.gls.athena.security.servlet.authorization.jackson2;

import com.gls.athena.starter.data.redis.support.RedisObjectMapperCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;

/**
 * 授权Jackson配置
 *
 * @author george
 */
@Configuration
public class AuthorizationJacksonConfig {

    /**
     * 授权Jackson配置
     *
     * @return RedisObjectMapperCustomizer
     */
    @Bean
    public RedisObjectMapperCustomizer authorizationRedisObjectMapperCustomizer() {
        return objectMapper -> {
            // 注册Security模块
            objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
            // 注册ServletSecurity模块
            objectMapper.registerModule(new AuthorizationModule());
        };
    }
}
