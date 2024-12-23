package com.gls.athena.starter.message.config;

import com.gls.athena.starter.message.support.IMessageEventListener;
import com.gls.athena.starter.message.support.KafkaMessageEventListener;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * 消息配置
 *
 * @author george
 */
@Configuration
public class MessageConfig {

    /**
     * 短信 kafka 配置
     */
    @AutoConfiguration
    @ConditionalOnClass(KafkaTemplate.class)
    public static class MessageKafkaConfig {

        /**
         * 消息事件监听
         *
         * @param messageProperties 消息配置
         * @param kafkaTemplate     kafka模板
         * @return 消息事件监听
         */
        @Bean
        public IMessageEventListener messageEventListener(MessageProperties messageProperties, KafkaTemplate<String, Object> kafkaTemplate) {
            return new KafkaMessageEventListener(messageProperties, kafkaTemplate);
        }

    }
}
