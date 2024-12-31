package com.gls.athena.sdk.amap.config;

import com.gls.athena.common.core.constant.BaseProperties;
import com.gls.athena.common.core.constant.IConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 高德配置
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = IConstants.BASE_PROPERTIES_PREFIX + ".amap")
public class AmapProperties extends BaseProperties {
    /**
     * 高德地图服务密钥
     */
    private String key;
    /**
     * 高德地图服务地址
     */
    private String host = "https://restapi.amap.com";
}
