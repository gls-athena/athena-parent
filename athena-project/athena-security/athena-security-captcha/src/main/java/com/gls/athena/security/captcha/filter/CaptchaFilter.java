package com.gls.athena.security.captcha.filter;

import com.gls.athena.security.captcha.service.BaseCaptchaService;
import com.gls.athena.security.captcha.service.CaptchaServiceManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 验证码过滤器，用于处理验证码的发送和验证
 * 该过滤器继承自OncePerRequestFilter，保证每次请求只执行一次过滤
 *
 * @author george
 */
@RequiredArgsConstructor
public class CaptchaFilter extends OncePerRequestFilter {

    private final CaptchaServiceManager captchaServiceManager;

    /**
     * 执行验证码过滤逻辑
     *
     * @param request     用于获取请求信息的HttpServletRequest对象
     * @param response    用于发送响应信息的HttpServletResponse对象
     * @param filterChain 过滤链，用于继续传递请求或响应
     * @throws ServletException 如果过滤过程中发生Servlet异常
     * @throws IOException      如果过滤过程中发生I/O异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 获取验证码服务
        BaseCaptchaService<?> captchaService = captchaServiceManager.getCaptchaService(request);
        if (captchaService == null) {
            // 没有匹配的验证码服务，继续过滤链
            filterChain.doFilter(request, response);
            return;
        }

        // 判断是否为发送验证码请求
        if (captchaService.isSendCaptchaRequest(request)) {
            // 处理发送验证码请求
            captchaService.sendCaptcha(request, response);
            // 发送验证码后直接返回，不继续过滤链
            return;
        }

        // 判断是否为验证验证码请求
        if (captchaService.isValidateCaptchaRequest(request)) {
            // 处理验证验证码请求
            captchaService.validateCaptcha(request);
        }

        // 继续过滤链
        filterChain.doFilter(request, response);
    }
}

