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
@FeignClient(name = "athena-sdk-amap", contextId = "direction", path = "/direction")
public interface DirectionFeign {

    /**
     * 步行路径规划
     *
     * @param request 步行路径规划请求
     * @return 步行路径规划响应
     */
    @GetMapping("/walking")
    WalkingV3Response walking(@SpringQueryMap WalkingV3Request request);

    /**
     * 公交路径规划
     *
     * @param request 公交路径规划请求
     * @return 公交路径规划响应
     */
    @GetMapping("/transit/integrated")
    TransitIntegratedV3Response transitIntegrated(@SpringQueryMap TransitIntegratedV3Request request);

    /**
     * 驾车路径规划
     *
     * @param request 驾车路径规划请求
     * @return 驾车路径规划响应
     */
    @GetMapping("/driving")
    DrivingV3Response driving(@SpringQueryMap DrivingV3Request request);

    /**
     * 骑行路径规划
     *
     * @param request 骑行路径规划请求
     * @return 骑行路径规划响应
     */
    @AmapVersion("v4")
    @GetMapping("/bicycling")
    BicyclingV4Response bicycling(@SpringQueryMap BicyclingV4Request request);

    /**
     * 驾车路线规划 2.0
     *
     * @param request 驾车路线规划 2.0 请求
     * @return 驾车路线规划 2.0 响应
     */
    @AmapVersion("v5")
    @GetMapping("/driving")
    DrivingV5Response driving(@SpringQueryMap DrivingV5Request request);

    /**
     * 步行路线规划 2.0
     *
     * @param request 步行路线规划 2.0 请求
     * @return 步行路线规划 2.0 响应
     */
    @AmapVersion("v5")
    @GetMapping("/walking")
    WalkingV5Response walking(@SpringQueryMap WalkingV5Request request);

    /**
     * 骑行路线规划 2.0
     *
     * @param request 骑行路线规划 2.0 请求
     * @return 骑行路线规划 2.0 响应
     */
    @AmapVersion("v5")
    @GetMapping("/bicycling")
    BicyclingV5Response bicycling(@SpringQueryMap BicyclingV5Request request);

    /**
     * 电动车路线规划 2.0
     *
     * @param request 电动车路线规划 2.0 请求
     * @return 电动车路线规划 2.0 响应
     */
    @AmapVersion("v5")
    @GetMapping("/electrobike")
    ElectrobikeV5Response electrobike(@SpringQueryMap ElectrobikeV5Request request);

    /**
     * 公交路线规划 2.0
     *
     * @param request 公交路线规划 2.0 请求
     * @return 公交路线规划 2.0 响应
     */
    @AmapVersion("v5")
    @GetMapping("/transit/integrated")
    TransitIntegratedV5Response transitIntegrated(@SpringQueryMap TransitIntegratedV5Request request);
}
