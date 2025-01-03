package com.gls.athena.security.servlet.captcha.image;

import com.gls.athena.security.servlet.captcha.CaptchaAuthenticationException;
import com.gls.athena.security.servlet.captcha.base.ICaptchaSender;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 图片验证码发送器
 * <p>
 * 负责将生成的图片验证码写入HTTP响应流中，并设置适当的HTTP响应头。
 * 包含了验证码图片的缓存控制和安全相关的配置。
 * </p>
 *
 * @author george
 */
@Slf4j
public class ImageCaptchaSender implements ICaptchaSender<ImageCaptcha> {

    /**
     * 图片格式常量，使用PNG格式以确保图片质量和透明度支持
     */
    private static final String IMAGE_FORMAT = "PNG";

    /**
     * HTTP响应的Content-Type，用于指定响应内容为PNG图片
     */
    private static final String CONTENT_TYPE = "image/" + IMAGE_FORMAT.toLowerCase();

    /**
     * 发送图片验证码
     * <p>
     * 将验证码图片写入HTTP响应流，并确保：
     * 1. 正确设置HTTP响应头
     * 2. 禁用浏览器缓存
     * 3. 防止内容类型嗅探
     * </p>
     *
     * @param target   接收目标（在图片验证码场景中通常不使用）
     * @param code     包含验证码图片的对象
     * @param response HTTP响应对象
     * @throws CaptchaAuthenticationException 当验证码为空或发送过程中出现IO异常时抛出
     */
    @Override
    public void send(String target, ImageCaptcha code, HttpServletResponse response) {
        if (code == null || code.getImage() == null) {
            log.error("验证码图片为空");
            throw new CaptchaAuthenticationException("验证码图片不能为空");
        }

        configureResponse(response);

        try (OutputStream out = response.getOutputStream()) {
            if (!ImageIO.write(code.getImage(), IMAGE_FORMAT, out)) {
                String errorMsg = "无法写入验证码图片，不支持的图片格式：" + IMAGE_FORMAT;
                log.error(errorMsg);
                throw new CaptchaAuthenticationException(errorMsg);
            }
            out.flush();
            log.debug("验证码图片发送成功");
        } catch (IOException e) {
            log.error("图片验证码发送失败", e);
            throw new CaptchaAuthenticationException("图片验证码发送失败", e);
        }
    }

    /**
     * 配置HTTP响应头
     * <p>
     * 设置以下响应头：
     * 1. Content-Type: 指定响应内容类型为PNG图片
     * 2. Cache-Control, Pragma, Expires: 确保验证码图片不被缓存
     * 3. X-Content-Type-Options: 防止浏览器进行MIME类型嗅探
     * </p>
     *
     * @param response HTTP响应对象
     */
    private void configureResponse(HttpServletResponse response) {
        // 设置内容类型
        response.setContentType(CONTENT_TYPE);

        // 禁用缓存
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0L);

        // 防止内容类型嗅探
        response.setHeader("X-Content-Type-Options", "nosniff");
    }
}
