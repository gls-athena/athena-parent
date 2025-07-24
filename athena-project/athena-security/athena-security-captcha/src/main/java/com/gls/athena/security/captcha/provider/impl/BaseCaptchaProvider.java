package com.gls.athena.security.captcha.provider.impl;

import cn.hutool.core.text.AntPathMatcher;
import cn.hutool.core.util.StrUtil;
import com.gls.athena.security.captcha.config.CaptchaEnums;
import com.gls.athena.security.captcha.config.CaptchaProperties;
import com.gls.athena.security.captcha.domain.Captcha;
import com.gls.athena.security.captcha.filter.CaptchaException;
import com.gls.athena.security.captcha.provider.CaptchaProvider;
import com.gls.athena.security.captcha.repository.CaptchaRepository;
import com.gls.athena.starter.web.util.WebUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * 验证码提供者的基类，实现CaptchaProvider接口
 * 提供了验证码处理的基础功能，包括验证码类型判断、发送验证码、验证验证码等通用逻辑
 *
 * @param <C> 验证码类型，继承自Captcha
 * @author george
 */
@RequiredArgsConstructor
public abstract class BaseCaptchaProvider<C extends Captcha> implements CaptchaProvider {

    private final CaptchaProperties properties;
    private final CaptchaRepository repository;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 判断当前请求是否为验证码类型请求
     *
     * @param request HTTP请求对象
     * @return 如果是验证码类型请求返回true，否则返回false
     */
    @Override
    public boolean isCaptchaTypeRequest(HttpServletRequest request) {
        // 从请求中获取验证码类型参数
        String captchaType = WebUtil.getParameter(request, properties.getTypeParam());
        // 根据验证码类型参数获取对应的枚举对象
        CaptchaEnums captchaEnums = CaptchaEnums.getByCode(captchaType);
        // 判断验证码类型是否为SMS
        return isCaptchaTypeRequest(captchaEnums);
    }

    /**
     * 判断指定的验证码类型是否为当前服务处理的类型
     *
     * @param captchaEnums 验证码类型枚举对象
     * @return 如果指定的验证码类型为当前服务处理的类型返回true，否则返回false
     */
    protected abstract boolean isCaptchaTypeRequest(CaptchaEnums captchaEnums);

    /**
     * 判断当前请求是否为发送验证码请求
     *
     * @param request HTTP请求对象
     * @return 如果是发送验证码请求返回true，否则返回false
     */
    @Override
    public boolean isSendCaptchaRequest(HttpServletRequest request) {
        String captchaUrl = getCaptchaUrl();
        return pathMatcher.match(captchaUrl, request.getRequestURI());
    }

    /**
     * 获取发送验证码请求的URL
     *
     * @return 发送验证码请求的URL
     */
    protected abstract String getCaptchaUrl();

    /**
     * 发送验证码
     *
     * @param request  HTTP请求对象
     * @param response HTTP响应对象
     */
    @Override
    public void sendCaptcha(HttpServletRequest request, HttpServletResponse response) {
        // 从请求参数中获取手机号码
        String key = WebUtil.getParameter(request, getKeyParam());
        if (StrUtil.isBlank(key)) {
            throw new CaptchaException("验证码参数不完整");
        }
        // 生成验证码
        C captcha = generateCaptcha();
        // 将手机号码和对应的验证码保存到验证码仓库中
        repository.saveCaptcha(key, captcha);
        // 执行发送验证码的操作
        doSendCaptcha(key, captcha, response);
    }

    /**
     * 执行发送验证码的操作
     *
     * @param key      手机号
     * @param captcha  验证码对象
     * @param response HTTP响应对象
     */
    protected abstract void doSendCaptcha(String key, C captcha, HttpServletResponse response);

    /**
     * 生成验证码
     *
     * @return 生成的验证码对象
     */
    protected abstract C generateCaptcha();

    /**
     * 获取验证码参数的键名
     *
     * @return 验证码参数的键名
     */
    protected abstract String getKeyParam();

    /**
     * 判断当前请求是否为验证验证码请求
     *
     * @param request HTTP请求对象
     * @return 如果是验证验证码请求返回true，否则返回false
     */
    @Override
    public boolean isValidateCaptchaRequest(HttpServletRequest request) {
        // 检查当前请求是否为登录请求
        if (isLoginRequest(request)) {
            return true;
        }
        // 遍历配置中的验证码校验URL模式列表，检查当前请求URI是否匹配任何模式
        return getCaptchaCheckUrls()
                .stream()
                .anyMatch(captchaCheckUrl -> pathMatcher.match(captchaCheckUrl, request.getRequestURI()));
    }

    /**
     * 判断当前请求是否为登录请求
     *
     * @param request HTTP请求对象，用于获取请求URI和参数
     * @return 如果请求匹配登录URL且包含非空的验证码参数返回true，否则返回false
     */
    private boolean isLoginRequest(HttpServletRequest request) {
        // 检查请求的URI是否匹配登录URL或OAuth2令牌URL
        if (pathMatcher.match(properties.getLoginUrl(), request.getRequestURI())
                || pathMatcher.match(properties.getOauth2TokenUrl(), request.getRequestURI())) {
            String key = WebUtil.getParameter(request, getKeyParam());
            return StrUtil.isNotBlank(key);
        }
        return false;
    }

    /**
     * 获取验证码校验URL模式列表
     *
     * @return 验证码校验URL模式列表
     */
    protected abstract List<String> getCaptchaCheckUrls();

    /**
     * 验证验证码
     *
     * @param request HTTP请求对象
     */
    @Override
    public void validateCaptcha(HttpServletRequest request) {
        // 获取请求参数中的手机号码和验证码
        String key = WebUtil.getParameter(request, getKeyParam());
        String captchaCode = WebUtil.getParameter(request, getCaptchaCodeParam());

        // 检查手机号码和验证码是否为空，如果任一参数为空，则抛出异常
        if (StrUtil.isBlank(key) || StrUtil.isBlank(captchaCode)) {
            throw new CaptchaException("验证码参数不完整");
        }

        // 从验证码仓库中获取对应的验证码对象
        Captcha captcha = repository.getCaptcha(key);
        // 如果验证码对象为空，说明验证码不存在或已过期，抛出异常
        if (captcha == null) {
            throw new CaptchaException("验证码不存在或已过期");
        }

        // 验证用户输入的验证码与系统生成的验证码是否匹配，如果不匹配，则抛出异常
        if (!isValidCaptcha(captcha, captchaCode)) {
            throw new CaptchaException("验证码错误");
        }

        // 验证码验证通过后，从验证码仓库中移除该验证码，避免重复使用
        repository.removeCaptcha(key);
    }

    /**
     * 获取验证码代码参数的键名
     *
     * @return 验证码代码参数的键名
     */
    protected abstract String getCaptchaCodeParam();

    /**
     * 验证验证码是否有效
     *
     * @param captcha     验证码对象
     * @param captchaCode 用户输入的验证码代码
     * @return 如果验证码有效返回true，否则返回false
     */
    private boolean isValidCaptcha(Captcha captcha, String captchaCode) {
        // 检查用户输入的验证码代码是否与生成的验证码代码匹配
        // 检查验证码的过期时间是否晚于当前时间，以确保验证码尚未过期
        return captcha.getCode().equals(captchaCode)
                && captcha.getExpireTime().getTime() > System.currentTimeMillis();
    }
}
