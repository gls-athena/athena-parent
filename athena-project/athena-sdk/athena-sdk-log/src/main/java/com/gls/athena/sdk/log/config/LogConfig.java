package com.gls.athena.sdk.log.config;

import cn.hutool.json.JSONUtil;
import com.gls.athena.sdk.log.method.IMethodEventListener;
import com.gls.athena.sdk.log.method.KafkaMethodEventListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * 日志配置
 *
 * @author george
 */
@Slf4j
@AutoConfiguration
public class LogConfig {

    /**
     * 方法事件监听器
     *
     * @return IMethodEventListener 方法事件监听器
     */
    @Bean
    @ConditionalOnMissingBean(IMethodEventListener.class)
    public IMethodEventListener methodEventListener() {
        return event -> log.info("MethodEvent: {}", JSONUtil.toJsonStr(event));
    }

    /**
     * 日志 kafka 配置
     *
     * @author george
     */
    @AutoConfiguration
    @ConditionalOnClass(KafkaTemplate.class)
    public static class LogKafkaConfig {

        /**
         * kafka方法事件监听器
         *
         * @param logProperties 日志配置
         * @param kafkaTemplate kafka模板
         * @return IMethodEventListener kafka方法事件监听器
         */
        @Bean
        public IMethodEventListener kafkaMethodEventListener(LogProperties logProperties, KafkaTemplate<String, Object> kafkaTemplate) {
            return new KafkaMethodEventListener(logProperties, kafkaTemplate);
        }
    }
}
