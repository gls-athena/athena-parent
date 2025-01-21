package com.gls.athena.sdk.amap.support;

import com.gls.athena.sdk.amap.feign.*;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * 高德地图API统一客户端
 * 整合封装高德地图各项服务的Feign客户端，提供统一的服务访问入口
 *
 * @author george
 */
@Data
@Component
public class AmapClient {
    /**
     * 辅助功能服务 - 提供各类辅助性API调用
     */
    private final AssistantFeign assistant;

    /**
     * 配置管理服务 - 处理系统配置参数
     */
    private final ConfigFeign config;

    /**
     * 路径规划服务 - 提供路线规划及导航功能
     */
    private final DirectionFeign direction;

    /**
     * 距离测量服务 - 计算地理位置间距离
     */
    private final DistanceFeign distance;

    /**
     * 地理编码服务 - 地址与坐标互转
     */
    private final GeocodeFeign geocode;

    /**
     * 道路抓取服务 - 用于轨迹纠偏等功能
     */
    private final GrasproadFeign grasproad;

    /**
     * IP定位服务 - IP地址定位查询
     */
    private final IpFeign ip;

    /**
     * 天气查询服务 - 获取天气信息
     */
    private final WeatherFeign weather;
    /**
     * 地点服务 - 提供地点搜索、周边搜索等功能
     */
    private final PlaceFeign place;
}
