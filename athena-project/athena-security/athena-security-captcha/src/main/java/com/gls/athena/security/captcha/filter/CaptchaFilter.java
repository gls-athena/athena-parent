package com.gls.athena.security.captcha.filter;

import cn.hutool.json.JSONUtil;
import com.gls.athena.common.bean.result.Result;
import com.gls.athena.common.bean.result.ResultStatus;
import com.gls.athena.security.captcha.provider.CaptchaProvider;
import com.gls.athena.security.captcha.provider.CaptchaProviderManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
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
@Slf4j
@Component
@RequiredArgsConstructor
public class CaptchaFilter extends OncePerRequestFilter implements OrderedFilter {

    /**
     * 验证码提供者管理器，用于获取适当的验证码服务实现
     */
    private final CaptchaProviderManager captchaProviderManager;

    private final AuthenticationFailureHandler authenticationFailureHandler = this::onAuthenticationFailure;

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
        try {
            // 获取验证码提供者
            CaptchaProvider captchaProvider = captchaProviderManager.getProvider(request);
            // 如果没有找到验证码提供者，直接放行请求
            if (captchaProvider == null) {
                filterChain.doFilter(request, response);
                log.info("没有找到适合的验证码提供者，直接放行请求");
                return;
            }

            // 如果请求需要发送验证码，则发送验证码并结束过滤
            if (captchaProvider.isSendCaptchaRequest(request)) {
                captchaProvider.sendCaptcha(request, response);
                log.info("验证码发送成功");
                return;
            }

            // 如果请求需要验证验证码，则进行验证码验证
            if (captchaProvider.isValidateCaptchaRequest(request)) {
                captchaProvider.validateCaptcha(request);
                log.info("验证码验证成功");
            }

            // 放行请求，允许后续处理
            filterChain.doFilter(request, response);
        } catch (CaptchaException e) {
            // 捕获异常并转发给异常处理程序
            log.error("验证码处理异常: {}", e.getMessage(), e);
            authenticationFailureHandler.onAuthenticationFailure(request, response, e);
        }
    }

    /**
     * 认证失败时的处理方法，返回401未授权响应和JSON格式的错误信息
     *
     * @param request  HTTP请求对象
     * @param response HTTP响应对象
     * @param e        认证异常对象，包含错误信息
     */
    private void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {
        // 设置响应状态码为401未授权
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // 设置响应内容类型为JSON
        response.setContentType("application/json; charset=UTF-8");
        // 构建并写入JSON格式的错误响应
        Result<String> result = ResultStatus.FAIL.toResult(e.getMessage());
        response.getWriter().write(JSONUtil.toJsonStr(result));
    }

    /**
     * 获取过滤器的执行顺序
     * <p>
     * 此方法用于确定当前过滤器在过滤器链中的执行顺序
     * 返回值越小，优先级越高，过滤器越早执行
     *
     * @return 过滤器执行顺序，设置为REQUEST_WRAPPER_FILTER_MAX_ORDER - 1000，
     * 确保在请求包装过滤器之后、其他业务过滤器之前执行
     */
    @Override
    public int getOrder() {
        return REQUEST_WRAPPER_FILTER_MAX_ORDER - 1000;
    }
}

