package com.gls.athena.sdk.amap.domain.v4;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 高德地图驾车路径规划V4版本请求参数实体
 * 用于封装请求轨迹纠偏服务的必要参数
 *
 * @author george
 * @since 1.0.0
 */
@Data
public class DrivingV4Request {

    /**
     * 经度坐标
     * 范围：-180 到 180
     */
    @JsonProperty("x")
    private Double lon;

    /**
     * 纬度坐标
     * 范围：-90 到 90
     */
    @JsonProperty("y")
    private Double lat;

    /**
     * 行驶角度
     * 范围：0-360度，正北为0度，顺时针
     */
    @JsonProperty("ag")
    private Double angle;

    /**
     * 采集时间
     * 单位：毫秒级时间戳
     */
    @JsonProperty("tm")
    private Long time;

    /**
     * 行驶速度
     * 单位：千米/小时
     */
    @JsonProperty("sp")
    private Double speed;
}
