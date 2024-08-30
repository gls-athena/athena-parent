package com.athena.security.servlet.client.customizer;

import com.athena.security.servlet.client.delegate.DelegateAuthorizationRequestResolver;
import jakarta.annotation.Resource;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.stereotype.Component;

/**
 * 授权端点自定义器
 */
@Component
public class AuthorizationEndpointCustomizer implements Customizer<OAuth2LoginConfigurer<HttpSecurity>.AuthorizationEndpointConfig> {

    /**
     * 委托授权请求解析器
     */
    @Resource
    private DelegateAuthorizationRequestResolver delegateAuthorizationRequestResolver;

    /**
     * 自定义 OAuth2 授权端点配置
     *
     * @param config 配置器
     */
    @Override
    public void customize(OAuth2LoginConfigurer<HttpSecurity>.AuthorizationEndpointConfig config) {
        // 自定义 OAuth2 授权请求解析器
        config.authorizationRequestResolver(delegateAuthorizationRequestResolver);
    }

}