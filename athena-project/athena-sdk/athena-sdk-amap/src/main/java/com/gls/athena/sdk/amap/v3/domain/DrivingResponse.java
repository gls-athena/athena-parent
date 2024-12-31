package com.gls.athena.sdk.amap.v3.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 驾车路径规划响应
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DrivingResponse extends BaseResponse {
    /**
     * 驾车路径规划方案数目
     */
    private String count;
    /**
     * 驾车路径规划信息列表
     */
    private Route route;

    /**
     * 驾车路径规划信息
     */
    @Data
    public static class Route implements Serializable {
        /**
         * 起点坐标
         */
        private String origin;
        /**
         * 终点坐标
         */
        private String destination;
        /**
         * 打车费用
         */
        @JsonProperty("taxi_cost")
        private String taxiCost;
        /**
         * 驾车换乘方案
         */
        private List<Path> paths;

    }

    /**
     * 驾车换乘方案
     */
    @Data
    public static class Path implements Serializable {
        /**
         * 行驶距离
         */
        private String distance;
        /**
         * 预计行驶时间
         */
        private String duration;
        /**
         * 导航策略
         */
        private String strategy;
        /**
         * 此导航方案道路收费
         */
        private String tolls;
        /**
         * 限行结果
         */
        private String restriction;
        /**
         * 红绿灯个数
         */
        @JsonProperty("traffic_lights")
        private String trafficLights;
        /**
         * 收费路段距离
         */
        @JsonProperty("toll_distance")
        private String tollDistance;
        /**
         * 导航路段
         */
        private List<Step> steps;

    }

    /**
     * 导航路段
     */
    @Data
    public static class Step implements Serializable {
        /**
         * 行驶指示
         */
        private String instruction;
        /**
         * 方向
         */
        private String orientation;
        /**
         * 道路名称
         */
        private String road;
        /**
         * 此路段距离
         */
        private String distance;
        /**
         * 此段收费
         */
        private String tolls;
        /**
         * 收费路段距离
         */
        @JsonProperty("toll_distance")
        private String tollDistance;
        /**
         * 主要收费道路
         */
        @JsonProperty("toll_road")
        private String tollRoad;
        /**
         * 此路段坐标点串
         */
        private String polyline;
        /**
         * 导航主要动作
         */
        private String action;
        /**
         * 导航辅助动作
         */
        @JsonProperty("assistant_action")
        private String assistantAction;
        /**
         * 驾车导航详细信息
         */
        private List<Tmc> tmcs;
        /**
         * 路线途经行政区划
         */
        private List<City> cities;
        /**
         * 此段导航路段的时间
         */
        private String duration;
    }

    /**
     * 驾车导航详细信息
     */
    @Data
    public static class Tmc implements Serializable {
        /**
         * 此段路的起点
         */
        private String lcode;
        /**
         * 此段路的长度
         */
        private String distance;
        /**
         * 此段路的交通情况
         */
        private String status;
        /**
         * 此段路的轨迹
         */
        private String polyline;
    }

    /**
     * 路线途经行政区划
     */
    @Data
    public static class City implements Serializable {
        /**
         * 名称
         */
        private String name;
        /**
         * 途径城市编码
         */
        private String citycode;
        /**
         * 途径区域编码
         */
        private String adcode;
        /**
         * 途径区域
         */
        private List<District> districts;

    }

    /**
     * 途径区域
     */
    @Data
    public static class District implements Serializable {
        /**
         * 途径区县名称
         */
        private String name;
        /**
         * 途径区县编码
         */
        private String adcode;
    }

}
