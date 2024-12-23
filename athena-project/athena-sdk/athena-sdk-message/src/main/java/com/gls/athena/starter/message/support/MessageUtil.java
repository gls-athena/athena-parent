package com.gls.athena.starter.message.support;

import cn.hutool.extra.spring.SpringUtil;
import com.gls.athena.starter.message.domain.MessageDto;
import com.gls.athena.starter.message.domain.MessageType;
import lombok.experimental.UtilityClass;

import java.util.Map;

/**
 * 消息发送者
 *
 * @author george
 */
@UtilityClass
public class MessageUtil {

    /**
     * 发送短信
     *
     * @param mobile       手机号
     * @param templateCode 模板编号
     * @param params       参数
     */
    public void sendSms(String mobile, String templateCode, Map<String, Object> params) {
        MessageDto messageDto = new MessageDto();
        messageDto.setType(MessageType.SMS);
        messageDto.setSender(SpringUtil.getApplicationName());
        messageDto.setReceiver(mobile);
        messageDto.setTemplate(templateCode);
        messageDto.setParams(params);
        SpringUtil.publishEvent(messageDto);
    }
}
