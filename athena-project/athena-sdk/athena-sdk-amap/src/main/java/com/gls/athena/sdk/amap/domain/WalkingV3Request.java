package com.gls.athena.sdk.amap.domain;

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
     * 出发点
     * 必填
     * 经纬度坐标
     * 传入内容规则：经度在前，纬度在后，经纬度间以“,”分割，经纬度小数点后不要超过 6 位。
     */
    private String origin;
    /**
     * 目的地
     * 必填
     * 经纬度坐标
     * 传入内容规则：经度在前，纬度在后，经纬度间以“,”分割，经纬度小数点后不要超过 6 位。
     */
    private String destination;
    /**
     * 出发点 POI ID
     * 可选
     * 当起点为POI时，建议填充此值
     */
    @JsonProperty("origin_id")
    private String originId;
    /**
     * 目的地 POI ID
     * 可选
     * 当终点为POI时，建议填充此值
     */
    @JsonProperty("destination_id")
    private String destinationId;
}
