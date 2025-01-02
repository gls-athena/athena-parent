package com.gls.athena.sdk.amap.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 骑行路径规划请求
 *
 * @author george
 */
@Data
@Accessors(chain = true)
public class BicyclingV4Request implements Serializable {
    /**
     * 高德key
     */
    private String key;
    /**
     * 出发点经纬度
     */
    private String origin;
    /**
     * 目的地经纬度
     */
    private String destination;
}