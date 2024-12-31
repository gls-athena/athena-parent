package com.gls.athena.sdk.amap.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 骑行路径规划响应
 *
 * @author george
 */
@Data
public class BicyclingV4Response implements Serializable {
    /**
     * 业务数据字段
     */
    private Bicycling data;
    /**
     * 返回结果码
     */
    private Integer errcode;
    /**
     * 此字段会详细说明错误原因
     */
    private String errdetail;
    /**
     * OK代表成功
     */
    private String errmsg;
    /**
     *
     */
    private String ext;

    /**
     * 业务数据字段
     */
    @Data
    public static class Bicycling implements Serializable {
        /**
         * 起点坐标
         */
        private String origin;
        /**
         * 终点坐标
         */
        private String destination;
        /**
         * 骑行方案列表信息
         */
        private List<Path> paths;

    }

    /**
     * 骑行方案信息
     */
    @Data
    public static class Path implements Serializable {
        /**
         * 起终点的骑行距离
         */
        private Integer distance;
        /**
         * 起终点的骑行时间
         */
        private Integer duration;
        /**
         * 具体骑行结果
         */
        private List<Step> steps;

    }

    /**
     * 骑行路段
     */
    @Data
    public static class Step implements Serializable {
        /**
         * 路段骑行指示
         */
        private String instruction;
        /**
         * 此段路道路名称
         */
        private String road;
        /**
         * 此段路骑行距离
         */
        private Integer distance;
        /**
         * 此段路骑行方向
         */
        private String orientation;
        /**
         * 此段路骑行耗时
         */
        private Integer duration;
        /**
         * 此段路骑行的坐标点
         */
        private String polyline;
        /**
         * 此段路骑行主要动作
         */
        private String action;
        /**
         * 此段路骑行辅助动作
         */
        @JsonProperty("assistant_action")
        private String assistantAction;
        /**
         * 此段路骑行类型
         */
        @JsonProperty("walk_type")
        private Integer walkType;
    }
}
