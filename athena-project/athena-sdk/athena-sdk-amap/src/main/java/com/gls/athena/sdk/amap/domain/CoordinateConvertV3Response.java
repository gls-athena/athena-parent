package com.gls.athena.sdk.amap.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 坐标转换响应
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CoordinateConvertV3Response extends BaseV3Response {
    /**
     * 坐标点
     */
    private String locations;
}
