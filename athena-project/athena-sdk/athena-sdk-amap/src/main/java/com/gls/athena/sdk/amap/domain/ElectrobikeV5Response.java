package com.gls.athena.sdk.amap.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 高德地图电动车路径规划V5版本响应实体
 * 用于封装电动车路径规划2.0版本的API响应数据
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ElectrobikeV5Response extends BaseV3Response {
    /**
     * 返回的路径规划方案数量
     */
    private String count;
    /**
     * 路径规划的详细信息，包含路线、距离等数据
     */
    private Route route;

    /**
     * 路径规划的详细路线信息
     * 包含起点、终点坐标及具体路径方案
     */
    @Data
    public static class Route {
        /**
         * 路线起点坐标，格式：经度,纬度
         */
        private String origin;
        /**
         * 路线终点坐标，格式：经度,纬度
         */
        private String destination;
        /**
         * 可选的路径规划方案列表
         */
        private List<Path> paths;
    }

    /**
     * 单个路径规划方案的详细信息
     * 包含距离、时间和具体路段信息
     */
    @Data
    public static class Path {
        /**
         * 路线总距离，单位：米
         */
        private String distance;
        /**
         * 预计行程时间，单位：秒
         */
        private String duration;
        /**
         * 路线分段信息列表，每个步骤的详细导航信息
         */
        private List<Step> steps;
    }

    /**
     * 路径规划中的单个路段信息
     * 包含导航指示、道路名称等详细信息
     */
    @Data
    public static class Step {
        /**
         * 当前路段的行进指示说明
         */
        private String instruction;
        /**
         * 行进方向指示（如：东、南、西、北等）
         */
        private String orientation;
        /**
         * 当前路段的道路名称
         */
        @JsonProperty("road_name")
        private String roadName;
        /**
         * 当前路段的距离，单位：米
         */
        @JsonProperty("step_distance")
        private String stepDistance;
        /**
         * 当前路段的时间和费用信息
         */
        private Cost cost;
        /**
         * 当前路段的详细导航动作指令
         */
        private Navi navi;
        /**
         * 当前路段的坐标点串，格式：经度,纬度;经度,纬度;...
         */
        private String polyline;
    }

    /**
     * 路段的时间成本信息
     * 包含该路段的预计耗时
     */
    @Data
    public static class Cost {
        /**
         * 当前路段的预计耗时，单位：秒
         */
        private String duration;
    }

    /**
     * 导航动作指令详情
     * 包含主要动作和辅助动作说明
     */
    @Data
    public static class Navi {
        /**
         * 主要导航动作指令（如：向前、左转、右转等）
         */
        private String action;
        /**
         * 辅助导航动作指令，用于补充说明主要动作
         */
        @JsonProperty("assistant_action")
        private String assistantAction;
        /**
         * 道路类型编码：
         * 0-普通道路   1-人行横道   3-地下通道   4-过街天桥
         * 5-地铁通道   6-公园      7-广场      8-扶梯
         * 9-直梯      10-索道     11-空中通道  12-建筑物穿越通道
         * 13-行人通道  14-游船路线  15-观光车路线 16-滑道
         * 18-扩路     19-道路附属连接线  20-阶梯   21-斜坡
         * 22-桥       23-隧道     30-轮渡
         */
        @JsonProperty("walk_type")
        private String walkType;
    }
}
