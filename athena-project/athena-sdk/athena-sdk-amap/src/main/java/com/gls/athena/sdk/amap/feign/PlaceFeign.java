package com.gls.athena.sdk.amap.feign;

import com.gls.athena.sdk.amap.domain.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 高德地图POI(兴趣点)检索服务接口
 * <p>
 * 提供以下搜索能力:
 * - 关键字搜索：根据关键字检索POI信息
 * - 周边搜索：指定坐标点范围内的POI检索
 * - 多边形搜索：在给定多边形区域内检索POI
 *
 * @author george
 */
@FeignClient(name = "athena-sdk-amap", contextId = "place", path = "/place")
public interface PlaceFeign {

    /**
     * 关键字搜索POI
     *
     * @param request 检索请求参数，包含关键字、城市等信息
     * @return 包含POI列表的检索结果
     */
    @GetMapping("/text")
    TextV3Response text(@SpringQueryMap TextV3Request request);

    /**
     * 周边搜索POI
     *
     * @param request 检索请求参数，包含中心点坐标、搜索半径等
     * @return 包含POI列表的检索结果
     */
    @GetMapping("/around")
    AroundV3Response around(@SpringQueryMap AroundV3Request request);

    /**
     * 多边形区域内搜索POI
     *
     * @param request 检索请求参数，包含多边形顶点坐标等
     * @return 包含POI列表的检索结果
     */
    @GetMapping("/polygon")
    PolygonV3Response polygon(@SpringQueryMap PolygonV3Request request);
}
