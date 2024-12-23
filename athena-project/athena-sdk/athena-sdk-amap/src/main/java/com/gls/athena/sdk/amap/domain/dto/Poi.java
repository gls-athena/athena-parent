package com.gls.athena.sdk.amap.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 兴趣点
 *
 * @author george
 */
@Data
public class Poi implements Serializable {
    /**
     * poi 的 id
     */
    private String id;
    /**
     * poi 点名称
     */
    private String name;
    /**
     * poi 类型
     */
    private String type;
    /**
     * 电话
     */
    private String tel;
    /**
     * 方向
     */
    private String direction;
    /**
     * 该 POI 的中心点到请求坐标的距离
     */
    private String distance;
    /**
     * 坐标点
     */
    private String location;
    /**
     * poi 地址信息
     */
    private String address;
    /**
     * poi 权重
     */
    private String poiweight;
    /**
     * poi 所在商圈名称
     */
    private String businessarea;

}
