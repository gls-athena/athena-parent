package com.gls.athena.security.servlet.captcha.image;

import com.gls.athena.security.servlet.captcha.CaptchaAuthenticationException;
import com.gls.athena.security.servlet.captcha.base.ICaptchaSender;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 图形验证码响应发送器
 *
 * @author george
 */
@Slf4j
public class ImageCaptchaSender implements ICaptchaSender<ImageCaptcha> {

    /**
     * 图片格式，使用PNG以支持透明度和无损压缩
     */
    private static final String IMAGE_FORMAT = "PNG";

    /**
     * HTTP响应Content-Type标头值
     */
    private static final String CONTENT_TYPE = "image/" + IMAGE_FORMAT.toLowerCase();

    /**
     * 将验证码图片写入HTTP响应
     *
     * <p>该方法负责将图形验证码输出到HTTP响应流中，并设置正确的响应头信息。
     * 如果验证码对象为空或输出过程中发生异常，将抛出验证码认证异常。</p>
     *
     * @param target   目标地址（图形验证码场景下未使用）
     * @param code     验证码图片对象，包含需要输出的图像数据
     * @param response HTTP响应对象，用于设置响应头和输出图像数据
     * @throws CaptchaAuthenticationException 当验证码对象为空、图像数据为空、
     *                                        不支持的图片格式或发生IO异常时抛出
     */
    @Override
    public void send(String target, ImageCaptcha code, HttpServletResponse response) {
        // 验证码对象及图像数据非空检查
        if (code == null || code.getImage() == null) {
            log.error("验证码图片为空");
            throw new CaptchaAuthenticationException("验证码图片不能为空");
        }

        // 配置HTTP响应头（如Content-Type等）
        configureResponse(response);

        // 尝试将验证码图像写入响应输出流
        try (OutputStream out = response.getOutputStream()) {
            // 使用ImageIO输出图像，格式由IMAGE_FORMAT常量指定
            if (!ImageIO.write(code.getImage(), IMAGE_FORMAT, out)) {
                String errorMsg = "无法写入验证码图片，不支持的图片格式：" + IMAGE_FORMAT;
                log.error(errorMsg);
                throw new CaptchaAuthenticationException(errorMsg);
            }
            log.info("验证码图片发送成功");
        } catch (IOException e) {
            log.error("图片验证码发送失败", e);
            throw new CaptchaAuthenticationException("图片验证码发送失败", e);
        }
    }

    /**
     * 配置HTTP响应头
     * <p>
     * 该方法用于设置HTTP响应头，包含以下配置：
     * - 设置内容类型为PNG图片（由常量CONTENT_TYPE定义）
     * - 禁用客户端和代理服务器的缓存
     * - 设置安全相关的响应头防止内容类型嗅探
     * </p>
     *
     * @param response HttpServletResponse对象，用于设置响应头信息
     * @throws IllegalArgumentException 如果response参数为null
     */
    private void configureResponse(HttpServletResponse response) {
        if (response == null) {
            throw new IllegalArgumentException("HttpServletResponse cannot be null");
        }

        // 设置内容类型头
        response.setContentType(CONTENT_TYPE);

        // 缓存控制配置
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setDateHeader("Expires", 0L);

        // 安全配置：防止浏览器自动推断内容类型
        response.setHeader("X-Content-Type-Options", "nosniff");
    }

}
