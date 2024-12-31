package com.gls.athena.sdk.amap.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 驾车路径规划 2.0 请求
 *
 * @author george
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class DrivingV5Request extends BaseV3Request {

    /**
     * 起点经纬度
     */
    private String origin;
    /**
     * 终点经纬度
     */
    private String destination;
    /**
     * 终点的 poi 类别
     */
    @JsonProperty("destination_type")
    private String destinationType;
    /**
     * 目的地 POI ID
     */
    @JsonProperty("destination_id")
    private String destinationId;
    /**
     * 驾车算路策略
     */
    private String strategy = "32";
    /**
     * 途经点
     */
    private String waypoints;
    /**
     * 避让区域
     */
    private String avoidpolygons;
    /**
     * 车牌号码
     */
    private String plate;
    /**
     * 车辆类型
     */
    private String cartype = "0";
    /**
     * 是否使用轮渡
     */
    private String ferry = "0";
    /**
     * 返回结果控制
     */
    @JsonProperty("show_fields")
    private String showFields;
}
