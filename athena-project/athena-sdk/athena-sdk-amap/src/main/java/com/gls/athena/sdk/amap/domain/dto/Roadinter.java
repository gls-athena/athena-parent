package com.gls.athena.sdk.amap.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 道路交叉口
 *
 * @author george
 */
@Data
public class Roadinter implements Serializable {
    /**
     * 方位
     */
    private String direction;
    /**
     * 交叉路口到请求坐标的距离
     */
    private String distance;
    /**
     * 路口经纬度
     */
    private String location;
    /**
     * 第一条道路 id
     */
    private String first_id;
    /**
     * 第一条道路名称
     */
    private String first_name;
    /**
     * 第二条道路 id
     */
    private String second_id;
    /**
     * 第二条道路名称
     */
    private String second_name;

}
