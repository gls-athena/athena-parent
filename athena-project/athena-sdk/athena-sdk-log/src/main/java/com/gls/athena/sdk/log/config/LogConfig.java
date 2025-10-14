package com.gls.athena.sdk.log.config;

import cn.hutool.json.JSONUtil;
import com.gls.athena.sdk.log.method.IMethodEventListener;
import com.gls.athena.sdk.log.method.KafkaMethodEventListener;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * 日志配置类
 * 负责配置方法事件监听器
 * 根据环境条件选择合适的监听器实现
 *
 * @author george
 */
@Slf4j
@Configuration
public class LogConfig {

    /**
     * 创建默认的方法事件监听器Bean
     * 当容器中不存在IMethodEventListener类型的Bean时，创建一个默认的监听器
     * 该监听器将方法事件信息以JSON格式输出到日志中
     *
     * @return IMethodEventListener 默认方法事件监听器实例
     */
    @Bean
    @ConditionalOnMissingBean(IMethodEventListener.class)
    public IMethodEventListener defaultMethodEventListener() {
        return event -> log.info("MethodEvent: {}", JSONUtil.toJsonStr(event));
    }

    /**
     * Kafka日志配置内部类
     * 当classpath中存在KafkaTemplate类时，该配置类才会生效
     * 用于创建基于Kafka的方法事件监听器
     */
    @Configuration
    @ConditionalOnClass(KafkaTemplate.class)
    public static class LogKafkaConfig {

        @Resource
        private LogProperties logProperties;

        @Resource
        private KafkaTemplate<String, Object> kafkaTemplate;

        /**
         * 创建基于Kafka的方法事件监听器Bean
         * 当容器中不存在IMethodEventListener类型的Bean时，创建Kafka监听器
         * 该监听器将方法事件信息发送到Kafka消息队列中
         *
         * @return IMethodEventListener Kafka方法事件监听器实例
         */
        @Bean
        @ConditionalOnMissingBean(IMethodEventListener.class)
        public IMethodEventListener kafkaMethodEventListener() {
            return new KafkaMethodEventListener(logProperties, kafkaTemplate);
        }
    }
}