package com.gls.athena.sdk.amap.v3.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 驾车路径规划请求
 *
 * @author george
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class DrivingRequest extends BaseRequest {
    /**
     * 出发点
     */
    private String origin;
    /**
     * 目的地
     */
    private String destination;
    /**
     * 目的地 poiid
     */
    private String destinationid;
    /**
     * 终点的 poi 类别
     */
    private String destinationtype;
    /**
     * 驾车选择策略
     */
    private String strategy = "0";
    /**
     * 途经点
     */
    private String waypoints;
    /**
     * 避让区域
     */
    private String avoidpolygons;
    /**
     * 用汉字填入车牌省份缩写，用于判断是否限行
     */
    private String province;
    /**
     * 填入除省份及标点之外，车牌的字母和数字（需大写）。用于判断限行相关。
     */
    private String number;
    /**
     * 车辆类型
     */
    private String cartype = "0";
    /**
     * 在路径规划中，是否使用轮渡
     */
    private String ferry = "0";
    /**
     * 是否返回路径聚合信息
     */
    private String roadaggregation = "false";
    /**
     * 是否返回 steps 字段内容
     */
    private String nosteps = "0";
    /**
     * 返回结果控制
     */
    private String extensions = "all";
}
