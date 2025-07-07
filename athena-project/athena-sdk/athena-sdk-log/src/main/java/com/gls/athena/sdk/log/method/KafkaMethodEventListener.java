package com.gls.athena.sdk.log.method;

import com.gls.athena.sdk.log.config.LogProperties;
import com.gls.athena.sdk.log.domain.MethodDto;
import com.gls.athena.sdk.log.domain.MethodLogDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * 方法日志消费者，负责将方法事件通过Kafka发送到指定主题
 *
 * @author george
 */
@Slf4j
@RequiredArgsConstructor
public class KafkaMethodEventListener implements IMethodEventListener {
    /**
     * 日志配置，包含Kafka主题和key配置信息
     */
    private final LogProperties logProperties;
    /**
     * Kafka消息发送模板，用于实际的消息投递操作
     */
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * 处理方法事件的核心方法，将方法日志发送到Kafka
     *
     * @param methodDto 包含方法执行信息的数据传输对象，包含方法名、参数、执行时间等信息
     */
    @Override
    public void onMethodEvent(MethodDto methodDto) {
        // 检查Kafka发送是否启用
        if (!logProperties.getKafka().isEnabled()) {
            log.debug("Kafka日志发送已禁用，跳过发送");
            return;
        }

        try {
            // 根据方法类型生成对应的Kafka消息key
            String key = getKafkaKey(methodDto);
            String topic = logProperties.getKafka().getTopic();

            // 记录发送日志并执行消息发送
            log.debug("发送方法日志到Kafka: topic={}, key={}", topic, key);
            kafkaTemplate.send(topic, key, methodDto);

        } catch (Exception e) {
            // Kafka发送失败不应影响主业务流程
            log.error("Kafka方法日志发送失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 根据方法类型生成Kafka消息的key
     *
     * @param methodDto 方法事件对象，用于判断具体类型
     * @return 配置文件中对应的key值：MethodLogDto类型返回方法日志key，其他类型返回通用方法key
     */
    private String getKafkaKey(MethodDto methodDto) {
        // 根据具体类型选择不同的消息key配置
        if (methodDto instanceof MethodLogDto) {
            return logProperties.getKafka().getMethodLogKey();
        }
        return logProperties.getKafka().getMethodKey();
    }

}
