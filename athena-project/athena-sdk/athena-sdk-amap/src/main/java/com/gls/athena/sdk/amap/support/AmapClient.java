package com.gls.athena.sdk.amap.support;

import com.gls.athena.sdk.amap.feign.*;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * 高德客户端
 *
 * @author george
 */
@Data
@Component
public class AmapClient {
    /**
     * 辅助feign
     */
    private final AmapAssistantFeign assistant;
    /**
     * 配置feign
     */
    private final AmapConfigFeign config;
    /**
     * 方向feign
     */
    private final AmapDirectionFeign direction;
    /**
     * 距离测量feign
     */
    private final AmapDistanceFeign distance;
    /**
     * 地理编码feign
     */
    private final AmapGeocodeFeign geocode;
    /**
     * ip feign
     */
    private final AmapIpFeign ip;
}
