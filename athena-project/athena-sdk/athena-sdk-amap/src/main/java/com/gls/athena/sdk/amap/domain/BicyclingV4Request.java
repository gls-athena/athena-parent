package com.gls.athena.sdk.amap.domain;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 高德地图骑行路径规划API请求参数
 * 用于调用高德地图V4版本骑行路径规划服务
 *
 * @author george
 * @see <a href="https://lbs.amap.com/api/webservice/guide/api/direction">高德地图路径规划API文档</a>
 */
@Data
@Accessors(chain = true)
public class BicyclingV4Request {
    /**
     * 高德地图应用key
     * 用于访问高德地图API的身份认证
     */
    private String key;

    /**
     * 出发点坐标
     * 格式：经度,纬度（小数点后不超过6位）
     * 示例：116.434307,39.90909
     */
    private String origin;

    /**
     * 目的地坐标
     * 格式：经度,纬度（小数点后不超过6位）
     * 示例：116.434307,39.90909
     */
    private String destination;
}
