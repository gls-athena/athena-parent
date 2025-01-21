package com.gls.athena.sdk.amap.domain.v5;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gls.athena.sdk.amap.domain.v3.BaseV3Request;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 高德地图骑行路径规划V5版本请求参数
 *
 * <p>用于请求高德地图骑行路径规划服务的参数封装类，支持获取骑行路线规划方案。
 * 继承自{@link BaseV3Request}基础请求类。</p>
 *
 * @author george
 * @see BaseV3Request
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class BicyclingV5Request extends BaseV3Request {
    /**
     * 起点经纬度
     * <p>格式：longitude,latitude</p>
     * <p>示例：116.434307,39.90909</p>
     */
    private String origin;

    /**
     * 终点经纬度
     * <p>格式：longitude,latitude</p>
     * <p>示例：116.434307,39.90909</p>
     */
    private String destination;

    /**
     * 返回结果字段控制
     * <p>可选值：cost（耗时）、distance（距离）、path（路径坐标点串）</p>
     * <p>多个字段用","分隔</p>
     */
    @JsonProperty("show_fields")
    private String showFields;

    /**
     * 返回方案条数控制
     * <p>默认1条，最多返回3条</p>
     * <p>可选值：1-3</p>
     */
    @JsonProperty("alternative_route")
    private String alternativeRoute;
}
