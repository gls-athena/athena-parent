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
 * 负责发送短信验证码并处理响应结果，使用指定模板发送验证码并返回JSON格式结果
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
     * 用于指定发送验证码的短信模板
     */
    private final String templateCode;

    /**
     * 发送短信验证码
     *
     * @param target     接收验证码的手机号
     * @param smsCaptcha 待发送的验证码对象
     * @param response   HTTP响应对象
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
     *
     * @param target     接收验证码的手机号
     * @param smsCaptcha 待发送的验证码对象
     * @throws RuntimeException 短信发送失败时抛出
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
     *
     * @param response HTTP响应对象
     * @throws IOException 写入响应失败时抛出
     */
    private void writeSuccessResponse(HttpServletResponse response) throws IOException {
        Result<String> result = ResultStatus.SUCCESS.toResult("短信验证码发送成功");
        writeResponse(response, result);
    }

    /**
     * 写入错误响应
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
     *
     * @param response HTTP响应对象
     * @param result   要写入的结果对象
     * @throws IOException 写入响应失败时抛出
     */
    private void writeResponse(HttpServletResponse response, Result<String> result) throws IOException {
        response.setContentType(CONTENT_TYPE);
        response.getWriter().write(JSONUtil.toJsonStr(result));
    }
}
