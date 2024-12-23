package com.gls.athena.starter.message.support;

import com.gls.athena.starter.message.config.MessageProperties;
import com.gls.athena.starter.message.domain.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * kafka消息事件监听器
 *
 * @author george
 */
@Slf4j
@RequiredArgsConstructor
public class KafkaMessageEventListener implements IMessageEventListener {
    /**
     * 短信配置
     */
    private final MessageProperties messageProperties;
    /**
     * kafka模板
     */
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void onMessageEvent(MessageDto messageDto) {
        log.info("发送消息: {}", messageDto);
        String key = messageDto.getType().name();
        String topic = messageProperties.getKafka().getTopic();
        kafkaTemplate.send(topic, key, messageDto);
    }
}
