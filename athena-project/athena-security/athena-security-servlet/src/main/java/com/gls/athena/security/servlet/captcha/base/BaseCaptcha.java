package com.gls.athena.security.servlet.captcha.base;

import lombok.Data;

import java.util.Date;

/**
 * 验证码基础抽象类
 * <p>
 * 提供验证码的基本属性和行为,具体验证码实现类需要继承此类
 * 并实现自己的验证码生成和校验逻辑
 * </p>
 *
 * @author george
 * @since 1.0.0
 */
@Data
public abstract class BaseCaptcha {
    /**
     * 验证码内容
     * <p>
     * 可以是数字、字母或其他字符的组合
     * </p>
     */
    private String code;

    /**
     * 验证码过期时间
     * <p>
     * 超过此时间验证码将失效,需要重新生成
     * </p>
     */
    private Date expireTime;
}
