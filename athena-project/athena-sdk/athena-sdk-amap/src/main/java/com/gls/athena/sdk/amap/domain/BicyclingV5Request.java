package com.gls.athena.sdk.amap.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 骑行路径规划 2.0 请求
 *
 * @author george
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class BicyclingV5Request extends BaseV3Request {
    /**
     * 起点经纬度
     */
    private String origin;
    /**
     * 终点经纬度
     */
    private String destination;
    /**
     * 返回结果控制
     */
    @JsonProperty("show_fields")
    private String showFields;
    /**
     * 返回方案条数
     */
    @JsonProperty("alternative_route")
    private String alternativeRoute;
}
