package com.gls.athena.sdk.amap.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 高德地图驾车路径规划 V3 版本响应实体
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DrivingV3Response extends BaseV3Response {
    /**
     * 返回的驾车路径规划方案数量
     */
    private String count;
    /**
     * 驾车路径规划详细信息
     */
    private Route route;

    /**
     * 驾车路径规划的路线信息
     */
    @Data
    public static class Route {
        /**
         * 起点坐标，格式：x,y（经度,纬度）
         */
        private String origin;
        /**
         * 终点坐标，格式：x,y（经度,纬度）
         */
        private String destination;
        /**
         * 预估的出租车费用，单位：元
         */
        @JsonProperty("taxi_cost")
        private String taxiCost;
        /**
         * 可选的驾车导航方案列表
         */
        private List<Path> paths;
    }

    /**
     * 单个驾车导航方案的详细信息
     */
    @Data
    public static class Path {
        /**
         * 方案总行驶距离，单位：米
         */
        private String distance;
        /**
         * 方案预计总耗时，单位：秒
         */
        private String duration;
        /**
         * 导航策略（不同策略可能对应不同路线）
         */
        private String strategy;
        /**
         * 此导航方案的总道路收费，单位：元
         */
        private String tolls;
        /**
         * 限行信息说明
         */
        private String restriction;
        /**
         * 此方案需要经过的红绿灯数量
         */
        @JsonProperty("traffic_lights")
        private String trafficLights;
        /**
         * 收费路段的总距离，单位：米
         */
        @JsonProperty("toll_distance")
        private String tollDistance;
        /**
         * 导航路段的详细信息列表
         */
        private List<Step> steps;
    }

    /**
     * 导航路段的详细信息
     */
    @Data
    public static class Step {
        /**
         * 行驶指示说明（如"向北行驶"等）
         */
        private String instruction;
        /**
         * 行驶方向描述
         */
        private String orientation;
        /**
         * 此路段主要道路名称
         */
        private String road;
        /**
         * 此路段行驶距离，单位：米
         */
        private String distance;
        /**
         * 此路段道路收费，单位：元
         */
        private String tolls;
        /**
         * 此路段收费路段长度，单位：米
         */
        @JsonProperty("toll_distance")
        private String tollDistance;
        /**
         * 此路段主要收费道路名称
         */
        @JsonProperty("toll_road")
        private String tollRoad;
        /**
         * 此路段坐标点串，格式：x1,y1;x2,y2;...
         */
        private String polyline;
        /**
         * 主要导航动作（如"左转"、"右转"等）
         */
        private String action;
        /**
         * 辅助导航动作（如"靠左"、"靠右"等）
         */
        @JsonProperty("assistant_action")
        private String assistantAction;
        /**
         * 此路段实时路况信息列表
         */
        private List<Tmc> tmcs;
        /**
         * 此路段途经的行政区划信息
         */
        private List<City> cities;
        /**
         * 此路段预计行驶时间，单位：秒
         */
        private String duration;
    }

    /**
     * 路段实时路况信息
     */
    @Data
    public static class Tmc {
        /**
         * 路况路段起点坐标
         */
        private String lcode;
        /**
         * 路况路段长度，单位：米
         */
        private String distance;
        /**
         * 路况状态：0-未知，1-畅通，2-缓行，3-拥堵，4-严重拥堵
         */
        private String status;
        /**
         * 路况路段坐标串，格式：x1,y1;x2,y2;...
         */
        private String polyline;
    }

    /**
     * 途经城市信息
     */
    @Data
    public static class City {
        /**
         * 城市名称
         */
        private String name;
        /**
         * 城市编码
         */
        private String citycode;
        /**
         * 区域编码（行政区划代码）
         */
        private String adcode;
        /**
         * 途经的区县信息列表
         */
        private List<District> districts;
    }

    /**
     * 途经区县信息
     */
    @Data
    public static class District {
        /**
         * 区县名称
         */
        private String name;
        /**
         * 区县编码（行政区划代码）
         */
        private String adcode;
    }
}
