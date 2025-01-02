package com.gls.athena.sdk.amap.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 高德地图V3版本逆地理编码响应实体类
 * 用于将经纬度坐标转换为结构化地址信息
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ReGeoV3Response extends BaseV3Response {
    /**
     * 逆地理编码信息
     * 包含地址、道路、POI等详细信息
     */
    private ReGeocode regeocode;

    /**
     * 逆地理编码详细信息实体类
     * 包含结构化地址、地址组成要素、周边道路、POI等信息
     */
    @Data
    public static class ReGeocode implements Serializable {
        /**
         * 结构化地址信息
         * 包含省市区到具体门牌号的完整地址描述
         */
        @JsonProperty("formatted_address")
        private String formattedAddress;

        /**
         * 地址组成要素
         * 包含国家、省份、城市等行政区划信息
         */
        private AddressComponent addressComponent;

        /**
         * 周边道路信息列表
         * 包含道路名称、方位和距离等信息
         */
        private List<Road> roads;

        /**
         * 道路交叉口信息列表
         * 包含交叉路口名称、方位和距离等信息
         */
        private List<RoadInter> roadinters;

        /**
         * 兴趣点信息列表
         * 包含周边POI(Point of Interest)的名称、类型、距离等信息
         */
        private List<Poi> pois;

        /**
         * 区域兴趣点信息列表
         * 包含周边AOI(Area of Interest)的名称、类型、面积等信息
         */
        private List<Aoi> aois;
    }

    /**
     * 地址组成要素实体类
     * 包含从国家到街道等各级行政区划信息
     */
    @Data
    public static class AddressComponent implements Serializable {
        /**
         * 国家名称
         * 例如：中国
         */
        private String country;

        /**
         * 省份名称
         * 例如：北京市、广东省
         */
        private String province;

        /**
         * 城市名称
         * 例如：北京市、深圳市
         */
        private String city;

        /**
         * 城市编码
         * 例如：010、0755
         */
        private String citycode;

        /**
         * 区县名称
         * 例如：海淀区、南山区
         */
        private String district;

        /**
         * 区域编码
         * 六位数字代码，用于唯一标识行政区划
         */
        private String adcode;

        /**
         * 乡镇街道名称
         */
        private String township;

        /**
         * 乡镇街道编码
         */
        private String towncode;

        /**
         * 社区信息
         * 包含社区名称和类型
         */
        private Neighborhood neighborhood;

        /**
         * 建筑物信息
         * 包含建筑物名称和类型
         */
        private Building building;

        /**
         * 门牌信息
         * 包含街道名称、门牌号等信息
         */
        private StreetNumber streetNumber;

        /**
         * 商圈信息列表
         * 包含商圈名称、范围等信息
         */
        private List<BusinessArea> businessAreas;
    }

    /**
     * 社区信息实体类
     * 描述周边社区的基本信息
     */
    @Data
    public static class Neighborhood implements Serializable {
        /**
         * 社区名称
         * 例如：望京社区、中关村社区
         */
        private String name;
        /**
         * 社区类型
         * 例如：住宅区、商业区
         */
        private String type;
    }

    /**
     * 建筑物信息实体类
     * 描述周边建筑物的基本信息
     */
    @Data
    public static class Building implements Serializable {
        /**
         * 建筑物名称
         * 例如：中关村科技大厦、望京SOHO
         */
        private String name;
        /**
         * 建筑物类型
         * 例如：商务写字楼、住宅楼
         */
        private String type;
    }

    /**
     * 门牌信息实体类
     * 描述具体街道门牌号的位置信息
     */
    @Data
    public static class StreetNumber implements Serializable {
        /**
         * 街道名称
         * 例如：朝阳路、中关村大街
         */
        private String street;
        /**
         * 门牌号
         * 例如：甲1号、25号
         */
        private String number;
        /**
         * 门牌号坐标点
         * 格式：经度,纬度
         */
        private String location;
        /**
         * 相对方位
         * 例如：东、南、西、北
         */
        private String direction;
        /**
         * 距离
         * 单位：米
         */
        private String distance;
    }

    /**
     * 商圈信息实体类
     * 描述周边商业区的基本信息
     */
    @Data
    public static class BusinessArea implements Serializable {
        /**
         * 商圈所在区域的编码
         * 六位数字区域编码
         */
        private String id;
        /**
         * 商圈名称
         * 例如：中关村商圈、望京商圈
         */
        private String name;
        /**
         * 商圈中心点坐标
         * 格式：经度,纬度
         */
        private String location;
    }

    /**
     * 道路信息实体类
     * 描述周边道路的基本信息
     */
    @Data
    public static class Road implements Serializable {
        /**
         * 道路唯一标识
         */
        private String id;
        /**
         * 道路名称
         * 例如：北四环路、阜成路
         */
        private String name;
        /**
         * 道路到请求坐标的距离
         * 单位：米
         */
        private String distance;
        /**
         * 道路相对方位
         * 例如：东、南、西、北
         */
        private String direction;
        /**
         * 道路中心点坐标
         * 格式：经度,纬度
         */
        private String location;
    }

    /**
     * 道路交叉口信息实体类
     * 描述道路交叉口的详细信息
     */
    @Data
    public static class RoadInter implements Serializable {
        /**
         * 交叉路口到请求坐标的距离
         * 单位：米
         */
        private String distance;
        /**
         * 交叉路口相对方位
         * 例如：东北、西南
         */
        private String direction;
        /**
         * 交叉路口中心点坐标
         * 格式：经度,纬度
         */
        private String location;
        /**
         * 第一条道路ID
         */
        @JsonProperty("first_id")
        private String firstId;
        /**
         * 第一条道路名称
         */
        @JsonProperty("first_name")
        private String firstName;
        /**
         * 第二条道路ID
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
     * 兴趣点信息实体类
     * 描述周边POI(Point of Interest)的详细信息
     */
    @Data
    public static class Poi implements Serializable {
        /**
         * POI唯一标识
         */
        private String id;
        /**
         * POI名称
         * 例如：北京大学、故宫博物院
         */
        private String name;
        /**
         * POI类型
         * 例如：学校、旅游景点
         */
        private String type;
        /**
         * POI联系电话
         */
        private String tel;
        /**
         * POI到请求坐标的距离
         * 单位：米
         */
        private String distance;
        /**
         * POI相对方位
         * 例如：东、南、西、北
         */
        private String direction;
        /**
         * POI地址
         */
        private String address;
        /**
         * POI中心点坐标
         * 格式：经度,纬度
         */
        private String location;
        /**
         * POI所在商圈
         */
        private String businessarea;
        /**
         * POI权重
         * 反映POI的重要程度
         */
        private String poiweight;
    }

    /**
     * 区域兴趣点信息实体类
     * 描述周边AOI(Area of Interest)的详细信息
     */
    @Data
    public static class Aoi implements Serializable {
        /**
         * AOI唯一标识
         */
        private String id;
        /**
         * AOI名称
         * 例如：奥林匹克公园、中关村软件园
         */
        private String name;
        /**
         * AOI所在区域编码
         * 六位数字区域编码
         */
        private String adcode;
        /**
         * AOI中心点坐标
         * 格式：经度,纬度
         */
        private String location;
        /**
         * AOI面积
         * 单位：平方米
         */
        private String area;
        /**
         * 距离AOI中心点的距离
         * 单位：米
         */
        private String distance;
        /**
         * AOI类型
         * 例如：学校、公园、商场
         */
        private String type;
    }

}
