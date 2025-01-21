package com.gls.athena.sdk.amap.domain.v3;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 步行路径规划请求
 *
 * @author george
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class WalkingV3Request extends BaseV3Request {

    /**
     * 起点坐标 (经度,纬度)，必填
     * 示例：116.434307,39.90909
     */
    private String origin;

    /**
     * 终点坐标 (经度,纬度)，必填
     * 示例：116.434307,39.90909
     */
    private String destination;

    /**
     * 起点POI ID，选填
     */
    @JsonProperty("origin_id")
    private String originId;

    /**
     * 终点POI ID，选填
     */
    @JsonProperty("destination_id")
    private String destinationId;
}
