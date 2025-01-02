package com.gls.athena.sdk.amap.support;

import com.gls.athena.sdk.amap.feign.*;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * 高德地图API客户端
 * 封装了高德地图各项服务的Feign客户端接口
 *
 * @author george
 */
@Data
@Component
public class AmapClient {
    /**
     * 高德地图辅助功能服务客户端
     * 用于调用辅助类API接口
     */
    private final AmapAssistantFeign assistant;

    /**
     * 高德地图配置服务客户端
     * 用于管理和获取配置信息
     */
    private final AmapConfigFeign config;

    /**
     * 高德地图路径规划服务客户端
     * 提供路径规划、导航等功能
     */
    private final AmapDirectionFeign direction;

    /**
     * 高德地图距离计算服务客户端
     * 用于计算地理坐标间的距离
     */
    private final AmapDistanceFeign distance;

    /**
     * 高德地图地理编码服务客户端
     * 提供地址与经纬度坐标的相互转换
     */
    private final AmapGeocodeFeign geocode;

    /**
     * 高德地图IP定位服务客户端
     * 提供IP地址定位功能
     */
    private final AmapIpFeign ip;

    /**
     * 高德地图天气服务客户端
     * 提供天气查询功能
     */
    private final AmapWeatherFeign weather;
}
