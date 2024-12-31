package com.gls.athena.sdk.amap.feign;

import com.gls.athena.sdk.amap.domain.*;
import com.gls.athena.sdk.amap.support.AmapVersion;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 高德地图路径规划feign
 *
 * @author george
 */
@FeignClient(name = "amap", contextId = "direction", path = "/direction")
public interface DirectionFeign {

    /**
     * 步行路径规划 API URL
     *
     * @param request 步行路径规划请求
     * @return 步行路径规划响应
     */
    @GetMapping("/walking")
    WalkingV3Response walking(@SpringQueryMap WalkingV3Request request);

    /**
     * 公交路径规划 API URL
     *
     * @param request 公交路径规划请求
     * @return 公交路径规划响应
     */
    @GetMapping("/transit/integrated")
    TransitIntegratedV3Response transitIntegrated(@SpringQueryMap TransitIntegratedV3Request request);

    /**
     * 驾车路径规划 API URL
     *
     * @param request 驾车路径规划请求
     * @return 驾车路径规划响应
     */
    @GetMapping("/driving")
    DrivingV3Response driving(@SpringQueryMap DrivingV3Request request);

    /**
     * 骑行路径规划 API URL
     *
     * @param request 骑行路径规划请求
     * @return 骑行路径规划响应
     */
    @AmapVersion("v4")
    @GetMapping("/bicycling")
    BicyclingV4Response bicycling(@SpringQueryMap BicyclingV4Request request);
}
