package com.gls.athena.sdk.message.config;

import com.gls.athena.sdk.message.kafka.KafkaMessageEventListener;
import com.gls.athena.sdk.message.support.IMessageEventListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
     * 创建Kafka消息事件监听器Bean
     *
     * @param messageProperties 消息配置属性
     * @param kafkaTemplate     Kafka模板对象
     * @return IMessageEventListener Kafka消息事件监听器实例
     */
    @Bean
    @ConditionalOnClass(KafkaTemplate.class)
    @ConditionalOnProperty(prefix = "athena.message.kafka", name = "enabled", havingValue = "true", matchIfMissing = true)
    public IMessageEventListener messageEventListener(MessageProperties messageProperties, KafkaTemplate<String, Object> kafkaTemplate) {
        return new KafkaMessageEventListener(messageProperties, kafkaTemplate);
    }

}
