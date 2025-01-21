package com.gls.athena.sdk.amap.domain.v5;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gls.athena.sdk.amap.domain.v3.BaseV3Request;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 高德地图驾车路径规划V5版本请求对象
 * 用于构建驾车路径规划的请求参数，包含起点、终点、途经点等配置信息
 *
 * @author george
 * @see BaseV3Request
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class DrivingV5Request extends BaseV3Request {

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
     * 终点的POI类别
     * 用于辅助更精确的终点判断
     */
    @JsonProperty("destination_type")
    private String destinationType;

    /**
     * 目的地POI的ID
     * 用于精确指定终点POI信息
     */
    @JsonProperty("destination_id")
    private String destinationId;

    /**
     * 驾车路径规划策略
     * 默认值为32（代表单路径规划）
     * 可选值：
     * 10-不走高速；20-距离优先；30-花费最少；31-规避拥堵；32-单路径规划
     */
    private String strategy = "32";

    /**
     * 途经点坐标串
     * 格式：longitude1,latitude1;longitude2,latitude2;...
     * 最多支持16个途经点，多个途经点间用";"分隔
     */
    private String waypoints;

    /**
     * 避让区域
     * 区域避让经纬度串，经纬度间用","分隔，区域间用";"分隔
     */
    private String avoidpolygons;

    /**
     * 车牌号码
     * 用于判断限行区域
     */
    private String plate;

    /**
     * 车辆类型
     * 默认值为0（普通汽车）
     * 可选值：
     * 0-普通汽车；1-纯电动车；2-插电混动车
     */
    private String cartype = "0";

    /**
     * 是否使用轮渡
     * 默认值为0（不使用轮渡）
     * 可选值：
     * 0-不使用轮渡；1-使用轮渡
     */
    private String ferry = "0";

    /**
     * 返回结果控制
     * 通过此参数控制返回结果中包含的内容
     * 可选值：cost,navi,cities等，多个值用","分隔
     */
    @JsonProperty("show_fields")
    private String showFields;
}
