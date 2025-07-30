package com.gls.athena.security.servlet.captcha.sms;

import com.gls.athena.security.servlet.captcha.base.BaseCaptcha;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 短信验证码实体类
 *
 * <p>该类继承自BaseCaptcha，用于表示短信验证码的数据结构。
 * 短信验证码主要用于用户手机号验证、重置密码等场景。</p>
 *
 * <p>使用@Data注解自动生成getter、setter等方法
 * 使用@EqualsAndHashCode注解生成equals和hashCode方法，并包含父类字段</p>
 *
 * @author george
 * @since 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SmsCaptcha extends BaseCaptcha {
}
