package com.gls.athena.sdk.amap.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 高德地图驾车路径规划 V3 版本请求参数
 * 用于计算从起点到终点的驾车导航路径，支持途经点、避让区域等高级功能
 *
 * @author george
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class DrivingV3Request extends BaseV3Request {
    /**
     * 出发点坐标
     * 格式：经度,纬度（小数点后不超过6位）
     * 示例：116.481028,39.989643
     */
    private String origin;

    /**
     * 目的地坐标
     * 格式：经度,纬度（小数点后不超过6位）
     * 示例：116.465302,40.004717
     */
    private String destination;

    /**
     * 目的地 POI ID
     * 当目的地为POI时，可通过POI ID来唯一标识
     */
    private String destinationid;

    /**
     * 目的地POI类别
     * 指定目的地的POI类型，辅助精准定位目的地
     */
    private String destinationtype;

    /**
     * 驾车路径规划策略
     * 0-速度优先（默认）
     * 1-费用优先
     * 2-距离优先
     * 3-不走高速
     * 4-躲避拥堵
     * 5-多策略（同时返回速度优先、费用优先、距离优先的路径）
     * 6-不走高速且避免收费
     * 7-不走高速且躲避拥堵
     * 8-躲避拥堵且不走高速且避免收费
     */
    private String strategy = "0";

    /**
     * 途经点坐标
     * 格式：经度,纬度;经度,纬度...（小数点后不超过6位）
     * 最多支持16个途经点
     */
    private String waypoints;

    /**
     * 避让区域
     * 区域避让，支持多个避让区域，每个区域最多可有16个顶点
     * 格式：经度,纬度;经度,纬度;...
     */
    private String avoidpolygons;

    /**
     * 车牌省份
     * 用汉字填入车牌省份缩写，用于判断是否限行
     * 示例：京、津、冀等
     */
    private String province;

    /**
     * 车牌号码
     * 填入除省份及标点之外的车牌字母和数字（需大写）
     * 示例：NH1N11
     */
    private String number;

    /**
     * 车辆类型
     * 0-普通汽车（默认）
     * 1-纯电动车
     * 2-插电混动车
     */
    private String cartype = "0";

    /**
     * 是否使用轮渡
     * 0-使用渡轮（默认）
     * 1-不使用渡轮
     */
    private String ferry = "0";

    /**
     * 是否返回路径聚合信息
     * true-返回路径聚合信息
     * false-不返回路径聚合信息（默认）
     */
    private String roadaggregation = "false";

    /**
     * 是否返回步骤信息
     * 0-返回步骤信息（默认）
     * 1-不返回步骤信息
     */
    private String nosteps = "0";

    /**
     * 返回结果控制
     * base-返回基本信息
     * all-返回全部信息（默认）
     */
    private String extensions = "all";
}
