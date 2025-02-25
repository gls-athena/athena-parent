package com.gls.athena.security.servlet.captcha.image;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gls.athena.security.servlet.captcha.base.BaseCaptcha;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.awt.image.BufferedImage;

/**
 * 图片验证码实体类
 * 继承自BaseCaptcha基础验证码类，用于处理图形验证码相关的业务逻辑
 *
 * @author george
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ImageCaptcha extends BaseCaptcha {

    /**
     * 验证码图片
     * 使用BufferedImage存储生成的验证码图片数据
     * 标记@JsonIgnore表示在JSON序列化时忽略此字段
     */
    @JsonIgnore
    private BufferedImage image;
}
