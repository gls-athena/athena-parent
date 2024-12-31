package com.gls.athena.sdk.amap.feign;

import com.gls.athena.sdk.amap.domain.GeoV3Request;
import com.gls.athena.sdk.amap.domain.GeoV3Response;
import com.gls.athena.sdk.amap.domain.ReGeoV3Request;
import com.gls.athena.sdk.amap.domain.ReGeoV3Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 高德地图逆地理编码feign
 *
 * @author george
 */
@FeignClient(name = "amap", contextId = "geocode", path = "/geocode")
public interface GeocodeFeign {
    /**
     * 地理编码
     *
     * @param request 地理编码请求
     * @return 地理编码响应
     */
    @GetMapping("/geo")
    GeoV3Response geo(@SpringQueryMap GeoV3Request request);

    /**
     * 逆地理编码
     *
     * @param request 逆地理编码请求
     * @return 逆地理编码响应
     */
    @GetMapping("/regeo")
    ReGeoV3Response regeo(@SpringQueryMap ReGeoV3Request request);
}
