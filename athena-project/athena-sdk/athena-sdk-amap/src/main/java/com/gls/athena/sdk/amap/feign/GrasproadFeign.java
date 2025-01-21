package com.gls.athena.sdk.amap.feign;

import com.gls.athena.sdk.amap.domain.v4.DrivingV4Request;
import com.gls.athena.sdk.amap.domain.v4.DrivingV4Response;
import com.gls.athena.sdk.amap.support.AmapVersion;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 高德地图路径规划服务Feign客户端
 * 提供轨迹纠偏等路径规划相关功能
 *
 * @author george
 * @since 1.0.0
 */
@FeignClient(name = "athena-sdk-amap", contextId = "grasproad", path = "/grasproad")
public interface GrasproadFeign {

    /**
     * 轨迹纠偏服务
     * 对原始轨迹点进行纠偏处理，生成准确的行驶路径
     *
     * @param request 轨迹点列表，包含经纬度、时间戳等信息
     * @return 纠偏后的路径信息，包含距离和纠偏后的轨迹点
     */
    @AmapVersion("v4")
    @PostMapping("/driving")
    DrivingV4Response driving(@RequestBody List<DrivingV4Request> request);
}
