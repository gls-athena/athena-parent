package com.gls.athena.sdk.amap.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 步行路径规划响应
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WalkingResponse extends BaseResponse {
    /**
     * 返回结果数目
     */
    private String count;
    /**
     * 路径规划信息
     */
    private Route route;

    /**
     * 路径规划信息
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
         * 路径规划方案
         */
        private List<Path> paths;

    }

    /**
     * 路径规划方案
     */
    @Data
    public static class Path implements Serializable {
        /**
         * 起点和终点的步行距离
         */
        private String distance;
        /**
         * 步行时间预计
         */
        private String duration;
        /**
         * 步行路段
         */
        private List<Step> steps;

    }

    /**
     * 步行路段
     */
    @Data
    public static class Step implements Serializable {
        /**
         * 路段步行指示
         */
        private String instruction;
        /**
         * 道路名称
         */
        private String road;
        /**
         * 此路段距离
         */
        private String distance;
        /**
         * 步行方向
         */
        private String orientation;
        /**
         * 此路段预计步行时间
         */
        private String duration;
        /**
         * 此路段坐标点
         */
        private String polyline;
        /**
         * 动作
         */
        private String action;
        /**
         * 辅助动作
         */
        @JsonProperty("assistant_action")
        private String assistantAction;
        /**
         * 这段路是否存在特殊的方式
         */
        @JsonProperty("walk_type")
        private String walkType;
    }

}
