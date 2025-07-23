package com.gls.athena.security.captcha.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.gls.athena.sdk.message.support.MessageUtil;
import com.gls.athena.security.captcha.config.CaptchaEnums;
import com.gls.athena.security.captcha.config.CaptchaProperties;
import com.gls.athena.security.captcha.config.SmsCaptchaProperties;
import com.gls.athena.security.captcha.domain.Captcha;
import com.gls.athena.security.captcha.repository.CaptchaRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 短信验证码服务实现类
 *
 * @author george
 */
@Slf4j
@Service("smsCaptchaService")
public class SmsCaptchaServiceImpl extends BaseCaptchaService<Captcha> {

    private final SmsCaptchaProperties properties;

    /**
     * 构造方法注入必要的属性
     *
     * @param properties        验证码属性配置
     * @param captchaRepository 验证码仓库
     */
    public SmsCaptchaServiceImpl(CaptchaProperties properties, CaptchaRepository captchaRepository) {
        super(properties, captchaRepository);
        this.properties = properties.getSms();
    }

    /**
     * 判断是否是短信验证码类型请求
     *
     * @param captchaEnums 验证码枚举类型
     * @return 如果是短信验证码类型返回true，否则返回false
     */
    @Override
    protected boolean isCaptchaTypeRequest(CaptchaEnums captchaEnums) {
        return captchaEnums.equals(CaptchaEnums.SMS);
    }

    /**
     * 获取短信验证码发送的URL
     *
     * @return 短信验证码发送的URL
     */
    @Override
    protected String getCaptchaUrl() {
        return properties.getCaptchaSendUrl();
    }

    /**
     * 执行发送验证码逻辑
     *
     * @param mobile   验证码键
     * @param captcha  验证码对象
     * @param response HTTP响应
     */
    @Override
    protected void doSendCaptcha(String mobile, Captcha captcha, HttpServletResponse response) {
        // 参数有效性校验
        if (Strings.isBlank(mobile)) {
            throw new IllegalArgumentException("手机号不能为空");
        }
        if (captcha == null || StrUtil.isBlank(captcha.getCode())) {
            throw new IllegalArgumentException("验证码对象无效");
        }

        // 构建短信模板参数并发送
        Map<String, Object> params = Map.of(
                "code", captcha.getCode(),
                "mobile", mobile
        );

        MessageUtil.sendSms(mobile, properties.getCaptchaTemplateId(), params);
        log.info("向手机[{}]发送验证码成功", mobile);

        // 写入成功响应
        writeSuccessResponse(response);
    }

    /**
     * 向客户端写入成功的HTTP响应
     * <p>
     * 此方法用于构造一个表示操作成功的HTTP响应它将HTTP状态码设置为200（SC_OK），
     * 并将响应内容类型设置为application/json，然后在响应体中写入一个简单的JSON对象，
     * 表示操作状态为成功如果在写入响应过程中发生异常，它将记录一个错误日志
     *
     * @param response 用于写入响应的HttpServletResponse对象
     */
    private void writeSuccessResponse(HttpServletResponse response) {
        // 设置HTTP状态码为200，表示请求已经成功处理
        response.setStatus(HttpServletResponse.SC_OK);
        // 设置响应的内容类型为application/json
        response.setContentType("application/json");
        try {
            // 向响应中写入表示成功的JSON信息
            response.getWriter().write("{\"code\": 200, \"message\": \"验证码发送成功\"}");
        } catch (Exception e) {
            // 记录在写入成功响应时发生的错误
            log.error("写入成功响应失败", e);
        }
    }

    /**
     * 生成短信验证码对象
     *
     * @return 生成的验证码对象
     * @throws IllegalArgumentException 如果验证码长度或过期时间配置不正确抛出此异常
     */
    @Override
    protected Captcha generateCaptcha() {
        // 参数校验
        if (properties.getCaptchaLength() <= 0) {
            throw new IllegalArgumentException("验证码长度必须为正数");
        }
        if (properties.getCaptchaExpire() <= 0) {
            throw new IllegalArgumentException("过期时间必须为正数");
        }

        Captcha captcha = new Captcha();
        // 使用更安全的随机数生成方式
        captcha.setCode(RandomUtil.randomNumbers(properties.getCaptchaLength()));

        // 明确时区处理
        DateTime now = DateUtil.date();
        captcha.setExpireTime(DateUtil.offsetSecond(now, properties.getCaptchaExpire()).toJdkDate());

        return captcha;
    }

    /**
     * 获取短信验证码接口中移动电话参数的名称
     *
     * @return 移动电话参数的名称
     */
    @Override
    protected String getKeyParam() {
        return properties.getMobileParam();
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
     * 获取短信验证码接口中验证码参数的名称
     *
     * @return 验证码参数的名称
     */
    @Override
    protected String getCaptchaCodeParam() {
        return properties.getCaptchaParam();
    }
}
