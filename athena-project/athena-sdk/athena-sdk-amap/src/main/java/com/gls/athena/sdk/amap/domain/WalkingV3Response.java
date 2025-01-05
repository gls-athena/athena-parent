package com.gls.athena.sdk.amap.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 高德地图步行路径规划 V3 版本响应实体
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WalkingV3Response extends BaseV3Response {
    /**
     * 返回的路径规划方案数量
     */
    private String count;

    /**
     * 路径规划详细信息
     */
    private Route route;

    /**
     * 路径规划详细信息实体类
     */
    @Data
    public static class Route {
        /**
         * 起点坐标，格式：longitude,latitude
         */
        private String origin;

        /**
         * 终点坐标，格式：longitude,latitude
         */
        private String destination;

        /**
         * 步行路径规划方案列表
         */
        private List<Path> paths;
    }

    /**
     * 步行路径规划方案实体类
     */
    @Data
    public static class Path {
        /**
         * 方案总步行距离，单位：米
         */
        private String distance;

        /**
         * 方案预计总耗时，单位：秒
         */
        private String duration;

        /**
         * 步行路段详细信息列表
         */
        private List<Step> steps;
    }

    /**
     * 步行路段详细信息实体类
     */
    @Data
    public static class Step {
        /**
         * 行走指示说明，如"向东步行100米"
         */
        private String instruction;

        /**
         * 途经道路名称
         */
        private String road;

        /**
         * 当前路段步行距离，单位：米
         */
        private String distance;

        /**
         * 步行方向，如：东、南、西、北等
         */
        private String orientation;

        /**
         * 当前路段预计耗时，单位：秒
         */
        private String duration;

        /**
         * 路段坐标点串，格式：longitude1,latitude1;longitude2,latitude2...
         */
        private String polyline;

        /**
         * 导航主要动作，如：直行、左转、右转等
         */
        private String action;

        /**
         * 导航辅助动作，如：靠左、靠右等
         */
        @JsonProperty("assistant_action")
        private String assistantAction;

        /**
         * 路段步行类型，0-普通步行，1-过人行横道，2-过天桥，3-过地下通道
         */
        @JsonProperty("walk_type")
        private String walkType;
    }
}
