package com.gls.athena.security.servlet.captcha.sms;

import cn.hutool.json.JSONUtil;
import com.gls.athena.common.bean.result.Result;
import com.gls.athena.common.bean.result.ResultStatus;
import com.gls.athena.sdk.message.support.MessageUtil;
import com.gls.athena.security.servlet.captcha.base.ICaptchaSender;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;

/**
 * 短信验证码发送器
 * <p>
 * 负责发送短信验证码并处理发送结果的响应。
 * 使用指定的短信模板发送验证码，并将发送结果以JSON格式返回给客户端。
 * </p>
 *
 * @author george
 * @see ICaptchaSender
 * @see SmsCaptcha
 */
@Slf4j
@RequiredArgsConstructor
public class SmsCaptchaSender implements ICaptchaSender<SmsCaptcha> {

    /**
     * HTTP响应的Content-Type
     */
    private static final String CONTENT_TYPE = "application/json;charset=UTF-8";

    /**
     * 短信模板编号
     * <p>
     * 用于指定发送验证码时使用的短信模板，模板中应包含验证码占位符
     * </p>
     */
    private final String templateCode;

    /**
     * 发送短信验证码
     * <p>
     * 向指定手机号发送验证码，并将发送结果写入HTTP响应。
     * 如果发送过程中发生异常，将返回错误响应。
     * </p>
     *
     * @param target     接收验证码的手机号
     * @param smsCaptcha 待发送的验证码对象
     * @param response   HTTP响应对象，用于返回发送结果
     */
    @Override
    public void send(String target, SmsCaptcha smsCaptcha, HttpServletResponse response) {
        log.warn("请配置真实的短信验证码发送器(SmsCaptchaSender)");
        log.debug("开始向手机[{}]发送验证码", target);

        try {
            doSendSms(target, smsCaptcha);
            writeSuccessResponse(response);
        } catch (Exception e) {
            log.error("向手机[{}]发送验证码失败", target, e);
            writeErrorResponse(response);
        }
    }

    /**
     * 执行短信发送操作
     * <p>
     * 构建短信参数并调用消息服务发送验证码
     * </p>
     *
     * @param target     接收验证码的手机号
     * @param smsCaptcha 待发送的验证码对象
     * @throws RuntimeException 如果短信发送失败
     */
    private void doSendSms(String target, SmsCaptcha smsCaptcha) {
        Map<String, Object> params = Map.of(
                "code", smsCaptcha.getCode(),
                "mobile", target
        );
        MessageUtil.sendSms(target, templateCode, params);
        log.info("向手机[{}]发送验证码成功", target);
    }

    /**
     * 写入成功响应
     * <p>
     * 将发送成功的结果写入HTTP响应
     * </p>
     *
     * @param response HTTP响应对象
     * @throws IOException 如果写入响应失败
     */
    private void writeSuccessResponse(HttpServletResponse response) throws IOException {
        Result<String> result = ResultStatus.SUCCESS.toResult("短信验证码发送成功");
        writeResponse(response, result);
    }

    /**
     * 写入错误响应
     * <p>
     * 将发送失败的结果写入HTTP响应
     * </p>
     *
     * @param response HTTP响应对象
     */
    private void writeErrorResponse(HttpServletResponse response) {
        try {
            Result<String> result = ResultStatus.INTERNAL_SERVER_ERROR.toResult("短信验证码发送失败");
            writeResponse(response, result);
        } catch (IOException e) {
            log.error("写入错误响应失败", e);
        }
    }

    /**
     * 写入HTTP响应
     * <p>
     * 设置响应的Content-Type并将结果写入响应体
     * </p>
     *
     * @param response HTTP响应对象
     * @param result   要写入的结果对象
     * @throws IOException 如果写入响应失败
     */
    private void writeResponse(HttpServletResponse response, Result<String> result) throws IOException {
        response.setContentType(CONTENT_TYPE);
        response.getWriter().write(JSONUtil.toJsonStr(result));
    }
}
