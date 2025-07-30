package com.gls.athena.security.servlet.captcha.base;

/**
 * 验证码生成器接口
 * <p>
 * 用于生成不同类型的验证码，如图片验证码、短信验证码等。
 * 实现该接口的类需要提供具体的验证码生成逻辑。
 *
 * @param <Captcha> 验证码类型，必须继承自 BaseCaptcha
 * @author george
 * @see BaseCaptcha
 */
@FunctionalInterface
public interface ICaptchaGenerator<Captcha extends BaseCaptcha> {

    /**
     * 生成验证码
     * <p>
     * 实现类需要提供具体的验证码生成算法，确保生成的验证码符合安全要求。
     *
     * @return 生成的验证码对象，类型为泛型参数指定的验证码类型
     */
    Captcha generate();
}
