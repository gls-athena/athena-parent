package com.gls.athena.sdk.amap.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 高德地图步行路径规划 V5 版本响应实体
 * 用于封装步行路径规划的详细信息，包括路径方案、距离、时间等
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WalkingV5Response extends BaseV3Response {
    /**
     * 返回的路径规划方案总数
     * 表示在起点到终点之间计算出的可行走路径方案数量
     */
    private String count;

    /**
     * 路径规划详细信息
     * 包含起点、终点坐标以及具体的路径方案信息
     */
    private Route route;

    /**
     * 路径规划方案详细信息
     * 包含起终点坐标和具体路径信息的数据结构
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
         * 路径规划方案列表
         * 包含多个可选的步行路径方案
         */
        private List<Path> paths;
    }

    /**
     * 单个路径方案详细信息
     * 描述具体的一条步行路径，包含距离、路段和费用信息
     */
    @Data
    public static class Path {
        /**
         * 步行总距离
         * 单位：米
         */
        private String distance;

        /**
         * 步行路段列表
         * 将整个步行路径分解为多个详细的步行路段
         */
        private List<Step> steps;

        /**
         * 路径费用信息
         * 包含时间成本和可能的费用支出
         */
        private Cost cost;
    }

    /**
     * 步行路段详细信息
     * 描述单个步行路段的具体信息，包含指示、方向等
     */
    @Data
    public static class Step {
        /**
         * 步行导航指示信息
         * 描述当前路段的行走指示，如"向北步行100米"
         */
        private String instruction;

        /**
         * 步行方向
         * 如：东、南、西、北、东北等
         */
        private String orientation;

        /**
         * 当前步行路段的道路名称
         */
        @JsonProperty("road_name")
        private String roadName;

        /**
         * 当前路段的步行距离
         * 单位：米
         */
        @JsonProperty("step_distance")
        private String stepDistance;

        /**
         * 当前路段的时间和费用成本信息
         */
        private Cost cost;

        /**
         * 当前路段的详细导航动作指令
         */
        private Navi navi;

        /**
         * 分路段坐标点集合
         * 格式：经度,纬度;经度,纬度;...
         */
        private String polyline;
    }

    /**
     * 费用和时间成本信息
     * 包含路径耗时和可能的打车费用估算
     */
    @Data
    public static class Cost {
        /**
         * 预计耗时
         * 单位：秒
         */
        private String duration;

        /**
         * 预估打车费用
         * 单位：元
         */
        private String taxi;
    }

    /**
     * 导航动作指令详细信息
     * 包含主要动作和辅助动作的导航指示
     */
    @Data
    public static class Navi {
        /**
         * 主要导航动作
         * 如：直行、左转、右转等
         */
        private String action;

        /**
         * 辅助导航动作
         * 对主要动作的补充说明
         */
        @JsonProperty("assistant_action")
        private String assistantAction;

        /**
         * 步行道路类型编码
         * 0:普通道路 1:人行横道 3:地下通道 4:过街天桥 5:地铁通道
         * 6:公园 7:广场 8:扶梯 9:直梯 10:索道 11:空中通道
         * 12:建筑物穿越通道 13:行人通道 14:游船路线 15:观光车路线
         * 16:滑道 18:扩路 19:道路附属连接线 20:阶梯 21:斜坡
         * 22:桥 23:隧道 30:轮渡
         */
        @JsonProperty("walk_type")
        private String walkType;
    }
}
