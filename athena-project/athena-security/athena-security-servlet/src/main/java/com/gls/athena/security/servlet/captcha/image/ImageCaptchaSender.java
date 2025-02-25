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
     * @param target   目标地址（图形验证码场景下未使用）
     * @param code     验证码图片对象
     * @param response HTTP响应对象
     * @throws CaptchaAuthenticationException 当验证码对象为空或IO异常时
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
     * - 设置内容类型为PNG图片
     * - 配置无缓存策略
     * - 启用安全响应头
     * </p>
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
