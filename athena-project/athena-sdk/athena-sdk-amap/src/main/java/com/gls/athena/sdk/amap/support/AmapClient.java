package com.gls.athena.sdk.amap.support;

import com.gls.athena.sdk.amap.feign.ConfigFeign;
import com.gls.athena.sdk.amap.feign.DirectionFeign;
import com.gls.athena.sdk.amap.feign.DistanceFeign;
import com.gls.athena.sdk.amap.feign.GeocodeFeign;
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
     * 配置feign
     */
    private final ConfigFeign config;
    /**
     * 方向feign
     */
    private final DirectionFeign direction;
    /**
     * 距离测量feign
     */
    private final DistanceFeign distance;
    /**
     * 地理编码feign
     */
    private final GeocodeFeign geocode;
}
