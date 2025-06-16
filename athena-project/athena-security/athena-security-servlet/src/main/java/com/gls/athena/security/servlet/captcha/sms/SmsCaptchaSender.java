package com.gls.athena.security.servlet.captcha.sms;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.gls.athena.common.bean.result.Result;
import com.gls.athena.common.bean.result.ResultStatus;
import com.gls.athena.sdk.message.support.MessageUtil;
import com.gls.athena.security.servlet.captcha.base.ICaptchaSender;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

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
     * <p>该方法实现短信验证码发送功能，包含发送过程和结果处理逻辑。
     * 当发送失败时会捕获异常并记录错误日志。</p>
     *
     * @param target     接收验证码的手机号，格式应为有效的手机号码
     * @param smsCaptcha 待发送的验证码对象，包含验证码内容和有效期等信息
     * @param response   HTTP响应对象，用于向客户端返回操作结果
     * @implNote 当前实现仅为模拟发送，实际使用时需要配置真实的短信发送服务
     */
    @Override
    public void send(String target, SmsCaptcha smsCaptcha, HttpServletResponse response) {
        // 警告日志提示需要配置真实短信服务
        log.warn("请配置真实的短信验证码发送器(SmsCaptchaSender)");

        // 记录发送开始日志
        log.info("开始向手机[{}]发送验证码", target);

        try {
            // 执行实际短信发送操作
            doSendSms(target, smsCaptcha);

            // 发送成功时写入成功响应
            writeSuccessResponse(response);
        } catch (Exception e) {
            // 发送失败时记录错误日志并返回错误响应
            log.error("向手机[{}]发送验证码失败，系统异常", target, e);
            writeErrorResponse(response);
        }
    }

    /**
     * 执行短信发送操作
     *
     * @param mobile     接收验证码的手机号，不能为空
     * @param smsCaptcha 待发送的验证码对象，包含验证码内容，不能为空且验证码内容不能为空
     * @throws IllegalArgumentException 当手机号为空，或验证码对象为空/无效时抛出
     */
    private void doSendSms(String mobile, SmsCaptcha smsCaptcha) {
        // 参数有效性校验
        if (Strings.isBlank(mobile)) {
            throw new IllegalArgumentException("手机号不能为空");
        }
        if (smsCaptcha == null || StrUtil.isBlank(smsCaptcha.getCode())) {
            throw new IllegalArgumentException("验证码对象无效");
        }

        // 构建短信模板参数并发送
        Map<String, Object> params = Map.of(
                "code", smsCaptcha.getCode(),
                "mobile", mobile
        );

        MessageUtil.sendSms(mobile, templateCode, params);
        log.info("向手机[{}]发送验证码成功", mobile);
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
