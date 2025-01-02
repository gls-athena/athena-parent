package com.gls.athena.sdk.amap.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 坐标转换请求
 *
 * @author george
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class CoordinateConvertV3Request extends BaseV3Request {
    /**
     * 坐标点
     */
    private String locations;
    /**
     * 原坐标系
     */
    private String coordsys;
}
