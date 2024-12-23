package com.gls.athena.sdk.log.method;

import com.gls.athena.sdk.log.config.LogProperties;
import com.gls.athena.sdk.log.domain.MethodDto;
import com.gls.athena.sdk.log.domain.MethodLogDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * 方法日志消费者
 *
 * @author george
 */
@Slf4j
@RequiredArgsConstructor
public class KafkaMethodEventListener implements IMethodEventListener {
    /**
     * 日志配置
     */
    private final LogProperties logProperties;
    /**
     * kafka模板
     */
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * 方法事件监听
     *
     * @param methodDto 方法事件
     */
    @Override
    public void onMethodEvent(MethodDto methodDto) {
        String key = getKafkaKey(methodDto);
        log.info("发送方法日志: {}", methodDto);
        kafkaTemplate.send(logProperties.getKafka().getTopic(), key, methodDto);
    }

    /**
     * 获取kafka key
     *
     * @param methodDto 方法事件
     * @return kafka key
     */
    private String getKafkaKey(MethodDto methodDto) {
        if (methodDto instanceof MethodLogDto) {
            return logProperties.getKafka().getMethodLogKey();
        }
        return logProperties.getKafka().getMethodKey();
    }

}
