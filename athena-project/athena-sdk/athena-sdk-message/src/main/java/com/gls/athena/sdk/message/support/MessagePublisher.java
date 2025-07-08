package com.gls.athena.sdk.message.support;

import cn.hutool.extra.spring.SpringUtil;
import com.gls.athena.sdk.message.domain.MessageDto;
import com.gls.athena.sdk.message.validator.MessageValidator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 消息发布器
 * 职责：专门负责消息事件的发布
 *
 * @author george
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessagePublisher {

    /**
     * 发布消息事件
     *
     * @param messageDto 消息对象
     */
    public static void publish(MessageDto messageDto) {
        // 消息验证
        if (!MessageValidator.validate(messageDto)) {
            log.error("消息验证失败，无法发布消息事件: {}", messageDto);
            throw new IllegalArgumentException("消息验证失败");
        }

        // 设置发送者为当前应用名称
        if (messageDto.getSender() == null) {
            messageDto.setSender(SpringUtil.getApplicationName());
        }

        log.debug("发布消息事件: {}", messageDto);
        SpringUtil.publishEvent(messageDto);
    }
}
