package com.gls.athena.security.rest.converter;

import cn.hutool.core.util.StrUtil;
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
     * 将HTTP请求转换为认证对象
     *
     * @param request HTTP请求对象，包含用户名和密码参数
     * @return Authentication 认证对象，如果用户名或密码为空则返回null
     */
    @Override
    public Authentication convert(HttpServletRequest request) {
        // 从请求中获取用户名和密码参数
        String username = WebUtil.getParameter(request, usernameParameter);
        String password = WebUtil.getParameter(request, passwordParameter);

        // 如果用户名或密码为空，则返回null
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            return null;
        }

        // 创建未认证的用户名密码认证令牌
        return UsernamePasswordAuthenticationToken.unauthenticated(username, password);
    }

}
