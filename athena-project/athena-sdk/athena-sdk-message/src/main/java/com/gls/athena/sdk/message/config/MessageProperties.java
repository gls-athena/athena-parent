package com.gls.athena.sdk.message.config;

import com.gls.athena.common.core.constant.BaseProperties;
import com.gls.athena.common.core.constant.IConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 消息配置
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = IConstants.BASE_PROPERTIES_PREFIX + ".message")
public class MessageProperties extends BaseProperties {
    /**
     * kafka配置
     */
    private Kafka kafka = new Kafka();

    /**
     * kafka配置
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class Kafka extends BaseProperties {
        /**
         * 主题
         */
        private String topic = "athena-message";
    }
}
