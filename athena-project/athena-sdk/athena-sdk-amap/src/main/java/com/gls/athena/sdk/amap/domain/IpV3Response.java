package com.gls.athena.sdk.amap.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * IP地理位置定位响应实体类
 * 用于封装高德地图IP定位API V3版本的响应数据
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class IpV3Response extends BaseV3Response {
    /**
     * 省份名称
     * 例如：北京市、广东省
     */
    private String province;

    /**
     * 城市名称
     * 例如：北京市、深圳市
     */
    private String city;

    /**
     * 城市的行政区划代码
     * 符合中华人民共和国行政区划代码标准
     * 例如：110000（北京市）
     */
    private String adcode;

    /**
     * 所在城市矩形区域经纬度范围
     * 格式：经度,纬度;经度,纬度
     * 例如：116.0119343,39.66127144;116.7829835,40.2164962
     */
    private String rectangle;
}
