package com.gls.athena.sdk.amap.v4.feign;

import com.gls.athena.sdk.amap.config.IAmapConstants;
import com.gls.athena.sdk.amap.v4.domain.BicyclingRequest;
import com.gls.athena.sdk.amap.v4.domain.BicyclingResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 高德地图路径规划feign
 *
 * @author george
 */
@FeignClient(name = "amap", contextId = "direction-v4", path = "/direction", url = IAmapConstants.URL_V4)
public interface DirectionFeign {
    /**
     * 骑行路径规划 API URL
     *
     * @param request 骑行路径规划请求
     * @return 骑行路径规划响应
     */
    @GetMapping("/bicycling")
    BicyclingResponse bicycling(@SpringQueryMap BicyclingRequest request);
}
