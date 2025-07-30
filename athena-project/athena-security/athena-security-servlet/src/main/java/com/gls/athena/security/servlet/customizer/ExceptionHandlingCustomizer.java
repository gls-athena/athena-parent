package com.gls.athena.security.servlet.customizer;

import com.gls.athena.security.servlet.rest.RestProperties;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 异常处理自定义器
 *
 * @author george
 */
@Component
public class ExceptionHandlingCustomizer
        implements Customizer<ExceptionHandlingConfigurer<HttpSecurity>> {

    /**
     * rest 安全属性配置
     */
    @Resource
    private RestProperties restProperties;

    /**
     * 自定义
     *
     * @param configurer 配置器
     */
    @Override
    public void customize(ExceptionHandlingConfigurer<HttpSecurity> configurer) {
        // 配置登录入口点
        AuthenticationEntryPoint authenticationEntryPoint = new LoginUrlAuthenticationEntryPoint(restProperties.getLoginPage());
        // 创建请求匹配器
        RequestMatcher requestMatcher = createRequestMatcher();
        // 配置异常处理 - 登录入口点
        configurer.defaultAuthenticationEntryPointFor(authenticationEntryPoint, requestMatcher);
    }

    /**
     * 创建请求匹配器 - 仅匹配HTML请求
     *
     * @return 请求匹配器
     */
    private RequestMatcher createRequestMatcher() {
        MediaTypeRequestMatcher requestMatcher = new MediaTypeRequestMatcher(MediaType.TEXT_HTML);
        requestMatcher.setIgnoredMediaTypes(Set.of(MediaType.ALL));
        return requestMatcher;
    }
}
