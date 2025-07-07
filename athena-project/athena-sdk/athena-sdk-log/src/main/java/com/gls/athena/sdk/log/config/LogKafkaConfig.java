package com.gls.athena.sdk.log.config;

import com.gls.athena.sdk.log.method.IMethodEventListener;
import com.gls.athena.sdk.log.method.KafkaMethodEventListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * Kafka日志配置
 * 职责：专门负责Kafka相关的日志配置
 *
 * @author george
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass(KafkaTemplate.class)
public class LogKafkaConfig {

    /**
     * Kafka方法事件监听器
     * 当存在KafkaTemplate时，提供基于Kafka的事件监听器
     *
     * @param logProperties 日志配置属性
     * @param kafkaTemplate Kafka模板
     * @return IMethodEventListener Kafka方法事件监听器
     */
    @Bean
    public IMethodEventListener kafkaMethodEventListener(LogProperties logProperties,
                                                         KafkaTemplate<String, Object> kafkaTemplate) {
        return new KafkaMethodEventListener(logProperties, kafkaTemplate);
    }
}
