package com.gls.athena.sdk.amap.domain.v3;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 高德地图多边形搜索V3响应参数
 *
 * @author george
 */
@Data
public class PolygonV3Response {
    /**
     * 建议检索关键词
     */
    private Suggestion suggestion;
    /**
     * 返回结果数目
     */
    private String count;
    /**
     * 返回状态码
     */
    private String infocode;
    /**
     * POI信息列表
     */
    private List<Poi> pois;
    /**
     * 返回结果状态值
     */
    private String status;
    /**
     * 返回结果描述
     */
    private String info;

    /**
     * 建议检索关键词结构
     */
    @Data
    public static class Suggestion {
        /**
         * 关键词建议列表
         */
        private List<String> keywords;
        /**
         * 城市建议列表
         */
        private List<String> cities;
    }

    /**
     * POI信息结构
     */
    @Data
    public static class Poi {
        /**
         * 父地点POI ID
         */
        private String parent;
        /**
         * POI地址信息
         */
        private String address;
        /**
         * 距离
         */
        private String distance;
        /**
         * 所在省份名称
         */
        private String pname;
        /**
         * 重要性
         */
        private String importance;
        /**
         * 商业扩展信息
         */
        @JsonProperty("biz_ext")
        private BizExt bizExt;
        /**
         * 营业类型
         */
        @JsonProperty("biz_type")
        private String bizType;
        /**
         * 所在城市名称
         */
        private String cityname;
        /**
         * POI类型
         */
        private String type;
        /**
         * POI图片列表
         */
        private List<Photo> photos;
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
        private String poiweight;
        /**
         * 子类型
         */
        private String childtype;
        /**
         * 所在区域名称
         */
        private String adname;
        /**
         * POI名称
         */
        private String name;
        /**
         * 经纬度坐标
         */
        private String location;
        /**
         * 电话号码
         */
        private String tel;
        /**
         * 商铺ID
         */
        private String shopid;
        /**
         * POI ID
         */
        private String id;
    }

    /**
     * 商业扩展信息结构
     */
    @Data
    public static class BizExt {
        /**
         * 人均消费
         */
        private String cost;
        /**
         * 营业时间2
         */
        private String opentime2;
        /**
         * 评分
         */
        private String rating;
        /**
         * 营业时间
         */
        @JsonProperty("open_time")
        private String openTime;
        /**
         * 是否支持订餐
         */
        @JsonProperty("meal_ordering")
        private String mealOrdering;
    }

    /**
     * POI图片信息结构
     */
    @Data
    public static class Photo {
        /**
         * 图片标题
         */
        private String title;
        /**
         * 图片URL
         */
        private String url;
    }
}
