package com.gls.athena.starter.amap.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 地理编码
 *
 * @author george
 */
@Data
public class Geocode implements Serializable {
    /**
     * 结构化地址信息
     */
    private String formatted_address;
    /**
     * 国家
     */
    private String country;
    /**
     * 地址所在的省份名
     */
    private String province;
    /**
     * 城市编码
     */
    private String citycode;
    /**
     * 地址所在的城市名
     */
    private String city;
    /**
     * 地址所在的区
     */
    private String district;
    /**
     * 街道
     */
    private String township;
    /**
     * 社区
     */
    private Neighborhood neighborhood;
    /**
     * 楼
     */
    private Building building;
    /**
     * 区域编码
     */
    private String adcode;
    /**
     * 街道
     */
    private String street;
    /**
     * 门牌
     */
    private String number;
    /**
     * 坐标点
     */
    private String location;
    /**
     * 匹配级别
     */
    private String level;
}
