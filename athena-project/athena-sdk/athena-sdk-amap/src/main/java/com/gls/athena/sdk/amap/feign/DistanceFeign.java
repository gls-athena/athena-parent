package com.gls.athena.sdk.amap.feign;

import com.gls.athena.sdk.amap.config.AmapFeignConfig;
import com.gls.athena.sdk.amap.domain.v3.DistanceV3Request;
import com.gls.athena.sdk.amap.domain.v3.DistanceV3Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 距离测量
 *
 * @author george
 */
@FeignClient(name = "athena-sdk-amap", contextId = "distance", path = "/distance", configuration = AmapFeignConfig.class)
public interface DistanceFeign {

    /**
     * 距离测量
     *
     * @param request 距离测量请求
     * @return 距离测量响应
     */
    @GetMapping
    DistanceV3Response distance(@SpringQueryMap DistanceV3Request request);
}
