package com.gls.athena.sdk.amap.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 高德地图公交路径规划V3版本接口响应实体
 * 包含完整的公交换乘方案信息，包括步行、公交、地铁等多种出行方式
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TransitIntegratedV3Response extends BaseV3Response {
    /**
     * 返回的公交换乘方案数量
     */
    private String count;
    /**
     * 完整的公交换乘方案信息
     */
    private Route route;

    /**
     * 公交换乘方案的路线信息
     * 包含起终点信息、距离、费用等基础数据
     */
    @Data
    public static class Route {
        /**
         * 起点坐标 格式：x,y
         */
        private String origin;
        /**
         * 终点坐标 格式：x,y
         */
        private String destination;
        /**
         * 起点和终点的步行距离，单位：米
         */
        private String distance;
        /**
         * 预估出租车费用，单位：元
         */
        @JsonProperty("taxi_cost")
        private String taxiCost;
        /**
         * 可选的公交换乘方案列表
         */
        private List<Transit> transits;
    }

    /**
     * 单个公交换乘方案详细信息
     * 包含费用、时间、距离等具体换乘数据
     */
    @Data
    public static class Transit {
        /**
         * 此换乘方案的总费用，单位：元
         */
        private String cost;
        /**
         * 此换乘方案的预计耗时，单位：秒
         */
        private String duration;
        /**
         * 是否夜班车线路，1：是，0：否
         */
        private String nightflag;
        /**
         * 此换乘方案的总步行距离，单位：米
         */
        @JsonProperty("walking_distance")
        private String walkingDistance;
        /**
         * 线路相关的紧急事件信息
         */
        private Emergency emergency;
        /**
         * 换乘路段信息列表，包含步行、公交等详细换乘信息
         */
        private List<Segment> segments;
        /**
         * 此方案的总距离，单位：米
         */
        private String distance;
        /**
         * 是否错过末班车，1：是，0：否
         */
        private String missed;
    }

    /**
     * 线路紧急事件信息
     * 用于描述公交线路的临时调整、故障等特殊情况
     */
    @Data
    public static class Emergency {
        /**
         * 事件影响的线路类型
         * 例如：地铁、公交等
         */
        private String linetype;
        /**
         * 事件的标签描述
         * 例如：临时调整、暂停运营等
         */
        private String eventTagDesc;
        /**
         * 事件的详细说明文本
         */
        private String ldescription;
        /**
         * 受影响线路的唯一标识
         */
        private String busid;
        /**
         * 受影响线路的名称
         */
        private String busname;
    }

    /**
     * 换乘路段信息
     * 描述一次完整出行中的各个出行阶段，包括步行、公交、地铁等
     */
    @Data
    public static class Segment {
        /**
         * 步行段信息，包含步行路线、距离等
         */
        private Walking walking;
        /**
         * 公交段信息，包含公交线路、站点等
         */
        private Bus bus;
        /**
         * 地铁站入口信息，包含位置、名称等
         */
        private Point entrance;
        /**
         * 地铁站出口信息，包含位置、名称等
         */
        private Point exit;
        /**
         * 火车段信息，包含车次、时刻等
         */
        private Railway railway;
        /**
         * 出租车段信息
         */
        private Taxi taxi;
    }

    /**
     * 步行导航段详细信息
     * 包含步行路线的起终点、距离、预计时间等信息
     */
    @Data
    public static class Walking {
        /**
         * 步行起点坐标，格式：x,y
         */
        private String origin;
        /**
         * 步行终点坐标，格式：x,y
         */
        private String destination;
        /**
         * 步行距离，单位：米
         */
        private String distance;
        /**
         * 步行预计耗时，单位：秒
         */
        private String duration;
        /**
         * 步行路段的详细信息列表
         */
        private List<Step> steps;
    }

    /**
     * 步行路段详细信息
     * 描述具体的步行导航指示
     */
    @Data
    public static class Step {
        /**
         * 步行导航指示说明
         * 例如："向北步行100米"
         */
        private String instruction;
        /**
         * 当前步行段所在道路名称
         */
        private String road;
        /**
         * 当前步行段距离，单位：米
         */
        private String distance;
        /**
         * 当前步行段预计耗时，单位：秒
         */
        private String duration;
        /**
         * 当前步行段路线坐标点串
         * 格式：x1,y1;x2,y2;x3,y3...
         */
        private String polyline;
        /**
         * 主要行动方向
         * 例如：直行、左转、右转等
         */
        private String action;
        /**
         * 辅助行动提示
         * 例如：靠左、人行横道等
         */
        @JsonProperty("assistant_action")
        private String assistantAction;
    }

    /**
     * 公交导航段详细信息
     * 包含完整的公交线路信息
     */
    @Data
    public static class Bus {
        /**
         * 可选的公交线路列表
         * 可能包含多条可选择的公交线路
         */
        private List<Busline> buslines;
    }

    /**
     * 公交线路详细信息
     * 描述具体的公交线路，包含首末班车时间、途经站点等信息
     */
    @Data
    public static class Busline {
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
         * 例如："1路"、"地铁1号线"等
         */
        private String name;
        /**
         * 公交线路唯一标识
         */
        private String id;
        /**
         * 公交类型
         * 例如：普通公交、地铁、快速公交BRT等
         */
        private String type;
        /**
         * 该线路行驶距离，单位：米
         */
        private String distance;
        /**
         * 预计行驶时间，单位：秒
         */
        private String duration;
        /**
         * 公交线路坐标点串
         * 格式：x1,y1;x2,y2;x3,y3...
         */
        private String polyline;
        /**
         * 首班车时间，格式：HH:mm
         */
        @JsonProperty("start_time")
        private String startTime;
        /**
         * 末班车时间，格式：HH:mm
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
        /**
         * 发车时间标记
         * 用于标识是否有实时发车信息
         */
        private String bustimetag;
    }

    /**
     * 公交站点信息
     * 描述公交站点的基本信息，包括名称、位置等
     */
    @Data
    public static class Stop {
        /**
         * 站点名称
         */
        private String name;
        /**
         * 站点唯一标识
         */
        private String id;
        /**
         * 站点坐标，格式：x,y
         */
        private String location;
    }

    /**
     * 地铁站出入口信息
     * 描述地铁站的具体出入口位置信息
     */
    @Data
    public static class Point {
        /**
         * 出入口名称
         * 例如："A口"、"B口"等
         */
        private String name;
        /**
         * 出入口坐标，格式：x,y
         */
        private String location;
    }

    /**
     * 火车导航段详细信息
     * 包含完整的火车线路信息，如车次、时刻表等
     */
    @Data
    public static class Railway {
        /**
         * 线路唯一标识
         */
        private String id;
        /**
         * 行车时间，单位：秒
         */
        private String time;
        /**
         * 线路名称
         * 例如："京广线"等
         */
        private String name;
        /**
         * 车次编号
         * 例如："G101"等
         */
        private String trip;
        /**
         * 行驶总距离，单位：米
         */
        private String distance;
        /**
         * 列车类型
         * 例如：高铁、动车、普快等
         */
        private String type;
        /**
         * 始发站详细信息
         */
        @JsonProperty("departure_stop")
        private DepartureStop departureStop;
        /**
         * 终到站详细信息
         */
        @JsonProperty("arrival_stop")
        private ArrivalStop arrivalStop;
        /**
         * 途经站点列表，仅在extensions=all时返回
         */
        @JsonProperty("via_stops")
        private List<ViaStops> viaStops;
        /**
         * 备选方案列表，仅在extensions=all时返回
         */
        private List<Alter> alters;
        /**
         * 座位类型及票价信息列表
         */
        private List<Space> spaces;
    }

    /**
     * 火车始发站信息
     */
    @Data
    public static class DepartureStop {
        /**
         * 车站唯一标识
         */
        private String id;
        /**
         * 车站名称
         */
        private String name;
        /**
         * 车站坐标，格式：x,y
         */
        private String location;
        /**
         * 车站所在城市的行政区划代码
         */
        private String adcode;
        /**
         * 发车时间，格式：HH:mm
         */
        private String time;
        /**
         * 是否为始发站，1：是，0：否
         */
        private String start;
    }

    /**
     * 火车终到站信息
     */
    @Data
    public static class ArrivalStop {
        /**
         * 车站唯一标识
         */
        private String id;
        /**
         * 车站名称
         */
        private String name;
        /**
         * 车站坐标，格式：x,y
         */
        private String location;
        /**
         * 车站所在城市的行政区划代码
         */
        private String adcode;
        /**
         * 到站时间，格式：HH:mm
         */
        private String time;
        /**
         * 是否为终到站，1：是，0：否
         */
        private String end;
    }

    /**
     * 火车途经站点信息
     */
    @Data
    public static class ViaStops {
        /**
         * 车站唯一标识
         */
        private String id;
        /**
         * 车站名称
         */
        private String name;
        /**
         * 车站坐标，格式：x,y
         */
        private String location;
        /**
         * 到站时间，格式：HH:mm
         */
        private String time;
        /**
         * 停站时长，单位：分钟
         */
        private String wait;
    }

    /**
     * 备选方案信息
     */
    @Data
    public static class Alter {
        /**
         * 备选方案唯一标识
         */
        private String id;
        /**
         * 备选方案描述
         */
        private String name;
    }

    /**
     * 座位类型及票价信息
     */
    @Data
    public static class Space {
        /**
         * 座位类型编码
         * 例如：特等座、一等座、二等座等
         */
        private String code;
        /**
         * 票价，单位：元
         */
        private String cost;
    }

    /**
     * 出租车导航段信息
     * TODO: 待完善具体字段
     */
    @Data
    public static class Taxi {
        //TODO
    }
}
