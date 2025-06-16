package com.gls.athena.security.servlet.rest;

import com.gls.athena.starter.web.util.WebUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;

/**
 * 用户名密码认证转换器
 *
 * @author george
 */
@Data
@Accessors(chain = true)
public class UsernamePasswordAuthenticationConverter implements AuthenticationConverter {
    /**
     * 用户名参数
     */
    private String usernameParameter = "username";
    /**
     * 密码参数
     */
    private String passwordParameter = "password";

    /**
     * 从HttpServletRequest中提取认证信息并转换为Authentication对象
     *
     * @param request HTTP请求对象，从中提取用户名和密码等认证信息
     * @return 包含用户名和密码的未认证Authentication对象，如果用户名或密码为空则返回null
     * @see UsernamePasswordAuthenticationToken
     */
    @Override
    public Authentication convert(HttpServletRequest request) {
        // 从请求中提取用户名和密码
        String username = obtainUsername(request);
        String password = obtainPassword(request);

        // 验证用户名和密码的有效性
        if (username == null || password == null) {
            return null;
        }

        // 创建并返回未认证的令牌对象
        return UsernamePasswordAuthenticationToken.unauthenticated(username, password);
    }

    /**
     * 获取用户名
     *
     * @param request 请求
     * @return 用户名
     */
    private String obtainUsername(HttpServletRequest request) {
        return WebUtil.getParameter(request, usernameParameter);
    }

    /**
     * 获取密码
     *
     * @param request 请求
     * @return 密码
     */
    private String obtainPassword(HttpServletRequest request) {
        return WebUtil.getParameter(request, passwordParameter);
    }

}
