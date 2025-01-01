package com.gls.athena.sdk.amap.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 步行路径规划 2.0 请求
 *
 * @author george
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class WalkingV5Request extends BaseV3Request {
    /**
     * 起点信息
     */
    private String origin;
    /**
     * 目的地信息
     */
    private String destination;
    /**
     * 起点 POI ID
     */
    @JsonProperty("origin_id")
    private String originId;
    /**
     * 目的地 POI ID
     */
    @JsonProperty("destination_id")
    private String destinationId;
    /**
     * 返回路线条数
     */
    @JsonProperty("alternative_route")
    private String alternativeRoute;
    /**
     * 返回结果控制
     */
    @JsonProperty("show_fields")
    private String showFields;
    /**
     * 是否需要室内算路
     */
    private String isindoor;
}
