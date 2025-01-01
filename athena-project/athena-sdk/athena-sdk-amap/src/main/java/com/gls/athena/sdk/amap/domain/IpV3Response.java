package com.gls.athena.sdk.amap.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * IP 定位 响应
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class IpV3Response extends BaseV3Response {
    /**
     * 省份
     */
    private String province;
    /**
     * 城市
     */
    private String city;
    /**
     * 城市的 adcode 编码
     */
    private String adcode;
    /**
     * 所在城市矩形区域范围
     */
    private String rectangle;
}
