package com.gls.athena.sdk.message.config;

import com.gls.athena.sdk.message.support.IMessageEventListener;
import com.gls.athena.sdk.message.support.KafkaMessageEventListener;
import com.gls.athena.sdk.message.support.KafkaMessageSender;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
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
     * Kafka消息配置
     * 职责：专门负责Kafka相关的Bean配置
     */
    @AutoConfiguration
    @ConditionalOnClass(KafkaTemplate.class)
    public static class MessageKafkaConfig {

        /**
         * Kafka消息发送器
         *
         * @param messageProperties 消息配置
         * @param kafkaTemplate     kafka模板
         * @return Kafka消息发送器
         */
        @Bean
        public KafkaMessageSender kafkaMessageSender(MessageProperties messageProperties, KafkaTemplate<String, Object> kafkaTemplate) {
            return new KafkaMessageSender(messageProperties, kafkaTemplate);
        }

        /**
         * 消息事件监听器
         *
         * @param kafkaMessageSender Kafka消息发送器
         * @return 消息事件监听器
         */
        @Bean
        public IMessageEventListener messageEventListener(KafkaMessageSender kafkaMessageSender) {
            return new KafkaMessageEventListener(kafkaMessageSender);
        }
    }
}
