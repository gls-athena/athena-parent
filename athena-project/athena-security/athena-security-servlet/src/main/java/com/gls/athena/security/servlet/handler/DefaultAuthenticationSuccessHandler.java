package com.gls.athena.security.servlet.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 默认验证成功处理器
 *
 * @author george
 */
@Slf4j
@Component
public class DefaultAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    /**
     * 在验证完成后调用。
     *
     * @param request        请求
     * @param response       响应
     * @param authentication 验证
     * @throws IOException      异常
     * @throws ServletException 异常
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("登录成功");
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("登录成功");
    }
}
