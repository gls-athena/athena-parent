package com.gls.athena.security.captcha.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author george
 */
@Data
public class Captcha implements Serializable {
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
