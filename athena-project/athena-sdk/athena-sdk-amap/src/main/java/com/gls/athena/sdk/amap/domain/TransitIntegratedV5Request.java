package com.gls.athena.sdk.amap.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 公交路径规划 2.0 请求
 *
 * @author george
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class TransitIntegratedV5Request extends BaseV3Request {

    /**
     * 出发点
     */
    private String origin;
    /**
     * 目的地经纬度
     */
    private String destination;
    /**
     * 起点 POI ID
     */
    private String originpoi;
    /**
     * 目的地 POI ID
     */
    private String destinationpoi;
    /**
     * 起点所在行政区域编码
     */
    private String ad1;
    /**
     * 终点所在行政区域编码
     */
    private String ad2;
    /**
     * 起点所在城市
     */
    private String city1;
    /**
     * 终点所在城市
     */
    private String city2;
    /**
     * 公共交通换乘策略
     */
    private String strategy = "0";
    /**
     * 返回方案条数
     */
    @JsonProperty("AlternativeRoute")
    private String alternativeRoute;
    /**
     * 地铁出入口数量
     */
    private String multiexport;
    /**
     * 考虑夜班车
     */
    private String nightflag = "0";
    /**
     * 请求日期
     */
    private String date;
    /**
     * 请求时间
     */
    private String time;
    /**
     * 返回结果控制
     */
    @JsonProperty("show_fields")
    private String showFields;
}
