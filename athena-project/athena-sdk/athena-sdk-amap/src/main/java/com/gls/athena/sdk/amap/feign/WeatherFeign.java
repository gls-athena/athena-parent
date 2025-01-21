package com.gls.athena.sdk.amap.feign;

import com.gls.athena.sdk.amap.domain.v3.WeatherV3Request;
import com.gls.athena.sdk.amap.domain.v3.WeatherV3Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 高德地图天气服务feign
 *
 * @author george
 */
@FeignClient(name = "athena-sdk-amap", contextId = "weather", path = "/weather")
public interface WeatherFeign {
    /**
     * 天气查询
     *
     * @param request 天气查询请求
     * @return 天气查询响应
     */
    @GetMapping("/weatherInfo")
    WeatherV3Response weatherInfo(@SpringQueryMap WeatherV3Request request);
}
