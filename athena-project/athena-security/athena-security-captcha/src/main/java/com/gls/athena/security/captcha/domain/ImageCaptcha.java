package com.gls.athena.security.captcha.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.awt.image.BufferedImage;

/**
 * @author lizy19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ImageCaptcha extends Captcha {
    /**
     * 验证码图片
     * 使用BufferedImage存储生成的验证码图片数据
     * 标记@JsonIgnore表示在JSON序列化时忽略此字段
     */
    @JsonIgnore
    private BufferedImage image;
}
