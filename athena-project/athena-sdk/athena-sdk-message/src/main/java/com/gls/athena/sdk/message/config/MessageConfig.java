package com.gls.athena.sdk.message.config;

import com.gls.athena.sdk.message.kafka.KafkaMessageEventListener;
import com.gls.athena.sdk.message.support.IMessageEventListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * 消息配置
 * 职责：统一管理消息相关的配置
 *
 * @author george
 */
@Configuration
public class MessageConfig {

    /**
     * 创建消息事件监听器Bean
     *
     * @param messageProperties 消息配置属性，包含Kafka相关的配置信息
     * @param kafkaTemplate     Kafka消息模板，用于发送和接收Kafka消息
     * @return 返回配置好的Kafka消息事件监听器实例
     */
    @Bean
    public IMessageEventListener messageEventListener(MessageProperties messageProperties, KafkaTemplate<String, Object> kafkaTemplate) {
        return new KafkaMessageEventListener(messageProperties, kafkaTemplate);
    }

}
