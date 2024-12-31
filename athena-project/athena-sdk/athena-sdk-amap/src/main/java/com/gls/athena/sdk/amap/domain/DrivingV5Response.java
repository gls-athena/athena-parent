package com.gls.athena.sdk.amap.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 驾车路径规划 2.0 响应
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DrivingV5Response extends BaseV3Response {
    /**
     * 路径规划方案总数
     */
    private String count;
    /**
     * 返回的规划方案列表
     */
    private Route route;

    /**
     * 路径规划方案
     */
    @Data
    public static class Route implements Serializable {
        /**
         * 起点经纬度
         */
        private String origin;
        /**
         * 终点经纬度
         */
        private String destination;
        /**
         * 预计出租车费用，单位：元
         */
        @JsonProperty("taxi_cost")
        private String taxiCost;
        /**
         * 算路方案详情
         */
        private List<Path> paths;

    }

    /**
     * 算路方案详情
     */
    @Data
    public static class Path implements Serializable {
        /**
         * 方案距离，单位：米
         */
        private String distance;
        /**
         * 0 代表限行已规避或未限行，即该路线没有限行路段
         * 1 代表限行无法规避，即该线路有限行路段
         */
        private String restriction;
        /**
         * 路线分段
         */
        private List<Step> steps;
        /**
         * 设置后可返回方案所需时间及费用成本
         */
        private Cost cost;

    }

    /**
     * 路线分段
     */
    @Data
    public static class Step implements Serializable {
        /**
         * 行驶指示
         */
        private String instruction;
        /**
         * 进入道路方向
         */
        private String orientation;
        /**
         * 分段道路名称
         */
        @JsonProperty("road_name")
        private String roadName;
        /**
         * 分段距离信息
         */
        @JsonProperty("step_distance")
        private String stepDistance;
        /**
         * 设置后可返回方案所需时间及费用成本
         */
        private Cost cost;
        /**
         * 设置后可返回分段路况详情
         */
        private List<Tmc> tmcs;
        /**
         * 设置后可返回详细导航动作指令
         */
        private Navi navi;
        /**
         * 设置后可返回分段途径城市信息
         */
        private List<City> cities;
        /**
         * 设置后可返回分路段坐标点串，两点间用“;”分隔
         */
        private String polyline;
    }

    /**
     * 设置后可返回方案所需时间及费用成本
     */
    @Data
    public static class Cost implements Serializable {
        /**
         * 线路耗时，分段 step 中的耗时
         */
        private String duration;
        /**
         * 此路线道路收费，单位：元，包括分段信息
         */
        private String tolls;
        /**
         * 收费路段里程，单位：米，包括分段信息
         */
        @JsonProperty("toll_distance")
        private String tollDistance;
        /**
         * 主要收费道路
         */
        @JsonProperty("toll_road")
        private String tollRoad;
        /**
         * 方案中红绿灯个数，单位：个
         */
        @JsonProperty("traffic_lights")
        private String trafficLights;
    }

    /**
     * 设置后可返回分段路况详情
     */
    @Data
    public static class Tmc implements Serializable {
        /**
         * 路况信息，包括：未知、畅通、缓行、拥堵、严重拥堵
         */
        @JsonProperty("tmc_status")
        private String tmcStatus;
        /**
         * 从当前坐标点开始 step 中路况相同的距离
         */
        @JsonProperty("tmc_distance")
        private String tmcDistance;
        /**
         * 此段路况涉及的道路坐标点串，点间用","分隔
         */
        @JsonProperty("tmc_polyline")
        private String tmcPolyline;
    }

    /**
     * 设置后可返回详细导航动作指令
     */
    @Data
    public static class Navi implements Serializable {
        /**
         * 导航主要动作指令
         */
        private String action;
        /**
         * 导航辅助动作指令
         */
        @JsonProperty("assistant_action")
        private String assistantAction;
    }

    /**
     * 设置后可返回分段途径城市信息
     */
    @Data
    public static class City implements Serializable {
        /**
         * 途径区域编码
         */
        private String adcode;
        /**
         * 途径城市编码
         */
        private String citycode;
        /**
         * 途径城市名称
         */
        private String city;
        /**
         * 途径区县信息
         */
        private List<District> districts;
    }

    /**
     * 途径区县信息
     */
    @Data
    public static class District implements Serializable {
        /**
         * 途径区县名称
         */
        private String name;
        /**
         * 途径区县 adcode
         */
        private String adcode;

    }

}
