package com.gls.athena.security.servlet.captcha.sms;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.gls.athena.security.servlet.captcha.base.ICaptchaGenerator;
import lombok.RequiredArgsConstructor;

/**
 * 短信验证码生成器
 * <p>
 * 用于生成数字格式的短信验证码，支持自定义验证码长度和有效期。
 * 生成的验证码将包含验证码内容和过期时间信息。
 * </p>
 *
 * @author george
 * @version 1.0
 * @since 1.0
 */
@RequiredArgsConstructor
public class SmsCaptchaGenerator implements ICaptchaGenerator<SmsCaptcha> {

    /**
     * 验证码长度
     * <p>
     * 定义生成的验证码数字位数
     * </p>
     */
    private final int length;

    /**
     * 过期时间
     * <p>
     * 验证码的有效期（单位：秒）
     * </p>
     */
    private final int expireIn;

    /**
     * 生成短信验证码
     * <p>
     * 根据配置的长度生成纯数字验证码，并设置其过期时间
     * </p>
     *
     * @return 包含验证码内容和过期时间的 {@link SmsCaptcha} 对象
     */
    @Override
    public SmsCaptcha generate() {
        // 参数校验
        if (length <= 0) {
            throw new IllegalArgumentException("验证码长度必须为正数");
        }
        if (expireIn <= 0) {
            throw new IllegalArgumentException("过期时间必须为正数");
        }

        SmsCaptcha smsCaptcha = new SmsCaptcha();
        // 使用更安全的随机数生成方式
        smsCaptcha.setCode(RandomUtil.randomNumbers(length));

        // 明确时区处理
        DateTime now = DateUtil.date();
        smsCaptcha.setExpireTime(DateUtil.offsetSecond(now, expireIn).toJdkDate());

        return smsCaptcha;
    }

}
