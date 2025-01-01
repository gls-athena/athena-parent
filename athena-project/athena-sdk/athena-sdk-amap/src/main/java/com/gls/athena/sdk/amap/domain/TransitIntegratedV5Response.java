package com.gls.athena.sdk.amap.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 公交路径规划 2.0 响应
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TransitIntegratedV5Response extends BaseV3Response {
    /**
     * 公交换乘方案数目
     */
    private String count;
    /**
     * 路线规划信息
     */
    private Route route;

    /**
     * 路线规划信息
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
         * 起点和终点的距离
         */
        private String distance;
        /**
         * 费用信息
         */
        private Cost cost;
        /**
         * 换乘方案列表
         */
        private List<Transit> transits;
        /**
         * 方案数量
         */
        private String count;
    }

    /**
     * 费用信息
     */
    @Data
    public static class Cost implements Serializable {
        /**
         * 行程耗时，单位：秒
         */
        private String duration;
        /**
         * 公交费用，单位：元
         */
        @JsonProperty("transit_fee")
        private String transitFee;
        /**
         * 出租车费用，单位：元
         */
        @JsonProperty("taxi_fee")
        private String taxiFee;
    }

    /**
     * 换乘方案信息
     */
    @Data
    public static class Transit implements Serializable {
        /**
         * 此换乘方案的费用信息
         */
        private Cost cost;
        /**
         * 此换乘方案的总距离
         */
        private String distance;
        /**
         * 此换乘方案的步行距离
         */
        @JsonProperty("walking_distance")
        private String walkingDistance;
        /**
         * 是否是夜班车，1：是，0：否
         */
        private String nightflag;
        /**
         * 换乘路段列表
         */
        private List<Segment> segments;
    }

    /**
     * 换乘路段信息
     */
    @Data
    public static class Segment implements Serializable {
        /**
         * 步行路段
         */
        private Walking walking;
        /**
         * 公交路段
         */
        private Bus bus;
    }

    /**
     * 步行路段信息
     */
    @Data
    public static class Walking implements Serializable {
        /**
         * 步行终点坐标
         */
        private String destination;
        /**
         * 步行距离
         */
        private String distance;
        /**
         * 步行起点坐标
         */
        private String origin;
        /**
         * 步行花费的时间信息
         */
        private Cost cost;
        /**
         * 步行导航信息列表
         */
        private List<Step> steps;
    }

    /**
     * 步行导航信息
     */
    @Data
    public static class Step implements Serializable {
        /**
         * 行走指示
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
         * 此路段坐标点串
         */
        private Polyline polyline;
        /**
         * 导航动作
         */
        private Navi navi;
    }

    /**
     * 路线坐标点串信息
     */
    @Data
    public static class Polyline implements Serializable {
        /**
         * 坐标点串
         */
        private String polyline;
    }

    /**
     * 导航动作信息
     */
    @Data
    public static class Navi implements Serializable {
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
         * 步行类型，0：普通步行，1：特殊步行，如地铁通道等
         */
        @JsonProperty("walk_type")
        private String walkType;
    }

    /**
     * 公交路段信息
     */
    @Data
    public static class Bus implements Serializable {
        /**
         * 公交线路列表
         */
        private List<BusLine> buslines;
    }

    /**
     * 公交线路信息
     */
    @Data
    public static class BusLine implements Serializable {
        /**
         * 上车站点信息
         */
        @JsonProperty("departure_stop")
        private Stop departureStop;
        /**
         * 下车站点信息
         */
        @JsonProperty("arrival_stop")
        private Stop arrivalStop;
        /**
         * 公交线路名称
         */
        private String name;
        /**
         * 公交线路ID
         */
        private String id;
        /**
         * 公交类型
         */
        private String type;
        /**
         * 此线路距离
         */
        private String distance;
        /**
         * 此线路花费信息
         */
        private Cost cost;
        /**
         * 坐标信息
         */
        private Polyline polyline;
        /**
         * 发车时间提示
         */
        @JsonProperty("bus_time_tips")
        private String busTimeTips;
        /**
         * 时间标签
         */
        private String bustimetag;
        /**
         * 首班车时间
         */
        @JsonProperty("start_time")
        private String startTime;
        /**
         * 末班车时间
         */
        @JsonProperty("end_time")
        private String endTime;
        /**
         * 途经站点数量
         */
        @JsonProperty("via_num")
        private String viaNum;
        /**
         * 途经站点列表
         */
        @JsonProperty("via_stops")
        private List<Stop> viaStops;
    }

    /**
     * 站点信息
     */
    @Data
    public static class Stop implements Serializable {
        /**
         * 站点名称
         */
        private String name;
        /**
         * 站点ID
         */
        private String id;
        /**
         * 站点坐标
         */
        private String location;
        /**
         * 站点入口信息
         */
        private Entrance entrance;
        /**
         * 站点出口信息
         */
        private Exit exit;
    }

    /**
     * 站点入口信息
     */
    @Data
    public static class Entrance implements Serializable {
        /**
         * 入口名称
         */
        private String name;
        /**
         * 入口坐标
         */
        private String location;
    }

    /**
     * 站点出口信息
     */
    @Data
    public static class Exit implements Serializable {
        /**
         * 出口名称
         */
        private String name;
        /**
         * 出口坐标
         */
        private String location;
    }
}
