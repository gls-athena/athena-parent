package com.gls.athena.sdk.amap.v3.feign;

import com.gls.athena.sdk.amap.config.IAmapConstants;
import com.gls.athena.sdk.amap.v3.domain.GeoRequest;
import com.gls.athena.sdk.amap.v3.domain.GeoResponse;
import com.gls.athena.sdk.amap.v3.domain.ReGeoRequest;
import com.gls.athena.sdk.amap.v3.domain.ReGeoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 高德地图逆地理编码feign
 *
 * @author george
 */
@FeignClient(name = "amap", contextId = "geocode-v3", path = "/geocode", url = IAmapConstants.URL_V3)
public interface GeocodeFeign {
    /**
     * 地理编码
     *
     * @param request 地理编码请求
     * @return 地理编码响应
     */
    @GetMapping("/geo")
    GeoResponse geo(@SpringQueryMap GeoRequest request);

    /**
     * 逆地理编码
     *
     * @param request 逆地理编码请求
     * @return 逆地理编码响应
     */
    @GetMapping("/regeo")
    ReGeoResponse regeo(@SpringQueryMap ReGeoRequest request);
}
