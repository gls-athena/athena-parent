package com.gls.athena.sdk.amap.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 高德地图天气查询请求
 *
 * @author george
 * @see BaseV3Request
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class WeatherV3Request extends BaseV3Request {

    /**
     * 城市编码（如：北京市 - 110000）
     */
    private String city;

    /**
     * 气象类型：base-实况天气（默认），all-天气预报
     */
    private String extensions = "base";
}
