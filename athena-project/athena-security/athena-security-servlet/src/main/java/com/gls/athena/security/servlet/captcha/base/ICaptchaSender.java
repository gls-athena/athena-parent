package com.gls.athena.security.servlet.captcha.base;

import jakarta.servlet.http.HttpServletResponse;

/**
 * 验证码发送器接口
 * <p>
 * 定义了验证码发送的标准行为。实现类需要指定具体的验证码类型并实现发送逻辑。
 *
 * @param <Captcha> 验证码对象类型参数,必须继承自BaseCaptcha
 * @author george
 */
@FunctionalInterface
public interface ICaptchaSender<Captcha extends BaseCaptcha> {

    /**
     * 发送验证码
     * <p>
     * 将生成的验证码发送到指定目标。发送方式由实现类决定,可以是短信、邮件等。
     *
     * @param target   验证码接收目标(如手机号、邮箱等)
     * @param captcha  待发送的验证码对象
     * @param response HTTP响应对象,用于处理发送结果
     */
    void send(String target, Captcha captcha, HttpServletResponse response);
}
