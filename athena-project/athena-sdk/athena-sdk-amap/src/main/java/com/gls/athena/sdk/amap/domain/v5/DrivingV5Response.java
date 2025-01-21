package com.gls.athena.sdk.amap.domain.v5;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gls.athena.sdk.amap.domain.v3.BaseV3Response;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 高德地图驾车路径规划V5版本响应实体
 * 用于封装驾车路径规划API的返回结果
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DrivingV5Response extends BaseV3Response {
    /**
     * 返回的路径规划方案总数
     * 表示本次路径规划返回的方案数量
     */
    private String count;

    /**
     * 路径规划方案详细信息
     * 包含起终点信息、路径方案等详细数据
     */
    private Route route;

    /**
     * 路径规划方案详细信息类
     * 包含起点、终点、路径方案等完整信息
     */
    @Data
    public static class Route {
        /**
         * 起点坐标
         * 格式：经度,纬度
         */
        private String origin;

        /**
         * 终点坐标
         * 格式：经度,纬度
         */
        private String destination;

        /**
         * 预计出租车费用
         * 单位：元（人民币）
         */
        @JsonProperty("taxi_cost")
        private String taxiCost;

        /**
         * 路径规划方案列表
         * 包含多个可选的路径方案
         */
        private List<Path> paths;
    }

    /**
     * 具体路径方案信息类
     * 包含距离、限行状态、路段信息等详细数据
     */
    @Data
    public static class Path {
        /**
         * 方案总距离
         * 单位：米
         */
        private String distance;

        /**
         * 路线限行状态
         * 0：无限行或已规避限行
         * 1：包含限行路段
         */
        private String restriction;

        /**
         * 路线分段信息列表
         * 将完整路径按照路段切分的详细信息
         */
        private List<Step> steps;

        /**
         * 方案成本信息
         * 包含时间成本和费用成本等信息
         */
        private Cost cost;
    }

    /**
     * 路段信息类
     * 描述单个路段的详细信息
     */
    @Data
    public static class Step {
        /**
         * 行驶指示信息
         * 描述该路段的行驶指示说明
         */
        private String instruction;

        /**
         * 进入道路的方向
         * 如：东、南、西、北等
         */
        private String orientation;

        /**
         * 道路名称
         * 当前路段所在道路的名称
         */
        @JsonProperty("road_name")
        private String roadName;

        /**
         * 路段距离
         * 单位：米
         */
        @JsonProperty("step_distance")
        private String stepDistance;

        /**
         * 路段成本信息
         * 包含时间、收费等信息
         */
        private Cost cost;

        /**
         * 路况信息列表
         * 包含路段的实时路况信息
         */
        private List<Tmc> tmcs;

        /**
         * 导航指令信息
         * 包含主要和辅助导航动作
         */
        private Navi navi;

        /**
         * 途经城市信息列表
         * 记录该路段途经的城市信息
         */
        private List<City> cities;

        /**
         * 路段坐标点串
         * 格式：经度,纬度;经度,纬度;...
         */
        private String polyline;
    }

    /**
     * 成本信息类
     * 描述路径或路段的各项成本
     */
    @Data
    public static class Cost {
        /**
         * 行驶耗时
         * 单位：秒
         */
        private String duration;

        /**
         * 道路通行费
         * 单位：元（人民币）
         */
        private String tolls;

        /**
         * 收费路段长度
         * 单位：米
         */
        @JsonProperty("toll_distance")
        private String tollDistance;

        /**
         * 主要收费道路名称
         * 记录途经的主要收费道路
         */
        @JsonProperty("toll_road")
        private String tollRoad;

        /**
         * 红绿灯数量
         * 单位：个
         */
        @JsonProperty("traffic_lights")
        private String trafficLights;
    }

    /**
     * 路况信息类
     * 描述道路的实时交通状况
     */
    @Data
    public static class Tmc {
        /**
         * 路况状态
         * 可能值：未知、畅通、缓行、拥堵、严重拥堵
         */
        @JsonProperty("tmc_status")
        private String tmcStatus;

        /**
         * 相同路况的距离
         * 单位：米
         */
        @JsonProperty("tmc_distance")
        private String tmcDistance;

        /**
         * 路况对应的坐标点串
         * 格式：经度,纬度,经度,纬度,...
         */
        @JsonProperty("tmc_polyline")
        private String tmcPolyline;
    }

    /**
     * 导航指令类
     * 包含导航过程中的转向等指令信息
     */
    @Data
    public static class Navi {
        /**
         * 主导航动作
         * 如：左转、右转、直行等
         */
        private String action;

        /**
         * 辅助导航动作
         * 对主导航动作的补充说明
         */
        @JsonProperty("assistant_action")
        private String assistantAction;
    }

    /**
     * 城市信息类
     * 描述途经城市的详细信息
     */
    @Data
    public static class City {
        /**
         * 区域编码
         * 高德地图行政区划编码
         */
        private String adcode;

        /**
         * 城市编码
         * 高德地图城市编码
         */
        private String citycode;

        /**
         * 城市名称
         * 途经城市的中文名称
         */
        private String city;

        /**
         * 区县信息列表
         * 途经的区县级行政区划信息
         */
        private List<District> districts;
    }

    /**
     * 区县信息类
     * 描述途经区县的详细信息
     */
    @Data
    public static class District {
        /**
         * 区县名称
         * 途经区县的中文名称
         */
        private String name;

        /**
         * 区县编码
         * 高德地图区县级行政区划编码
         */
        private String adcode;
    }
}
