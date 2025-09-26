package com.gls.athena.sdk.amap.config;

import com.gls.athena.common.core.constant.BaseProperties;
import com.gls.athena.common.core.constant.IConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * 高德配置
 *
 * @author george
 */
@Data
@Validated
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = IConstants.BASE_PROPERTIES_PREFIX + ".amap")
public class AmapProperties extends BaseProperties {

    /**
     * 高德地图服务密钥
     */
    @NotBlank(message = "高德地图API密钥不能为空")
    private String key;

    /**
     * 高德地图服务地址
     */
    @NotBlank(message = "高德地图服务地址不能为空")
    private String host = "https://restapi.amap.com";

    /**
     * 高德地图服务私钥（用于数字签名，可选）
     */
    private String privateKey;

    /**
     * 连接超时时间（毫秒）
     */
    @Positive(message = "连接超时时间必须大于0")
    private Integer connectTimeout = 5000;

    /**
     * 读取超时时间（毫秒）
     */
    @Positive(message = "读取超时时间必须大于0")
    private Integer readTimeout = 10000;

    /**
     * 重试配置
     */
    @NotNull
    private RetryConfig retry = new RetryConfig();

    /**
     * 重试配置内部类
     */
    @Data
    public static class RetryConfig {
        /**
         * 最大重试次数
         */
        @Positive(message = "最大重试次数必须大于0")
        private Integer maxAttempts = 3;

        /**
         * 初始重试间隔（毫秒）
         */
        @Positive(message = "初始重试间隔必须大于0")
        private Long period = 1000L;

        /**
         * 最大重试间隔（毫秒）
         */
        @Positive(message = "最大重试间隔必须大于0")
        private Long maxPeriod = 5000L;
    }
}
