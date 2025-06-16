package com.gls.athena.security.servlet.captcha.image;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.date.DateUtil;
import com.gls.athena.security.servlet.captcha.base.ICaptchaGenerator;
import lombok.RequiredArgsConstructor;

/**
 * 图片验证码生成器
 * 用于生成包含随机字符的图片验证码,支持自定义验证码参数
 *
 * @author george
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class ImageCaptchaGenerator implements ICaptchaGenerator<ImageCaptcha> {
    /**
     * 验证码字符长度
     */
    private final int length;

    /**
     * 验证码有效期(秒)
     */
    private final int expireIn;

    /**
     * 验证码图片宽度(像素)
     */
    private final int width;

    /**
     * 验证码图片高度(像素)
     */
    private final int height;

    /**
     * 干扰线数量
     * 用于增加验证码识别难度
     */
    private final int lineCount;

    /**
     * 字体大小(像素)
     */
    private final float fontSize;

    /**
     * 生成图片验证码
     *
     * <p>该方法使用Hutool工具库创建带有线段干扰的验证码图片，并封装验证码信息返回。</p>
     * 生成的验证码包含以下信息：
     * 1. 验证码图片
     * 2. 验证码文本内容
     * 3. 验证码过期时间（当前时间+配置的过期时长）
     *
     * @return {@link ImageCaptcha} 包含验证码图片、验证码文本和过期时间的封装对象
     */
    @Override
    public ImageCaptcha generate() {
        // 使用Hutool工具创建带线段干扰的验证码，配置宽度、高度、字符长度、干扰线数量和字体大小
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(width, height, length, lineCount, fontSize);

        // 封装验证码信息：验证码文本、图片数据及计算过期时间
        ImageCaptcha imageCaptcha = new ImageCaptcha();
        imageCaptcha.setCode(lineCaptcha.getCode());
        imageCaptcha.setImage(lineCaptcha.getImage());
        imageCaptcha.setExpireTime(DateUtil.offsetSecond(DateUtil.date(), expireIn).toJdkDate());

        return imageCaptcha;
    }

}
