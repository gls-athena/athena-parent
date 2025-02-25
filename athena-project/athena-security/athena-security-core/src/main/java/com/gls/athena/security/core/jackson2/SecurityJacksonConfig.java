package com.gls.athena.security.core.jackson2;

import com.gls.athena.starter.data.redis.support.RedisObjectMapperCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.jackson2.SecurityJackson2Modules;

/**
 * Spring Security Jackson序列化配置
 * <p>
 * 用于配置Spring Security相关对象的JSON序列化和反序列化
 *
 * @author george
 */
@Configuration
public class SecurityJacksonConfig {

    /**
     * 配置Redis的ObjectMapper，注册Security相关的序列化模块
     * <p>
     * 1. 注册标准的Spring Security序列化模块
     * 2. 注册自定义的核心安全模块
     *
     * @return Redis对象映射器的自定义配置
     */
    @Bean
    public RedisObjectMapperCustomizer securityRedisObjectMapperCustomizer() {
        return objectMapper -> {
            // 注册Security模块
            SecurityJackson2Modules.getModules(getClass().getClassLoader())
                    .forEach(objectMapper::registerModule);
            // 注册CoreSecurity模块
            objectMapper.registerModule(new CoreSecurityModule());
        };
    }
}
