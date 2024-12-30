package com.gls.athena.sdk.amap.feign;

import com.gls.athena.sdk.amap.config.IAmapConstants;
import com.gls.athena.sdk.amap.domain.GeoRequest;
import com.gls.athena.sdk.amap.domain.GeoResponse;
import com.gls.athena.sdk.amap.domain.ReGeoRequest;
import com.gls.athena.sdk.amap.domain.ReGeoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 高德地图逆地理编码feign
 *
 * @author george
 */
@FeignClient(name = "amap", contextId = "geocode", path = "/geocode", url = IAmapConstants.URL)
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
