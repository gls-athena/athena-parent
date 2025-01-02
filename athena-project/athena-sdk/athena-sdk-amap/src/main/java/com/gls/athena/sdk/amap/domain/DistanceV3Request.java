package com.gls.athena.sdk.amap.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 距离测量请求
 *
 * @author george
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class DistanceV3Request extends BaseV3Request {
    /**
     * 起点坐标
     */
    private String origins;
    /**
     * 终点坐标
     */
    private String destination;
    /**
     * 路径计算的方式和方法
     */
    private String type = "1";
}