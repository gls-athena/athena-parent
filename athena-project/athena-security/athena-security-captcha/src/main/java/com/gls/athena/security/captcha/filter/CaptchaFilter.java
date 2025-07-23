package com.gls.athena.security.captcha.filter;

import com.gls.athena.security.captcha.provider.CaptchaProvider;
import com.gls.athena.security.captcha.provider.CaptchaProviderManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * CaptchaFilter 类用于处理验证码相关的请求过滤逻辑
 * <p>
 * 该过滤器在每次请求时检查是否需要发送或验证验证码，并根据请求类型调用相应的验证码提供者方法
 * <p>
 * 它依赖于 CaptchaProviderManager 来获取适当的验证码服务实现
 * <p>
 * 通过使用 @RequiredArgsConstructor 注解，自动生成构造函数以注入所需的依赖项
 *
 * @author george
 */
@RequiredArgsConstructor
public class CaptchaFilter extends OncePerRequestFilter {

    /**
     * 验证码提供者管理器，用于获取适当的验证码服务实现
     */
    private final CaptchaProviderManager captchaProviderManager;

    /**
     * 执行验证码过滤逻辑
     * <p>
     * 此方法主要用于过滤请求，以处理验证码的发送和验证它首先根据请求获取相应的验证码提供者（CaptchaProvider），
     * 如果没有找到提供者，则直接放行请求如果请求需要发送验证码，则调用提供者的sendCaptcha方法发送验证码；
     * 如果请求需要验证验证码，则调用提供者的validateCaptcha方法进行验证无论上述条件是否满足，最终都会放行请求，
     * 以允许后续的过滤器或Servlet处理
     *
     * @param request     用于获取请求信息的HttpServletRequest对象
     * @param response    用于获取响应信息的HttpServletResponse对象
     * @param filterChain 过滤链，用于放行请求或传递到下一个过滤器
     * @throws ServletException 如果过滤过程中发生Servlet异常
     * @throws IOException      如果过滤过程中发生I/O异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 获取验证码提供者
        CaptchaProvider captchaProvider = captchaProviderManager.getCaptchaService(request);
        // 如果没有找到验证码提供者，直接放行请求
        if (captchaProvider == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 如果请求需要发送验证码，则发送验证码并结束过滤
        if (captchaProvider.isSendCaptchaRequest(request)) {
            captchaProvider.sendCaptcha(request, response);
            return;
        }

        // 如果请求需要验证验证码，则进行验证码验证
        if (captchaProvider.isValidateCaptchaRequest(request)) {
            captchaProvider.validateCaptcha(request);
        }

        // 放行请求，允许后续处理
        filterChain.doFilter(request, response);
    }
}

