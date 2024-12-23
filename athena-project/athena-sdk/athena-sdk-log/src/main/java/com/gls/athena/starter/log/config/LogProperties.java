package com.gls.athena.starter.log.config;

import com.gls.athena.common.core.constant.BaseProperties;
import com.gls.athena.common.core.constant.IConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/**
 * 日志配置
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = IConstants.BASE_PROPERTIES_PREFIX + ".log")
public class LogProperties extends BaseProperties {
    /**
     * kafka配置
     */
    private Kafka kafka = new Kafka();

    /**
     * kafka配置
     */
    @Data
    public static class Kafka implements Serializable {
        /**
         * 主题
         */
        private String topic = "athena-log";
        /**
         * 日志key
         */
        private String methodLogKey = "method-log";
        /**
         * 方法key
         */
        private String methodKey = "method";
    }
}
