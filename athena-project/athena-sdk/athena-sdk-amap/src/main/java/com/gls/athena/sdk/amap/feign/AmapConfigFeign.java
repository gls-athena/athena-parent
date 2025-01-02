package com.gls.athena.sdk.amap.feign;

import com.gls.athena.sdk.amap.domain.DistrictV3Request;
import com.gls.athena.sdk.amap.domain.DistrictV3Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 高德地图配置feign
 *
 * @author george
 */
@FeignClient(name = "athena-sdk-amap", contextId = "amap-config", path = "/config")
public interface AmapConfigFeign {

    /**
     * 行政区域查询
     *
     * @param request 行政区域查询请求
     * @return 行政区域查询响应
     */
    @GetMapping("/district")
    DistrictV3Response district(@SpringQueryMap DistrictV3Request request);
}
