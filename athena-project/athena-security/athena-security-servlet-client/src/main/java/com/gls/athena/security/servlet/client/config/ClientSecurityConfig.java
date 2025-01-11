package com.gls.athena.security.servlet.client.config;

import com.gls.athena.security.servlet.client.support.DefaultOAuth2ClientPropertiesMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;

/**
 * OAuth2客户端安全配置类
 * <p>
 * 用于配置OAuth2客户端的相关安全设置，包括：
 * - 客户端注册信息管理
 * - 客户端认证配置
 *
 * @author george
 * @since 1.0.0
 */
@Configuration
public class ClientSecurityConfig {

    /**
     * 创建客户端注册信息存储库
     * <p>
     * 当配置属性 athena.security.client.type 为 IN_MEMORY 或未配置时，
     * 使用内存方式存储客户端注册信息。
     *
     * @param properties OAuth2客户端配置属性，包含客户端ID、密钥等信息
     * @return {@link ClientRegistrationRepository} 客户端注册信息存储库实例
     */
    @Bean
    @ConditionalOnProperty(prefix = "athena.security.client", name = "type", havingValue = "IN_MEMORY", matchIfMissing = true)
    public ClientRegistrationRepository clientRegistrationRepository(OAuth2ClientProperties properties) {
        return new InMemoryClientRegistrationRepository(new DefaultOAuth2ClientPropertiesMapper(properties).getClientRegistrations());
    }
}
