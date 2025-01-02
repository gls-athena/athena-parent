package com.gls.athena.sdk.amap.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 高德地图步行路径规划V5版本请求对象
 * 用于构建步行路径规划的API请求参数
 *
 * @author george
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class WalkingV5Request extends BaseV3Request {
    /**
     * 起点经纬度
     * 格式：longitude,latitude
     * 经度在前，纬度在后，经纬度间以","分隔
     */
    private String origin;

    /**
     * 终点经纬度
     * 格式：longitude,latitude
     * 经度在前，纬度在后，经纬度间以","分隔
     */
    private String destination;

    /**
     * 起点POI的ID
     * 当起点为POI时，可通过POI ID来唯一标识一个POI点位
     */
    @JsonProperty("origin_id")
    private String originId;

    /**
     * 终点POI的ID
     * 当终点为POI时，可通过POI ID来唯一标识一个POI点位
     */
    @JsonProperty("destination_id")
    private String destinationId;

    /**
     * 返回方案数量
     * 若不填写，则返回一条默认方案
     * 若填写，则返回指定数量的方案，最多支持返回3条方案
     */
    @JsonProperty("alternative_route")
    private String alternativeRoute;

    /**
     * 返回结果控制
     * 支持以下字段：
     * cost - 花费信息
     * navi - 导航信息
     * cities - 途径城市信息
     * 多个字段间使用"|"分隔
     */
    @JsonProperty("show_fields")
    private String showFields;

    /**
     * 是否需要室内算路
     * 可选值：
     * 0：不需要（默认）
     * 1：需要
     */
    private String isindoor;
}
