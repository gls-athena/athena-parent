package com.gls.athena.sdk.amap.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 高德地图公交路径规划V5版本响应实体
 * 用于封装公交换乘方案的详细信息，包括路线、费用、时间等数据
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TransitIntegratedV5Response extends BaseV3Response {
    /**
     * 返回的公交换乘方案总数
     */
    private String count;
    /**
     * 完整的路线规划详细信息，包含路线、距离、费用等数据
     */
    private Route route;

    /**
     * 路线规划详细信息
     * 包含起终点信息、距离、费用以及具体的换乘方案列表
     */
    @Data
    public static class Route implements Serializable {
        /**
         * 起点坐标，格式：longitude,latitude
         */
        private String origin;
        /**
         * 终点坐标，格式：longitude,latitude
         */
        private String destination;
        /**
         * 起点和终点的直线距离，单位：米
         */
        private String distance;
        /**
         * 完整路线的费用信息，包含时间和金额
         */
        private Cost cost;
        /**
         * 可选的公交换乘方案列表，每个方案包含完整的换乘细节
         */
        private List<Transit> transits;
        /**
         * 当前路线下可选换乘方案的数量
         */
        private String count;
    }

    /**
     * 费用信息详情
     * 包含时间成本和金额成本的完整计算
     */
    @Data
    public static class Cost implements Serializable {
        /**
         * 行程总耗时，单位：秒
         */
        private String duration;
        /**
         * 公共交通费用（公交、地铁等），单位：元
         */
        @JsonProperty("transit_fee")
        private String transitFee;
        /**
         * 出租车费用估算，单位：元
         */
        @JsonProperty("taxi_fee")
        private String taxiFee;
    }

    /**
     * 换乘方案详细信息
     * 包含单个完整换乘方案的所有细节
     */
    @Data
    public static class Transit implements Serializable {
        /**
         * 当前换乘方案的费用信息，包含时间和金额成本
         */
        private Cost cost;
        /**
         * 当前换乘方案的总距离，单位：米
         */
        private String distance;
        /**
         * 当前换乘方案的总步行距离，单位：米
         */
        @JsonProperty("walking_distance")
        private String walkingDistance;
        /**
         * 是否包含夜班车
         * 1：包含夜班车
         * 0：不包含夜班车
         */
        private String nightflag;
        /**
         * 换乘路段列表，按顺序包含步行和乘车等细节
         */
        private List<Segment> segments;
    }

    /**
     * 换乘路段信息
     * 包含步行和公交两种出行方式的详细信息
     */
    @Data
    public static class Segment implements Serializable {
        /**
         * 步行路段详细信息，包含距离、时间和导航指示
         */
        private Walking walking;
        /**
         * 公交路段详细信息，包含线路、站点等信息
         */
        private Bus bus;
    }

    /**
     * 步行路段详细信息
     * 包含步行导航的完整信息，如起终点、距离、时间和具体步行指示
     */
    @Data
    public static class Walking implements Serializable {
        /**
         * 步行终点坐标，格式：longitude,latitude
         */
        private String destination;
        /**
         * 步行段总距离，单位：米
         */
        private String distance;
        /**
         * 步行起点坐标，格式：longitude,latitude
         */
        private String origin;
        /**
         * 步行段的时间和消耗信息
         */
        private Cost cost;
        /**
         * 步行导航的详细指示列表，包含每个转弯点的信息
         */
        private List<Step> steps;
    }

    /**
     * 步行导航指示信息
     * 包含单个步行段的详细导航信息
     */
    @Data
    public static class Step implements Serializable {
        /**
         * 行走指示说明，如"向北步行100米"
         */
        private String instruction;
        /**
         * 当前步行段所在的道路名称
         */
        private String road;
        /**
         * 当前步行段的距离，单位：米
         */
        private String distance;
        /**
         * 当前步行段的坐标点串信息
         */
        private Polyline polyline;
        /**
         * 导航动作指示，包含主要动作和辅助动作
         */
        private Navi navi;
    }

    /**
     * 路线坐标点串信息
     * 用于在地图上绘制路线
     */
    @Data
    public static class Polyline implements Serializable {
        /**
         * 坐标点串，格式：longitude1,latitude1;longitude2,latitude2;...
         */
        private String polyline;
    }

    /**
     * 导航动作详细信息
     * 描述在路径规划中的具体导航动作
     */
    @Data
    public static class Navi implements Serializable {
        /**
         * 导航主要动作，如"向前直行"、"左转"、"右转"等
         */
        private String action;
        /**
         * 导航辅助动作，为主要动作的补充说明
         */
        @JsonProperty("assistant_action")
        private String assistantAction;
        /**
         * 步行类型
         * 0：普通步行（地面）
         * 1：特殊步行（地下通道、天桥等）
         */
        @JsonProperty("walk_type")
        private String walkType;
    }

    /**
     * 公交路段详细信息
     * 包含公交线路的完整信息
     */
    @Data
    public static class Bus implements Serializable {
        /**
         * 可选的公交线路列表，包含所有可用的公交线路信息
         */
        private List<BusLine> buslines;
    }

    /**
     * 公交线路详细信息
     * 包含单条公交线路的完整运营信息
     */
    @Data
    public static class BusLine implements Serializable {
        /**
         * 上车站点的详细信息，包含名称、位置等
         */
        @JsonProperty("departure_stop")
        private Stop departureStop;
        /**
         * 下车站点的详细信息，包含名称、位置等
         */
        @JsonProperty("arrival_stop")
        private Stop arrivalStop;
        /**
         * 公交线路名称，如"1路"、"地铁1号线"等
         */
        private String name;
        /**
         * 公交线路唯一标识符
         */
        private String id;
        /**
         * 公交类型，如"地铁"、"公交"、"快速公交BRT"等
         */
        private String type;
        /**
         * 当前线路的行驶距离，单位：米
         */
        private String distance;
        /**
         * 当前线路的时间和费用信息
         */
        private Cost cost;
        /**
         * 当前线路的坐标点串信息，用于在地图上绘制
         */
        private Polyline polyline;
        /**
         * 发车时间相关提示信息
         */
        @JsonProperty("bus_time_tips")
        private String busTimeTips;
        /**
         * 时间标签，用于标识高峰、低峰等时段
         */
        private String bustimetag;
        /**
         * 首班车发车时间，格式：HH:mm
         */
        @JsonProperty("start_time")
        private String startTime;
        /**
         * 末班车发车时间，格式：HH:mm
         */
        @JsonProperty("end_time")
        private String endTime;
        /**
         * 该线路的途经站点数量
         */
        @JsonProperty("via_num")
        private String viaNum;
        /**
         * 该线路的所有途经站点列表
         */
        @JsonProperty("via_stops")
        private List<Stop> viaStops;
    }

    /**
     * 公交站点详细信息
     * 包含站点的位置、名称及出入口信息
     */
    @Data
    public static class Stop implements Serializable {
        /**
         * 站点名称，如"西单站"
         */
        private String name;
        /**
         * 站点唯一标识符
         */
        private String id;
        /**
         * 站点坐标，格式：longitude,latitude
         */
        private String location;
        /**
         * 站点入口详细信息，主要用于地铁站
         */
        private Entrance entrance;
        /**
         * 站点出口详细信息，主要用于地铁站
         */
        private Exit exit;
    }

    /**
     * 站点入口详细信息
     * 主要用于描述地铁站的入口位置
     */
    @Data
    public static class Entrance implements Serializable {
        /**
         * 入口名称，如"A口"、"B口"等
         */
        private String name;
        /**
         * 入口坐标，格式：longitude,latitude
         */
        private String location;
    }

    /**
     * 站点出口详细信息
     * 主要用于描述地铁站的出口位置
     */
    @Data
    public static class Exit implements Serializable {
        /**
         * 出口名称，如"C口"、"D口"等
         */
        private String name;
        /**
         * 出口坐标，格式：longitude,latitude
         */
        private String location;
    }
}
