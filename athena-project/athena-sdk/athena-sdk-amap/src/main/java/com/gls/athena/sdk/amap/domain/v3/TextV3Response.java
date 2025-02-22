package com.gls.athena.sdk.amap.domain.v3;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 高德地图文本搜索V3响应参数
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TextV3Response extends BaseV3Response {
    /**
     * 建议提示信息
     */
    private Suggestion suggestion;
    /**
     * 返回结果总数目
     */
    private String count;

    /**
     * POI信息列表
     */
    private List<Poi> pois;

    @Data
    public static class Suggestion {
        /**
         * 建议关键字列表
         */
        private String keywords;
        /**
         * 建议城市列表
         */
        private String cities;
    }

    @Data
    public static class Poi {
        /**
         * 父地点POI ID
         */
        private String parent;
        /**
         * 距离中心点的距离
         */
        private String distance;
        /**
         * 省份编码
         */
        private String pcode;
        /**
         * POI权重
         */
        private String importance;
        /**
         * 扩展信息
         */
        @JsonProperty("biz_ext")
        private BizExt bizExt;
        /**
         * 推荐城市代码
         */
        private String recommend;
        /**
         * POI类型
         */
        private String type;
        /**
         * 照片列表
         */
        private List<Photo> photos;
        /**
         * 优惠信息
         */
        @JsonProperty("discount_num")
        private String discountNum;
        /**
         * 地理格栅编码
         */
        private String gridcode;
        /**
         * POI类型编码
         */
        private String typecode;
        /**
         * 商铺信息
         */
        private String shopinfo;
        /**
         * POI权重
         */
        @JsonProperty("poiweight")
        private String poiWeight;
        /**
         * 城市编码
         */
        private String citycode;
        /**
         * 区域名称
         */
        private String adname;
        /**
         * 子场所列表
         */
        private List<Children> children;
        /**
         * 别名
         */
        private String alias;
        /**
         * 电话
         */
        private String tel;
        /**
         * ID
         */
        private String id;
        /**
         * 标签
         */
        private String tag;
        /**
         * 事件
         */
        private String event;
        /**
         * 入口经纬度
         */
        @JsonProperty("entr_location")
        private String entrLocation;
        /**
         * 室内地图标识
         */
        @JsonProperty("indoor_map")
        private String indoorMap;
        /**
         * 邮箱
         */
        private String email;
        /**
         * 营业时间
         */
        private String timestamp;
        /**
         * 官网
         */
        private String website;
        /**
         * 地址
         */
        private String address;
        /**
         * 区域编码
         */
        private String adcode;
        /**
         * 省份名称
         */
        private String pname;
        /**
         * 商业类型
         */
        @JsonProperty("biz_type")
        private String bizType;
        /**
         * 城市名称
         */
        private String cityname;
        /**
         * 邮政编码
         */
        private String postcode;
        /**
         * 匹配级别
         */
        private String match;
        /**
         * 商圈名称
         */
        @JsonProperty("business_area")
        private String businessArea;
        /**
         * 室内信息
         */
        @JsonProperty("indoor_data")
        private IndoorData indoorData;
        /**
         * 子类型
         */
        @JsonProperty("childtype")
        private String childType;
        /**
         * 出口位置
         */
        @JsonProperty("exit_location")
        private String exitLocation;
        /**
         * POI名称
         */
        private String name;
        /**
         * POI经纬度
         */
        private String location;
        /**
         * 商铺id
         */
        @JsonProperty("shopid")
        private String shopId;
        /**
         * 导航POI ID
         */
        @JsonProperty("navi_poiid")
        private String naviPoiId;
        /**
         * 团购数量
         */
        @JsonProperty("groupbuy_num")
        private String groupbuyNum;
    }

    @Data
    public static class BizExt {
        /**
         * 餐饮消费
         */
        private String cost;
        /**
         * 评分
         */
        private String rating;
    }

    @Data
    public static class Photo {
        /**
         * 照片标题
         */
        private String title;
        /**
         * 照片URL
         */
        private String url;
    }

    @Data
    public static class Children {
        /**
         * POI类型编码
         */
        private String typecode;
        /**
         * 地址
         */
        private String address;
        /**
         * 距离
         */
        private String distance;
        /**
         * 子类型
         */
        private String subtype;
        /**
         * 简称
         */
        private String sname;
        /**
         * 名称
         */
        private String name;
        /**
         * 位置
         */
        private String location;
        /**
         * ID
         */
        private String id;
    }

    @Data
    public static class IndoorData {
        /**
         * CMS ID
         */
        private String cmsid;
        /**
         * 真实楼层
         */
        @JsonProperty("truefloor")
        private String trueFloor;
        /**
         * CP ID
         */
        private String cpid;
        /**
         * 楼层
         */
        private String floor;
    }
}
