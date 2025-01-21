package com.gls.athena.sdk.amap.domain.v3;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 高德地图 V3 版本距离测量请求对象
 * 用于计算两个坐标点之间的距离，支持直线距离和驾车导航距离等多种计算方式
 *
 * @author george
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class DistanceV3Request extends BaseV3Request {
    /**
     * 起点坐标，支持多个起点
     * 格式：经度,纬度|经度,纬度|...（经纬度小数点后不超过6位）
     * 示例：116.481028,39.989643|114.481028,39.989643
     */
    private String origins;

    /**
     * 终点坐标
     * 格式：经度,纬度（经纬度小数点后不超过6位）
     * 示例：114.465302,40.004717
     */
    private String destination;

    /**
     * 路径计算的方式和方法
     * 0：直线距离
     * 1：驾车导航距离（默认）
     * 2：公交规划距离
     * 3：步行规划距离
     */
    private String type = "1";
}
