package com.gls.athena.sdk.amap.feign;

import com.gls.athena.sdk.amap.domain.CoordinateConvertV3Request;
import com.gls.athena.sdk.amap.domain.CoordinateConvertV3Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 高德地图辅助服务feign
 *
 * @author george
 */
@FeignClient(name = "athena-sdk-amap", contextId = "amap-assistant", path = "/assistant")
public interface AmapAssistantFeign {
    /**
     * 坐标转换
     *
     * @param request 坐标转换请求
     * @return 坐标转换响应
     */
    @GetMapping("/coordinate/convert")
    CoordinateConvertV3Response coordinateConvert(@SpringQueryMap CoordinateConvertV3Request request);
}
