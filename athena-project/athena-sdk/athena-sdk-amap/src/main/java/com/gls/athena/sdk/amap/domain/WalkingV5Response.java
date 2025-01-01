package com.gls.athena.sdk.amap.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 步行路径规划 2.0 响应
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WalkingV5Response extends BaseV3Response {
    /**
     * 路径规划方案总数
     */
    private String count;
    /**
     * 路径规划方案
     */
    private Route route;

    /**
     * 路径规划方案
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
         * 步行路段
         */
        private List<Step> steps;
        /**
         * 费用成本
         */
        private Cost cost;
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
         * 路段方向
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
         * 设置后可返回详细导航动作指令
         */
        private Navi navi;
        /**
         * 设置后可返回分路段坐标点串，两点间用“,”分隔
         */
        private String polyline;
    }

    /**
     * 设置后可返回方案所需时间及费用成本。注意：steps 中不返回 taxi 字段。
     */
    @Data
    public static class Cost implements Serializable {
        /**
         * 线路耗时，包括方案总耗时及分段 step 中的耗时
         */
        private String duration;
        /**
         * 预估打车费用
         */
        private String taxi;
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
        /**
         * 算路结果中存在的道路类型：
         * 0，普通道路 1，人行横道 3，地下通道 4，过街天桥 5，地铁通道 6，公园 7，广场 8，扶梯 9，直梯 10，索道 11，空中通道 12，建筑物穿越通道 13，行人通道 14，游船路线 15，观光车路线 16，滑道 18，扩路 19，道路附属连接线 20，阶梯 21，斜坡 22，桥 23，隧道 30，轮渡
         */
        @JsonProperty("walk_type")
        private String walkType;
    }
}
