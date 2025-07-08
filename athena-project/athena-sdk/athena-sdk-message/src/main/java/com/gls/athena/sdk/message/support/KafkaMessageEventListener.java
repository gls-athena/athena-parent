package com.gls.athena.sdk.message.support;

import com.gls.athena.sdk.message.domain.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Kafka消息事件监听器
 * 职责：专门负责监听消息事件并委托给发送器处理
 *
 * @author george
 */
@Slf4j
@RequiredArgsConstructor
public class KafkaMessageEventListener implements IMessageEventListener {

    private final KafkaMessageSender kafkaMessageSender;

    @Override
    public void onMessageEvent(MessageDto messageDto) {
        log.debug("接收到消息事件: {}", messageDto);
        kafkaMessageSender.send(messageDto);
    }
}
