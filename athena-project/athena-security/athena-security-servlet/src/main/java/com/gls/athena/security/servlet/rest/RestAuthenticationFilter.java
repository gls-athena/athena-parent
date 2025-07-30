package com.gls.athena.security.servlet.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Rest认证过滤器
 *
 * @author george
 */
@Setter
public class RestAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    /**
     * 认证转换器
     */
    private AuthenticationConverter authenticationConverter;

    /**
     * 尝试认证
     *
     * @param request  请求
     * @param response 响应
     * @return 认证
     * @throws AuthenticationException 认证异常
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        Authentication authRequest = authenticationConverter.convert(request);
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    /**
     * 设置详情
     *
     * @param request        请求
     * @param authentication 认证
     */
    private void setDetails(HttpServletRequest request, Authentication authentication) {
        if (authentication instanceof AbstractAuthenticationToken abstractAuthenticationToken) {
            abstractAuthenticationToken.setDetails(authenticationDetailsSource.buildDetails(request));
        }
    }
}
