package com.gls.athena.sdk.amap.feign;

import com.gls.athena.sdk.amap.domain.IpV3Request;
import com.gls.athena.sdk.amap.domain.IpV3Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 高德地图ip查询feign
 *
 * @author george
 */
@FeignClient(name = "amap", contextId = "ip", path = "/ip")
public interface AmapIpFeign {

    /**
     * IP 定位
     *
     * @param request IP 定位请求
     * @return IP 定位响应
     */
    @GetMapping
    IpV3Response ip(@SpringQueryMap IpV3Request request);
}
