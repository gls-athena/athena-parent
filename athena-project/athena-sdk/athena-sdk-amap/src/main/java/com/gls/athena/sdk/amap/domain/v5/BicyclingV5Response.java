package com.gls.athena.sdk.amap.domain.v5;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gls.athena.sdk.amap.domain.v3.BaseV3Response;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 高德地图骑行路径规划V5版本响应实体类
 * 用于封装骑行路径规划API的返回结果
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BicyclingV5Response extends BaseV3Response {
    /**
     * 返回方案的数量
     */
    private String count;

    /**
     * 骑行路径规划具体方案信息
     */
    private Route route;

    /**
     * 骑行路径规划方案详细信息
     * 包含起点、终点坐标及具体路径信息
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
         * 骑行路径规划的多个可选方案列表
         */
        private List<Path> paths;
    }

    /**
     * 单个骑行路径方案的详细信息
     * 包含距离、时间和具体路段信息
     */
    @Data
    public static class Path {
        /**
         * 方案总距离，单位：米
         */
        private String distance;

        /**
         * 预计骑行时间，单位：秒
         */
        private String duration;

        /**
         * 骑行路径的分段信息列表
         */
        private List<Step> steps;
    }

    /**
     * 骑行路径的单个路段信息
     * 包含该段路径的详细导航信息
     */
    @Data
    public static class Step {
        /**
         * 路段骑行导航指示信息
         */
        private String instruction;

        /**
         * 路段的方向指示
         * 如：东、南、西、北等
         */
        private String orientation;

        /**
         * 路段所在道路名称
         */
        @JsonProperty("road_name")
        private String roadName;

        /**
         * 当前路段的距离，单位：米
         */
        @JsonProperty("step_distance")
        private String stepDistance;

        /**
         * 当前路段的时间和成本信息
         */
        private Cost cost;

        /**
         * 当前路段的详细导航动作指令
         */
        private Navi navi;

        /**
         * 分段坐标点集合
         * 格式：longitude1,latitude1;longitude2,latitude2...
         */
        private String polyline;
    }

    /**
     * 路段的时间成本信息
     */
    @Data
    public static class Cost {
        /**
         * 当前路段的预计耗时，单位：秒
         */
        private String duration;
    }

    /**
     * 导航动作指令详细信息
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
         * 用于补充说明主要动作
         */
        @JsonProperty("assistant_action")
        private String assistantAction;

        /**
         * 道路类型编码：
         * 0: 普通道路
         * 1: 人行横道
         * 3: 地下通道
         * 4: 过街天桥
         * 5: 地铁通道
         * 6: 公园
         * 7: 广场
         * 8: 扶梯
         * 9: 直梯
         * 10: 索道
         * 11: 空中通道
         * 12: 建筑物穿越通道
         * 13: 行人通道
         * 14: 游船路线
         * 15: 观光车路线
         * 16: 滑道
         * 18: 扩路
         * 19: 道路附属连接线
         * 20: 阶梯
         * 21: 斜坡
         * 22: 桥
         * 23: 隧道
         * 30: 轮渡
         */
        @JsonProperty("walk_type")
        private String walkType;
    }
}
