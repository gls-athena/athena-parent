package com.gls.athena.sdk.message.support;

import com.gls.athena.sdk.message.config.MessageProperties;
import com.gls.athena.sdk.message.domain.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * Kafka消息发送器
 * 职责：专门负责通过Kafka发送消息
 *
 * @author george
 */
@Slf4j
@RequiredArgsConstructor
public class KafkaMessageSender {

    private final MessageProperties messageProperties;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * 发送消息到Kafka
     *
     * @param messageDto 消息对象
     */
    public void send(MessageDto messageDto) {
        if (messageDto == null) {
            log.warn("消息对象为空，无法发送到Kafka");
            return;
        }

        try {
            String key = messageDto.getType().getCode();
            String topic = messageProperties.getKafka().getTopic();

            log.info("发送消息到Kafka - Topic: {}, Key: {}, Message: {}", topic, key, messageDto);
            kafkaTemplate.send(topic, key, messageDto);

        } catch (Exception e) {
            log.error("发送消息到Kafka失败: {}", messageDto, e);
            throw new RuntimeException("消息发送失败", e);
        }
    }
}
