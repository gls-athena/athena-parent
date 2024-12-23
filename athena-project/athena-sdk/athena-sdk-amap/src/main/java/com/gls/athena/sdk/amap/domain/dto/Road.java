package com.gls.athena.sdk.amap.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 道路信息
 *
 * @author george
 */
@Data
public class Road implements Serializable {
    /**
     * 道路 id
     */
    private String id;
    /**
     * 道路名称
     */
    private String name;
    /**
     * 方位
     */
    private String direction;
    /**
     * 道路到请求坐标的距离
     */
    private String distance;
    /**
     * 坐标点
     */
    private String location;
}
