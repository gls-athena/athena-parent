package com.gls.athena.sdk.amap.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 高德地图地理编码API V3版本响应实体
 * 用于封装地理编码服务的响应数据，包含地址解析的详细信息
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GeoV3Response extends BaseV3Response {
    /**
     * 返回结果数目
     * 标识本次地理编码检索返回的地址数量
     */
    private String count;

    /**
     * 地理编码信息列表
     * 包含所有解析后的地理编码结果
     */
    private List<Geocode> geocodes;

    /**
     * 地理编码信息实体类
     * 封装单个地址的地理编码详细信息，包括结构化地址、行政区划、坐标等
     */
    @Data
    public static class Geocode {
        /**
         * 结构化地址信息
         * 规范化的地址字符串，包含省市区街道等信息
         */
        @JsonProperty("formatted_address")
        private String formattedAddress;

        /**
         * 国家名称
         * 地址所在的国家，默认为中国
         */
        private String country;

        /**
         * 省份名称
         * 地址所在的省份、直辖市或自治区
         */
        private String province;

        /**
         * 城市名称
         * 地址所在的地级市
         */
        private String city;

        /**
         * 城市编码
         * 地址所在城市的编码，如：0571
         */
        private String citycode;

        /**
         * 区县名称
         * 地址所在的区、县级行政区划
         */
        private String district;

        /**
         * 街道名称
         * 地址所在的街道或道路名称
         */
        private String street;

        /**
         * 门牌号码
         * 地址的门牌号信息
         */
        private String number;

        /**
         * 行政区划编码
         * 六位数字的行政区划代码，如：330100
         */
        private String adcode;

        /**
         * 经纬度坐标
         * 格式：经度,纬度（116.397428,39.90923）
         */
        private String location;

        /**
         * 匹配级别
         * 返回匹配的精确度级别，如：道路、门牌号等
         */
        private String level;

        /**
         * 乡镇街道名称
         * 地址所在的乡镇、街道级行政区划
         */
        private String township;

        /**
         * 社区信息
         * 包含社区的名称和类型信息
         */
        private Neighborhood neighborhood;

        /**
         * 建筑物信息
         * 包含建筑物的名称和类型信息
         */
        private Building building;
    }

    /**
     * 社区信息实体类
     * 封装社区相关的详细信息
     */
    @Data
    public static class Neighborhood {
        /**
         * 社区名称
         * 例如：望京花园社区
         */
        private String name;

        /**
         * 社区类型
         * 描述社区的类型信息
         */
        private String type;
    }

    /**
     * 建筑物信息实体类
     * 封装建筑物相关的详细信息
     */
    @Data
    public static class Building {
        /**
         * 建筑物名称
         * 例如：望京国际研发园
         */
        private String name;

        /**
         * 建筑物类型
         * 描述建筑物的类型信息
         */
        private String type;
    }
}
