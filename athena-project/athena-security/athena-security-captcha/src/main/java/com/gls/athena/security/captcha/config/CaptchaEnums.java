/**
 * 验证码类型枚举
 * 用于定义系统中可能存在的验证码类型，包括无验证码、图片验证码、短信验证码和邮件验证码
 * 实现了IEnum接口，以提供通用的枚举操作
 */
package com.gls.athena.security.captcha.config;

import com.gls.athena.common.bean.base.IEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 验证码类型枚举
 *
 * @author george
 */
@Getter
@RequiredArgsConstructor
public enum CaptchaEnums implements IEnum<String> {

    /**
     * 无验证码类型
     * 表示不使用任何验证码进行验证
     */
    NONE("none", "无验证码"),

    /**
     * 图片验证码类型
     * 通过图形化的验证码进行用户验证，以防止自动化软件的恶意操作
     */
    IMAGE("image", "图片验证码"),

    /**
     * 短信验证码类型
     * 通过发送短信的方式提供一次性验证码，用于用户身份验证
     */
    SMS("sms", "短信验证码"),

    /**
     * 邮件验证码类型
     * 通过发送邮件的方式提供一次性验证码，用于用户身份验证
     */
    EMAIL("email", "邮件验证码");

    /**
     * 枚举元素的代码
     */
    private final String code;

    /**
     * 枚举元素的名称
     */
    private final String name;

    /**
     * 根据验证码类型代码获取对应的枚举实例
     *
     * @param code 验证码类型代码，用于标识不同的验证码类型
     * @return 返回匹配的验证码枚举实例，如果找不到匹配的枚举，则返回null
     */
    public static CaptchaEnums getByCode(String code) {
        // 使用IEnum工具类根据代码获取对应的枚举实例
        return IEnum.of(CaptchaEnums.class, code);
    }

    /**
     * 根据名称获取对应的验证码枚举类型
     *
     * @param name 验证码枚举类型的名称
     * @return 对应名称的验证码枚举类型，如果不存在则返回null
     */
    public static CaptchaEnums getByName(String name) {
        return IEnum.fromName(CaptchaEnums.class, name);
    }
}
