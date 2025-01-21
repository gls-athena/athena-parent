package com.gls.athena.sdk.amap.domain;

import lombok.Data;

/**
 * 高德地图多边形搜索V3请求参数
 *
 * @author george
 */
@Data
public class PolygonV3Request {
    /**
     * 用户在高德地图官网申请Web服务API类型KEY（必填）
     */
    private String key;

    /**
     * 经纬度坐标对（必填）
     * 规则：经度和纬度用","分割，经度在前，纬度在后，坐标对用"|"分割
     * 经纬度小数点后不得超过6位
     * 多边形为矩形时，可传入左上右下两顶点坐标对；其他情况下首尾坐标对需相同
     */
    private String polygon;

    /**
     * 查询关键字（可选）
     * 规则：只支持一个关键字
     */
    private String keywords;

    /**
     * 查询POI类型（可选）
     * 多个类型用"|"分割
     * 可选值：分类代码或汉字
     */
    private String types;

    /**
     * 每页记录数据（可选）
     * 强烈建议不超过25，若超过25可能造成访问报错
     * 默认值：20
     */
    private Integer offset;

    /**
     * 当前页数（可选）
     * 默认值：1
     */
    private Integer page;

    /**
     * 返回结果控制（可选）
     * 默认返回基本地址信息
     * 取值为all返回地址信息、附近POI、道路以及道路交叉口信息
     * 默认值：base
     */
    private String extensions;

    /**
     * 数字签名（可选）
     */
    private String sig;

    /**
     * 回调函数（可选）
     * 仅在output=JSON时有效
     */
    private String callback;
}
