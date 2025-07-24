package com.gls.athena.security.rest.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * REST认证过滤器
 * 用于处理RESTful API的用户认证请求，继承自UsernamePasswordAuthenticationFilter
 * 通过自定义的AuthenticationConverter来转换认证请求
 *
 * @author george
 */
public class RestAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Setter
    private AuthenticationConverter authenticationConverter;

    /**
     * 尝试进行用户认证
     * 使用配置的authenticationConverter将HTTP请求转换为Authentication对象，
     * 然后通过认证管理器进行认证处理
     *
     * @param request  HTTP请求对象，包含用户的认证信息
     * @param response HTTP响应对象，用于返回认证结果
     * @return Authentication 认证成功后的Authentication对象，如果认证失败则返回null
     * @throws AuthenticationException 认证过程中发生的异常
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // 使用converter将请求转换为Authentication对象
        Authentication authentication = authenticationConverter.convert(request);
        if (authentication == null) {
            return null;
        }
        // 使用认证管理器进行认证
        return getAuthenticationManager().authenticate(authentication);
    }
}
