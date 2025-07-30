package com.gls.athena.security.servlet.captcha;

import cn.hutool.core.text.AntPathMatcher;
import cn.hutool.core.util.StrUtil;
import com.gls.athena.security.servlet.captcha.base.BaseCaptcha;
import com.gls.athena.security.servlet.captcha.base.ICaptchaGenerator;
import com.gls.athena.security.servlet.captcha.base.ICaptchaSender;
import com.gls.athena.security.servlet.captcha.repository.ICaptchaRepository;
import com.gls.athena.starter.web.util.WebUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.List;

/**
 * 验证码提供器
 *
 * @param <Captcha> 验证码类型
 * @author george
 */
@RequiredArgsConstructor
public class CaptchaProvider<Captcha extends BaseCaptcha> {
    /**
     * 添加常量和路径匹配器
     */
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    private static final String GRANT_TYPE_PARAM = "grant_type";
    private static final String PASSWORD_GRANT = "password";
    private static final String SMS_GRANT = "sms";
    /**
     * 验证码存储器
     */
    private final ICaptchaRepository repository;
    /**
     * 验证码生成器
     */
    private final ICaptchaGenerator<Captcha> generator;
    /**
     * 验证码发送器
     */
    private final ICaptchaSender<Captcha> sender;
    /**
     * code参数名
     */
    private final String codeParameterName;
    /**
     * target参数名
     */
    private final String targetParameterName;
    /**
     * 发送验证码url
     */
    private final String url;
    /**
     * 需要校验验证码的url
     */
    private final List<String> urls;
    /**
     * 登录处理 URL
     */
    private final String loginProcessingUrl;
    /**
     * oauth2 token url
     */
    private final String oauth2TokenUrl;

    /**
     * 发送验证码
     *
     * @param request 请求
     */
    public void send(ServletWebRequest request) {
        // 获取接收目标
        String target = getTarget(request);
        // 生成验证码
        Captcha captcha = generator.generate();
        // 保存验证码
        repository.save(target, captcha);
        // 发送验证码
        sender.send(target, captcha, request.getResponse());
    }

    /**
     * 验证验证码
     *
     * @param request 请求
     */
    public void verify(ServletWebRequest request) {
        String target = getTarget(request);
        String code = getCode(request);

        if (StrUtil.isBlank(target) || StrUtil.isBlank(code)) {
            throw new CaptchaAuthenticationException("验证码参数不完整");
        }

        BaseCaptcha baseCaptcha = repository.get(target);
        if (baseCaptcha == null) {
            throw new CaptchaAuthenticationException("验证码不存在或已过期");
        }

        if (!isValidCaptcha(baseCaptcha, code)) {
            throw new CaptchaAuthenticationException("验证码错误");
        }

        repository.remove(target);
    }

    /**
     * 是否支持
     *
     * @param request 请求
     * @return 是否支持
     */
    public boolean support(ServletWebRequest request) {
        return isSendRequest(request) || isVerifyRequest(request);
    }

    /**
     * 是否是发送请求
     *
     * @param request 请求
     * @return 是否是发送请求
     */
    public boolean isSendRequest(ServletWebRequest request) {
        return PATH_MATCHER.match(url, getRequestUri(request));
    }

    /**
     * 是否是校验请求
     *
     * @param request 请求
     * @return 是否是校验请求
     */
    private boolean isVerifyRequest(ServletWebRequest request) {
        if (isLogin(request)) {
            return true;
        }
        String requestUri = getRequestUri(request);
        return urls.stream().anyMatch(url -> PATH_MATCHER.match(url, requestUri));
    }

    /**
     * 是否登录
     *
     * @param request 请求
     * @return 是否登录
     */
    private boolean isLogin(ServletWebRequest request) {
        String requestUri = getRequestUri(request);

        if (requestUri.contains(loginProcessingUrl)) {
            return hasValidTarget(request);
        }

        if (requestUri.contains(oauth2TokenUrl)) {
            return isPasswordOrSmsGrant(request);
        }

        return false;
    }

    /**
     * 获取接收目标
     *
     * @param request 请求
     * @return 接收目标
     */
    private String getTarget(ServletWebRequest request) {
        return WebUtil.getParameter(request.getRequest(), targetParameterName);
    }

    /**
     * 获取验证码
     *
     * @param request 请求
     * @return 验证码
     */
    private String getCode(ServletWebRequest request) {
        return WebUtil.getParameter(request.getRequest(), codeParameterName);
    }

    /**
     * 提取验证 target 参数的方法
     *
     * @param request 请求
     * @return 是否有效
     */
    private boolean hasValidTarget(ServletWebRequest request) {
        String target = WebUtil.getParameter(request.getRequest(), targetParameterName);
        return StrUtil.isNotBlank(target);
    }

    /**
     * 提取验证 grant_type 的方法
     *
     * @param request 请求
     * @return 是否有效
     */
    private boolean isPasswordOrSmsGrant(ServletWebRequest request) {
        String grantType = request.getParameter(GRANT_TYPE_PARAM);
        return StrUtil.containsIgnoreCase(grantType, PASSWORD_GRANT)
                || StrUtil.containsIgnoreCase(grantType, SMS_GRANT);
    }

    /**
     * 添加获取请求URI的工具方法
     *
     * @param request 请求
     * @return 请求URI
     */
    private String getRequestUri(ServletWebRequest request) {
        return request.getRequest().getRequestURI();
    }

    /**
     * 提取验证码验证逻辑
     *
     * @param captcha 验证码
     * @param code    输入的验证码
     * @return 是否有效
     */
    private boolean isValidCaptcha(BaseCaptcha captcha, String code) {
        return captcha.getCode().equals(code)
                && captcha.getExpireTime().getTime() > System.currentTimeMillis();
    }

}
