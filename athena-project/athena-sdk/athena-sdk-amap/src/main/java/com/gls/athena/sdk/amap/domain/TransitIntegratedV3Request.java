package com.gls.athena.sdk.amap.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 高德地图公交路径规划 V3 版本请求对象
 * 用于查询公交出行路线，支持市内和跨城公交查询
 *
 * @author george
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class TransitIntegratedV3Request extends BaseV3Request {
    /**
     * 出发点坐标
     * 格式：经度,纬度（小数点后不超过6位）
     * 示例：116.434307,39.90909
     */
    private String origin;

    /**
     * 目的地坐标
     * 格式：经度,纬度（小数点后不超过6位）
     * 示例：116.434307,39.90909
     */
    private String destination;

    /**
     * 城市编码或城市名称
     * 用于市内公交换乘查询，也可用于跨城公交的起点城市
     * 示例：北京/010
     */
    private String city;

    /**
     * 终点城市
     * 用于跨城公交换乘查询的终点城市
     * 示例：天津/022
     */
    private String cityd;

    /**
     * 返回结果控制
     * base：返回基本信息
     * all：返回全部信息（默认值）
     */
    private String extensions = "all";

    /**
     * 公交换乘策略
     * 0：最快捷模式（默认）
     * 1：最经济模式
     * 2：最少换乘模式
     * 3：最少步行模式
     * 4：最舒适模式
     * 5：不乘地铁模式
     */
    private String strategy = "0";

    /**
     * 是否计算夜班车
     * 0：不计算夜班车（默认）
     * 1：计算夜班车
     */
    private String nightflag = "0";

    /**
     * 出发日期
     * 格式：yyyy-MM-dd
     * 示例：2023-12-25
     */
    private String date;

    /**
     * 出发时间
     * 格式：HH:mm
     * 示例：10:30
     */
    private String time;
}
