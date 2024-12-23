package com.gls.athena.starter.amap.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 门牌信息列表
 *
 * @author george
 */
@Data
public class StreetNumber implements Serializable {
    /**
     * 街道名称
     */
    private String street;
    /**
     * 门牌号
     */
    private String number;
    /**
     * 坐标点
     */
    private String location;
    /**
     * 方向
     */
    private String direction;
    /**
     * 门牌地址到请求坐标的距离
     */
    private String distance;
}
