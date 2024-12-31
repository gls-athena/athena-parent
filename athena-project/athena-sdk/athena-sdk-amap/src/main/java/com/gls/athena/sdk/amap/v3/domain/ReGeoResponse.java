package com.gls.athena.sdk.amap.v3.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 逆地理编码响应
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ReGeoResponse extends BaseResponse {
    /**
     * 逆地理编码信息
     */
    private ReGeocode regeocode;

    /**
     * 逆地理编码信息
     */
    @Data
    public static class ReGeocode implements Serializable {
        /**
         * 结构化地址信息
         */
        @JsonProperty("formatted_address")
        private String formattedAddress;
        /**
         * 地址元素
         */
        private AddressComponent addressComponent;
        /**
         * 道路信息
         */
        private List<Road> roads;
        /**
         * 道路交叉口信息
         */
        private List<RoadInter> roadinters;
        /**
         * poi 信息
         */
        private List<Poi> pois;
        /**
         * aoi 信息
         */
        private List<Aoi> aois;

    }

    /**
     * 地址元素
     */
    @Data
    public static class AddressComponent implements Serializable {
        /**
         * 国家
         */
        private String country;
        /**
         * 省名
         */
        private String province;
        /**
         * 城市名
         */
        private String city;
        /**
         * 城市编码
         */
        private String citycode;
        /**
         * 区域名称
         */
        private String district;
        /**
         * 区域编码
         */
        private String adcode;
        /**
         * 乡镇街道
         */
        private String township;
        /**
         * 乡镇街道编码
         */
        private String towncode;
        /**
         * 社区
         */
        private Neighborhood neighborhood;
        /**
         * 楼
         */
        private Building building;
        /**
         * 门牌
         */
        private StreetNumber streetNumber;
        /**
         * 商圈列表
         */
        private List<BusinessArea> businessAreas;
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
         * 名称
         */
        private String name;
        /**
         * 类型
         */
        private String type;
    }

    /**
     * 门牌
     */
    @Data
    public static class StreetNumber implements Serializable {
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
         * 方向
         */
        private String direction;
        /**
         * 距离
         */
        private String distance;
    }

    /**
     * 商圈
     */
    @Data
    public static class BusinessArea implements Serializable {
        /**
         * 商圈所在区域的 adcode
         */
        private String id;
        /**
         * 商圈名称
         */
        private String name;
        /**
         * 商圈中心点经纬度
         */
        private String location;

    }

    /**
     * 道路信息
     */
    @Data
    public static class Road implements Serializable {
        /**
         * 道路id
         */
        private String id;
        /**
         * 道路名称
         */
        private String name;
        /**
         * 道路到请求坐标的距离
         */
        private String distance;
        /**
         * 方向
         */
        private String direction;
        /**
         * 坐标点
         */
        private String location;
    }

    /**
     * 道路交叉口信息
     */
    @Data
    public static class RoadInter implements Serializable {
        /**
         * 交叉路口到请求坐标的距离
         */
        private String distance;
        /**
         * 方位
         */
        private String direction;
        /**
         * 交叉路口坐标点
         */
        private String location;
        /**
         * 第一条道路id
         */
        @JsonProperty("first_id")
        private String firstId;
        /**
         * 第一条道路名称
         */
        @JsonProperty("first_name")
        private String firstName;
        /**
         * 第二条道路id
         */
        @JsonProperty("second_id")
        private String secondId;
        /**
         * 第二条道路名称
         */
        @JsonProperty("second_name")
        private String secondName;
    }

    /**
     * poi 信息
     */
    @Data
    public static class Poi implements Serializable {
        /**
         * poi 的 id
         */
        private String id;
        /**
         * poi 名称
         */
        private String name;
        /**
         * poi 类型
         */
        private String type;
        /**
         * poi 电话
         */
        private String tel;
        /**
         * 该 POI 的中心点到请求坐标的距离
         */
        private String distance;
        /**
         * 方向
         */
        private String direction;
        /**
         * poi 地址信息
         */
        private String address;
        /**
         * poi 坐标点
         */
        private String location;
        /**
         * poi 所在商圈名称
         */
        private String businessarea;
        /**
         * poi 所在省
         */
        private String poiweight;
    }

    /**
     * aoi 信息
     */
    @Data
    public static class Aoi implements Serializable {
        /**
         * aoi 的 id
         */
        private String id;
        /**
         * aoi 名称
         */
        private String name;
        /**
         * aoi 所在区域编码
         */
        private String adcode;
        /**
         * aoi 中心点坐标
         */
        private String location;
        /**
         * 所属 aoi 点面积
         */
        private String area;
        /**
         * 输入经纬度是否在 aoi 面之中
         */
        private String distance;
        /**
         * aoi 类型
         */
        private String type;
    }

}
