package com.gls.athena.security.servlet.handler;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.gls.athena.common.bean.result.Result;
import com.gls.athena.common.bean.result.ResultStatus;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 默认验证失败处理器
 *
 * @author george
 */
@Slf4j
@Component
public class DefaultAuthenticationFailureHandler implements AuthenticationFailureHandler {

    /**
     * 在验证过程中遇到异常时调用。
     *
     * @param request   请求
     * @param response  响应
     * @param exception 异常
     * @throws IOException      异常
     * @throws ServletException 异常
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        // 记录异常日志
        String message = null;
        if (exception instanceof OAuth2AuthenticationException oauth2AuthenticationException) {
            // OAuth2异常
            message = oauth2AuthenticationException.getError().getDescription();
        }
        if (StrUtil.isBlank(message)) {
            message = exception.getMessage();
        }
        // 输出异常信息
        log.error(message, exception);
        Result<String> result = ResultStatus.PARAM_ERROR.toResult(message);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSONUtil.toJsonStr(result));
    }
}
