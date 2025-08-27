package com.gls.athena.sdk.message.kafka;

import com.gls.athena.sdk.message.config.MessageProperties;
import com.gls.athena.sdk.message.domain.MessageDto;
import com.gls.athena.sdk.message.support.IMessageEventListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * Kafka消息事件监听器
 * 职责：专门负责监听消息事件并委托给发送器处理
 *
 * @author george
 */
@Slf4j
public record KafkaMessageEventListener(MessageProperties messageProperties,
                                        KafkaTemplate<String, Object> kafkaTemplate) implements IMessageEventListener {

    @Override
    public void onMessageEvent(MessageDto messageDto) {
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
