package com.gls.athena.sdk.amap.feign;

import com.gls.athena.sdk.amap.config.AmapFeignConfig;
import com.gls.athena.sdk.amap.domain.v3.CoordinateConvertV3Request;
import com.gls.athena.sdk.amap.domain.v3.CoordinateConvertV3Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 高德地图辅助服务feign
 *
 * @author george
 */
@FeignClient(name = "athena-sdk-amap", contextId = "assistant", path = "/assistant", configuration = AmapFeignConfig.class)
public interface AssistantFeign {
    /**
     * 坐标转换
     *
     * @param request 坐标转换请求
     * @return 坐标转换响应
     */
    @GetMapping("/coordinate/convert")
    CoordinateConvertV3Response coordinateConvert(@SpringQueryMap CoordinateConvertV3Request request);
}
