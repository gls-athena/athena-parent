/**
 * 验证码服务接口，定义了验证码的发送和验证相关的操作
 * 该接口主要用于处理与验证码相关的请求，包括判断是否需要发送或验证验证码，以及执行相应的操作
 */
package com.gls.athena.security.captcha.service;

import cn.hutool.core.text.AntPathMatcher;
import cn.hutool.core.util.StrUtil;
import com.gls.athena.security.captcha.config.CaptchaEnums;
import com.gls.athena.security.captcha.config.CaptchaProperties;
import com.gls.athena.security.captcha.domain.Captcha;
import com.gls.athena.security.captcha.repository.CaptchaRepository;
import com.gls.athena.starter.web.util.WebUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * 验证码服务接口
 *
 * @author george
 */
@RequiredArgsConstructor
public abstract class BaseCaptchaService<C extends Captcha> {

    private final CaptchaProperties properties;

    private final CaptchaRepository captchaRepository;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 判断当前请求是否支持验证码操作，包括发送、验证和类型请求
     *
     * @param request HTTP请求对象
     * @return 如果当前请求支持验证码操作返回true，否则返回false
     */
    public boolean supports(HttpServletRequest request) {
        return isSendCaptchaRequest(request) || isValidateCaptchaRequest(request) || isCaptchaTypeRequest(request);
    }

    /**
     * 判断当前请求是否为验证码类型请求
     *
     * @param request HTTP请求对象
     * @return 如果当前请求为验证码类型请求返回true，否则返回false
     */
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
     * @return 如果当前请求为发送验证码请求返回true，否则返回false
     */
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
    public void sendCaptcha(HttpServletRequest request, HttpServletResponse response) {
        // 从请求参数中获取手机号码
        String key = WebUtil.getParameter(request, getKeyParam());
        // 生成验证码
        C captcha = generateCaptcha();
        // 将手机号码和对应的验证码保存到验证码仓库中
        captchaRepository.saveCaptcha(key, captcha);
        // 执行发送验证码的操作
        doSendCaptcha(key, captcha, response);
    }

    /**
     * 执行发送验证码的操作
     *
     * @param key      手机号码
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
     * @return 如果当前请求为验证验证码请求返回true，否则返回false
     */
    public boolean isValidateCaptchaRequest(HttpServletRequest request) {
        // 遍历配置中的验证码校验URL模式列表，检查当前请求URI是否匹配任何模式
        return getCaptchaCheckUrls()
                .stream()
                .anyMatch(captchaCheckUrl -> pathMatcher.match(captchaCheckUrl, request.getRequestURI()));
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
    public void validateCaptcha(HttpServletRequest request) {
        // 获取请求参数中的手机号码和验证码
        String key = WebUtil.getParameter(request, getKeyParam());
        String captchaCode = WebUtil.getParameter(request, getCaptchaCodeParam());

        // 检查手机号码和验证码是否为空，如果任一参数为空，则抛出异常
        if (StrUtil.isBlank(key) || StrUtil.isBlank(captchaCode)) {
            throw new IllegalArgumentException("验证码参数不完整");
        }

        // 从验证码仓库中获取对应的验证码对象
        Captcha captcha = captchaRepository.getCaptcha(key);
        // 如果验证码对象为空，说明验证码不存在或已过期，抛出异常
        if (captcha == null) {
            throw new IllegalArgumentException("验证码不存在或已过期");
        }

        // 验证用户输入的验证码与系统生成的验证码是否匹配，如果不匹配，则抛出异常
        if (!isValidCaptcha(captcha, captchaCode)) {
            throw new IllegalArgumentException("验证码错误");
        }

        // 验证码验证通过后，从验证码仓库中移除该验证码，避免重复使用
        captchaRepository.removeCaptcha(key);
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

