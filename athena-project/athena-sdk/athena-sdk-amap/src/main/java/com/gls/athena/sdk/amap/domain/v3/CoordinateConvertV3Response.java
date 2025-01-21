package com.gls.athena.sdk.amap.domain.v3;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 高德地图坐标转换API的响应对象
 * 用于封装坐标转换服务的返回结果
 *
 * @author george
 * @see BaseV3Response
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CoordinateConvertV3Response extends BaseV3Response {
    /**
     * 转换后的坐标点信息
     * 格式：经度,纬度（如：116.481499,39.990475）
     * 当有多个坐标时，坐标间以分号分隔
     */
    private String locations;
}
