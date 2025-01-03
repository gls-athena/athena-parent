package com.gls.athena.security.servlet.captcha;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * 验证码过滤器
 *
 * @author george
 */
@Slf4j
@RequiredArgsConstructor
public class CaptchaFilter extends OncePerRequestFilter {
    /**
     * 认证失败处理器
     */
    private final AuthenticationFailureHandler authenticationFailureHandler;
    /**
     * 验证码管理器
     */
    private final List<CaptchaProvider<?>> providers;

    /**
     * 过滤器逻辑
     *
     * @param request     请求
     * @param response    响应
     * @param filterChain 过滤器链
     * @throws ServletException 异常
     * @throws IOException      异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ServletWebRequest webRequest = new ServletWebRequest(request, response);

        try {
            processCaptchaRequest(webRequest, filterChain);
        } catch (CaptchaAuthenticationException e) {
            log.error("验证码处理失败: {}", e.getMessage(), e);
            authenticationFailureHandler.onAuthenticationFailure(request, response, e);
        }
    }

    /**
     * 处理验证码请求
     *
     * @param webRequest  请求上下文
     * @param filterChain 过滤器链
     */
    private void processCaptchaRequest(ServletWebRequest webRequest,
                                       FilterChain filterChain) throws ServletException, IOException {
        Optional<CaptchaProvider<?>> providerOpt = findSupportedProvider(webRequest);

        if (providerOpt.isEmpty()) {
            log.debug("未找到匹配的验证码处理器，跳过验证码校验");
            filterChain.doFilter(webRequest.getRequest(), webRequest.getResponse());
            return;
        }

        CaptchaProvider<?> provider = providerOpt.get();

        if (provider.isSendRequest(webRequest)) {
            log.debug("处理发送验证码请求");
            provider.send(webRequest);
            return;
        }

        log.debug("验证验证码");
        provider.verify(webRequest);
        filterChain.doFilter(webRequest.getRequest(), webRequest.getResponse());
    }

    /**
     * 查找支持当前请求的验证码提供器
     *
     * @param request 请求上下文
     * @return 验证码提供器
     */
    private Optional<CaptchaProvider<?>> findSupportedProvider(ServletWebRequest request) {
        return providers.stream()
                .filter(provider -> provider.support(request))
                .findFirst();
    }

}
