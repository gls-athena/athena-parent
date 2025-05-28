package com.gls.athena.starter.aliyun.sms.support;

import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.gls.athena.starter.aliyun.sms.config.AliyunSmsProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 阿里云短信工具类
 *
 * @author george
 */
@Slf4j
@Component
public class AliyunSmsClient {

    @Resource
    private IAcsClient acsClient;

    @Resource
    private AliyunSmsProperties aliyunSmsProperties;

    /**
     * 发送短信
     *
     * @param phone        接收短信的手机号码
     * @param templateCode 短信模板编号，用于指定发送的短信内容模板
     * @param params       短信模板中的参数，用于替换模板中的占位符
     * @throws ClientException 如果短信发送失败，抛出此异常，包含错误信息
     */
    public void sendSms(String phone, String templateCode, String params) throws ClientException {

        // 创建短信发送请求对象，并设置相关参数
        SendSmsRequest request = new SendSmsRequest();
        request.setPhoneNumbers(phone);
        request.setSignName(aliyunSmsProperties.getSignName());
        request.setTemplateCode(templateCode);
        request.setTemplateParam(params);

        // 发送短信并获取响应
        SendSmsResponse response = acsClient.getAcsResponse(request);

        // 检查响应状态，如果发送失败则记录错误日志并抛出异常
        if (!"OK".equals(response.getCode())) {
            log.error("发送短信失败，错误码：{}，错误信息：{}", response.getCode(), response.getMessage());
            throw new ClientException(response.getMessage());
        }
    }

}
