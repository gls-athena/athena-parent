package com.gls.athena.sdk.amap.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 地理编码响应
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GeoResponse extends BaseResponse {
    /**
     * 返回结果数目
     */
    private String count;
    /**
     * 地理编码信息列表
     */
    private List<Geocode> geocodes;

    /**
     * 地理编码信息
     */
    @Data
    public static class Geocode implements Serializable {
        /**
         * 结构化地址信息
         */
        @JsonProperty("formatted_address")
        private String formattedAddress;
        /**
         * 国家
         */
        private String country;
        /**
         * 地址所在的省份名
         */
        private String province;
        /**
         * 地址所在的城市名
         */
        private String city;
        /**
         * 城市编码
         */
        private String citycode;
        /**
         * 地址所在的区
         */
        private String district;
        /**
         * 街道
         */
        private String street;
        /**
         * 门牌
         */
        private String number;
        /**
         * 区域编码
         */
        private String adcode;
        /**
         * 坐标点
         */
        private String location;
        /**
         * 匹配级别
         */
        private String level;
        /**
         * 乡镇街道
         */
        private String township;
        /**
         * 社区信息
         */
        private Neighborhood neighborhood;
        /**
         * 楼信息
         */
        private Building building;
    }

    /**
     * 社区信息
     */
    @Data
    public static class Neighborhood implements Serializable {
        /**
         * 社区名称
         */
        private String name;
        /**
         * 社区类型
         */
        private String type;
    }

    /**
     * 楼信息
     */
    @Data
    public static class Building implements Serializable {
        /**
         * 楼名称
         */
        private String name;
        /**
         * 楼类型
         */
        private String type;
    }
}
