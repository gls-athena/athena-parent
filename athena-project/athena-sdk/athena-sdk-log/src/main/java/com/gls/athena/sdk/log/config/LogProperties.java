package com.gls.athena.sdk.log.config;

import com.gls.athena.common.core.constant.BaseProperties;
import com.gls.athena.common.core.constant.IConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 日志配置属性
 * 职责：专门负责日志相关的配置管理
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = IConstants.BASE_PROPERTIES_PREFIX + ".log")
public class LogProperties extends BaseProperties {

    /**
     * 性能监控配置
     */
    private Performance performance = new Performance();

    /**
     * Kafka配置
     */
    private Kafka kafka = new Kafka();

    /**
     * 性能监控配置
     */
    @Data
    public static class Performance {
        /**
         * 是否启用性能监控
         */
        private boolean enabled = true;

        /**
         * 超时阈值（毫秒）
         */
        private long timeoutThreshold = 5000;

        /**
         * 是否记录方法参数
         */
        private boolean logArgs = true;

        /**
         * 是否记录方法返回值
         */
        private boolean logResult = true;
    }

    /**
     * Kafka配置
     */
    @Data
    public static class Kafka {
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
        /**
         * 是否启用Kafka发送
         */
        private boolean enabled = true;
    }
}
