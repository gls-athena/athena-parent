package com.gls.athena.sdk.amap.feign;

import com.gls.athena.sdk.amap.domain.DistanceV3Request;
import com.gls.athena.sdk.amap.domain.DistanceV3Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 距离测量
 *
 * @author george
 */
@FeignClient(name = "athena-sdk-amap", contextId = "amap-distance", path = "/distance")
public interface AmapDistanceFeign {

    /**
     * 距离测量
     *
     * @param request 距离测量请求
     * @return 距离测量响应
     */
    @GetMapping
    DistanceV3Response distance(@SpringQueryMap DistanceV3Request request);
}
