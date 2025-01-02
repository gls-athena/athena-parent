package com.gls.athena.sdk.amap.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 高德地图天气查询请求实体类
 *
 * <p>用于封装调用高德地图天气查询 API 的请求参数。继承自 {@link BaseV3Request}，
 * 提供天气查询所需的城市编码和天气数据类型等基础参数。</p>
 *
 * @author george
 * @see BaseV3Request
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class WeatherV3Request extends BaseV3Request {

    /**
     * 城市编码
     * <p>高德地图行政区划编码，用于指定查询天气的目标城市。
     * 例如：北京市编码为 "110000"</p>
     */
    private String city;

    /**
     * 气象类型
     * <p>可选值：
     * <ul>
     *     <li>base - 返回实况天气数据（默认值）</li>
     *     <li>all - 返回预报天气数据</li>
     * </ul>
     * </p>
     */
    private String extensions = "base";
}
