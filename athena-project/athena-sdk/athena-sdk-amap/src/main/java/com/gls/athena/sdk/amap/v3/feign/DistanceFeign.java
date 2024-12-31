package com.gls.athena.sdk.amap.v3.feign;

import com.gls.athena.sdk.amap.config.IAmapConstants;
import com.gls.athena.sdk.amap.v3.domain.DistanceRequest;
import com.gls.athena.sdk.amap.v3.domain.DistanceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 距离测量
 *
 * @author george
 */
@FeignClient(name = "amap", contextId = "distance-v3", path = "/distance", url = IAmapConstants.URL_V3)
public interface DistanceFeign {

    /**
     * 距离测量 API URL
     *
     * @param request 距离测量请求
     * @return 距离测量响应
     */
    @GetMapping("/")
    DistanceResponse distance(DistanceRequest request);
}
