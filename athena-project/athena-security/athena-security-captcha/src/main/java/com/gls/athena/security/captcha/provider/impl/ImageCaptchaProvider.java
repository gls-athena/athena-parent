package com.gls.athena.security.captcha.provider.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.date.DateUtil;
import com.gls.athena.security.captcha.config.CaptchaEnums;
import com.gls.athena.security.captcha.config.CaptchaProperties;
import com.gls.athena.security.captcha.config.ImageCaptchaProperties;
import com.gls.athena.security.captcha.domain.ImageCaptcha;
import com.gls.athena.security.captcha.filter.CaptchaException;
import com.gls.athena.security.captcha.repository.CaptchaRepository;
import com.gls.athena.starter.web.enums.FileEnums;
import com.gls.athena.starter.web.util.WebUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.io.OutputStream;
import java.util.List;

/**
 * 图片验证码提供者实现类
 * 用于生成和发送图片验证码，并提供相关的配置和逻辑支持
 *
 * @author george
 */
@Slf4j
public class ImageCaptchaProvider extends BaseCaptchaProvider<ImageCaptcha> {

    private final ImageCaptchaProperties properties;

    /**
     * 构造方法注入必要的属性
     *
     * @param properties        验证码属性配置
     * @param captchaRepository 验证码仓库
     */
    public ImageCaptchaProvider(CaptchaProperties properties, CaptchaRepository captchaRepository) {
        super(properties, captchaRepository);
        this.properties = properties.getImage();
    }

    /**
     * 判断是否是图片验证码类型请求
     *
     * @param captchaEnums 验证码枚举类型
     * @return 如果是图片验证码类型返回true，否则返回false
     */
    @Override
    protected boolean isCaptchaTypeRequest(CaptchaEnums captchaEnums) {
        return CaptchaEnums.IMAGE.equals(captchaEnums);
    }

    /**
     * 获取图片验证码发送的URL
     *
     * @return 图片验证码发送的URL
     */
    @Override
    protected String getCaptchaUrl() {
        return properties.getCaptchaUrl();
    }

    /**
     * 执行发送验证码逻辑
     *
     * @param key      验证码键
     * @param captcha  图片验证码对象
     * @param response HTTP响应对象
     */
    @Override
    protected void doSendCaptcha(String key, ImageCaptcha captcha, HttpServletResponse response) {
        try (OutputStream out = WebUtil.createOutputStream(response, key, FileEnums.PNG)) {
            // 将验证码图片以PNG格式写入输出流
            ImageIO.write(captcha.getImage(), "PNG", out);
        } catch (Exception e) {
            // 记录验证码发送失败的日志
            log.error("发送验证码失败", e);
            // 抛出运行时异常，提示验证码发送失败
            throw new CaptchaException("发送验证码失败", e);
        }
    }

    /**
     * 生成图片验证码对象
     *
     * @return 生成的图片验证码对象
     */
    @Override
    protected ImageCaptcha generateCaptcha() {
        // 创建一个线条验证码对象，参数分别为宽度、高度、字符数量、线条数量和字体大小
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(properties.getWidth(),
                properties.getHeight(), properties.getCodeCount(), properties.getLineCount(), properties.getSize());
        // 记录生成的验证码内容以便于调试或后续验证
        log.debug("生成验证码：{}", lineCaptcha.getCode());
        // 创建一个图片验证码对象来存储验证码信息
        ImageCaptcha imageCaptcha = new ImageCaptcha();
        // 设置图片验证码的代码
        imageCaptcha.setCode(lineCaptcha.getCode());
        // 设置图片验证码的图片
        imageCaptcha.setImage(lineCaptcha.getImage());
        // 设置图片验证码的过期时间，偏移秒数从当前时间开始计算
        imageCaptcha.setExpireTime(DateUtil.offsetSecond(DateUtil.date(), properties.getCaptchaExpire()).toJdkDate());
        // 返回生成的图片验证码对象
        return imageCaptcha;
    }

    /**
     * 获取验证码接口中UUID参数的名称
     *
     * @return UUID参数的名称
     */
    @Override
    protected String getKeyParam() {
        return properties.getUuidParam();
    }

    /**
     * 获取需要进行验证码校验的URL列表
     *
     * @return 需要进行验证码校验的URL列表
     */
    @Override
    protected List<String> getCaptchaCheckUrls() {
        return properties.getCaptchaCheckUrls();
    }

    /**
     * 获取验证码代码参数的名称
     *
     * @return 验证码代码参数的名称
     */
    @Override
    protected String getCaptchaCodeParam() {
        return properties.getCaptchaParam();
    }
}
