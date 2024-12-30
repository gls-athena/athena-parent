package com.gls.athena.sdk.amap.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 公交路径规划响应
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TransitIntegratedResponse extends BaseResponse {
    /**
     * 公交换乘方案数目
     */
    private String count;
    /**
     * 公交换乘信息列表
     */
    private Route route;

    /**
     * 公交换乘信息
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
         * 起点和终点的步行距离
         */
        private String distance;
        /**
         * 出租车费用
         */
        @JsonProperty("taxi_cost")
        private String taxiCost;
        /**
         * 公交换乘方案列表
         */
        private List<Transit> transits;

    }

    /**
     * 公交换乘信息
     */
    @Data
    public static class Transit implements Serializable {
        /**
         * 此换乘方案价格
         */
        private String cost;
        /**
         * 此换乘方案预期时间
         */
        private String duration;
        /**
         * 是否是夜班车
         */
        private String nightflag;
        /**
         * 此方案总步行距离
         */
        @JsonProperty("walking_distance")
        private String walkingDistance;
        /**
         * 紧急事件
         */
        private Emergency emergency;
        /**
         * 换乘路段
         */
        private List<Segment> segments;

        private String distance;

        private String missed;

    }

    /**
     * 紧急事件
     */
    @Data
    public static class Emergency implements Serializable {
        /**
         * 事件类型
         */
        private String linetype;
        /**
         * 事件标签
         */
        private String eventTagDesc;
        /**
         * 事件的线路上的文案
         */
        private String ldescription;
        /**
         * 线路id
         */
        private String busid;
        /**
         * 线路名
         */
        private String busname;
    }

    /**
     * 换乘路段
     */
    @Data
    public static class Segment implements Serializable {
        /**
         * 此路段步行导航信息
         */
        private Walking walking;
        /**
         * 此路段公交导航信息
         */
        private Bus bus;
        /**
         * 地铁入口
         */
        private Point entrance;
        /**
         * 地铁出口
         */
        private Point exit;
        /**
         * 火车导航信息
         */
        private Railway railway;
        /**
         * 出租车导航信息
         */
        private Taxi taxi;
    }

    /**
     * 步行导航信息
     */
    @Data
    public static class Walking implements Serializable {
        /**
         * 起点坐标
         */
        private String origin;
        /**
         * 终点坐标
         */
        private String destination;
        /**
         * 每段线路步行距离
         */
        private String distance;
        /**
         * 步行预计时间
         */
        private String duration;
        /**
         * 步行路段列表
         */
        private List<Step> steps;

    }

    /**
     * 步行路段
     */
    @Data
    public static class Step implements Serializable {
        /**
         * 此段路的行走介绍
         */
        private String instruction;
        /**
         * 路的名字
         */
        private String road;
        /**
         * 此段路的距离
         */
        private String distance;
        /**
         * 此段路预计消耗时间
         */
        private String duration;
        /**
         * 此段路的坐标
         */
        private String polyline;
        /**
         * 步行主要动作
         */
        private String action;
        /**
         * 步行辅助动作
         */
        @JsonProperty("assistant_action")
        private String assistantAction;
    }

    /**
     * 公交导航信息
     */
    @Data
    public static class Bus implements Serializable {
        /**
         * 公交线路
         */
        private List<Busline> buslines;

    }

    /**
     * 公交线路
     */
    @Data
    public static class Busline implements Serializable {
        /**
         * 此段起乘站信息
         */
        @JsonProperty("departure_stop")
        private Stop departureStop;
        /**
         * 此段下车站信息
         */
        @JsonProperty("arrival_stop")
        private Stop arrivalStop;
        /**
         * 公交路线名称
         */
        private String name;
        /**
         * 公交路线 id
         */
        private String id;
        /**
         * 公交类型
         */
        private String type;
        /**
         * 公交行驶距离
         */
        private String distance;
        /**
         * 公交预计行驶时间
         */
        private String duration;
        /**
         * 此路段坐标集
         */
        private String polyline;
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
         * 此段途经公交站数
         */
        @JsonProperty("via_num")
        private String viaNum;
        /**
         * 此段途经公交站点列表
         */
        @JsonProperty("via_stops")
        private List<Stop> viaStops;

        private String bustimetag;
    }

    /**
     * 公交站点
     */
    @Data
    public static class Stop implements Serializable {
        /**
         * 公交站点信息
         */
        private String name;
        /**
         * 公交站点编号
         */
        private String id;
        /**
         * 公交站点坐标
         */
        private String location;
    }

    /**
     * 出入口信息
     */
    @Data
    public static class Point implements Serializable {
        /**
         * 出入口名称
         */
        private String name;
        /**
         * 出入口坐标
         */
        private String location;
    }

    /**
     * 火车导航信息
     */
    @Data
    public static class Railway implements Serializable {
        /**
         * 线路 id 编号
         */
        private String id;
        /**
         * 该线路车段耗时
         */
        private String time;
        /**
         * 线路名称
         */
        private String name;
        /**
         * 线路车次号
         */
        private String trip;
        /**
         * 该 item 换乘段的行车总距离
         */
        private String distance;
        /**
         * 线路车次类型
         */
        private String type;
        /**
         * 火车始发站信息
         */
        @JsonProperty("departure_stop")
        private DepartureStop departureStop;
        /**
         * 火车到站信息
         */
        @JsonProperty("arrival_stop")
        private ArrivalStop arrivalStop;
        /**
         * 途径站点信息，extensions=all 时返回
         */
        @JsonProperty("via_stops")
        private List<ViaStops> viaStops;
        /**
         * 聚合的备选方案，extensions=all 时返回
         */
        private List<Alter> alters;
        /**
         * 仓位及价格信息
         */
        private List<Space> spaces;

    }

    /**
     * 火车始发站信息
     */
    @Data
    public static class DepartureStop implements Serializable {
        /**
         * 上车站点 ID
         */
        private String id;
        /**
         * 上车站点名称
         */
        private String name;
        /**
         * 上车站点坐标
         */
        private String location;
        /**
         * 上车站点所在城市的 adcode
         */
        private String adcode;
        /**
         * 上车点发车时间
         */
        private String time;
        /**
         * 是否始发站，1表示为始发站，0表示非始发站
         */
        private String start;

    }

    /**
     * 火车到站信息
     */
    @Data
    public static class ArrivalStop implements Serializable {
        /**
         * 下车站点 ID
         */
        private String id;
        /**
         * 下车站点名称
         */
        private String name;
        /**
         * 下车站点坐标
         */
        private String location;
        /**
         * 下车站点所在城市的 adcode
         */
        private String adcode;
        /**
         * 下车点到站时间
         */
        private String time;
        /**
         * 是否终点站，1表示为终点站，0表示非终点站
         */
        private String end;
    }

    /**
     * 途径站点信息
     */
    @Data
    public static class ViaStops implements Serializable {
        /**
         * 途径站点 ID
         */
        private String id;
        /**
         * 途径站点名称
         */
        private String name;
        /**
         * 途径站点坐标
         */
        private String location;
        /**
         * 途径站点到站时间
         */
        private String time;
        /**
         * 途径站点的停靠时间，单位：分钟
         */
        private String wait;
    }

    /**
     * 备选方案
     */
    @Data
    public static class Alter implements Serializable {
        /**
         * 备选方案 ID
         */
        private String id;
        /**
         * 备选方案名称
         */
        private String name;
    }

    /**
     * 仓位及价格信息
     */
    @Data
    public static class Space implements Serializable {
        /**
         * 仓位编码
         */
        private String code;
        /**
         * 仓位费用
         */
        private String cost;
    }

    /**
     * 出租车导航信息
     */
    @Data
    public static class Taxi implements Serializable {
        //TODO
    }
}
