package com.gls.athena.sdk.amap.v3.feign;

import com.gls.athena.sdk.amap.config.IAmapConstants;
import com.gls.athena.sdk.amap.v3.domain.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 高德地图路径规划feign
 *
 * @author george
 */
@FeignClient(name = "amap", contextId = "direction-v3", path = "/direction", url = IAmapConstants.URL_V3)
public interface DirectionFeign {

    /**
     * 步行路径规划 API URL
     *
     * @param request 步行路径规划请求
     * @return 步行路径规划响应
     */
    @GetMapping("/walking")
    WalkingResponse walking(@SpringQueryMap WalkingRequest request);

    /**
     * 公交路径规划 API URL
     *
     * @param request 公交路径规划请求
     * @return 公交路径规划响应
     */
    @GetMapping("/transit/integrated")
    TransitIntegratedResponse transitIntegrated(@SpringQueryMap TransitIntegratedRequest request);

    /**
     * 驾车路径规划 API URL
     *
     * @param request 驾车路径规划请求
     * @return 驾车路径规划响应
     */
    @GetMapping("/driving")
    DrivingResponse driving(@SpringQueryMap DrivingRequest request);
}
