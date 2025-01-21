package com.gls.athena.sdk.amap.domain.v3;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 高德地图坐标转换请求实体类
 * 用于在不同坐标系统之间进行坐标点的转换
 *
 * @author george
 * @since 1.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class CoordinateConvertV3Request extends BaseV3Request {
    /**
     * 需要转换的坐标点
     * 格式：经度,纬度;经度,纬度...
     * 例如：116.481499,39.990475;116.481499,39.990375
     */
    private String locations;
    /**
     * 原坐标系类型
     * 可选值：
     * gps - GPS原始坐标
     * mapbar - 图吧坐标
     * baidu - 百度坐标
     * autonavi - 高德坐标
     */
    private String coordsys;
}
