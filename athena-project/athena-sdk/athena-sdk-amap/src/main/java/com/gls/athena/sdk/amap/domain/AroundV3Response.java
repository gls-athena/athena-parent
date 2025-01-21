package com.gls.athena.sdk.amap.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 高德地图周边搜索V3接口响应实体
 *
 * @author george
 * @see <a href="https://lbs.amap.com/api/webservice/guide/api/search">周边搜索API文档</a>
 */
@Data
public class AroundV3Response {
    /**
     * 搜索建议对象
     */
    private Suggestion suggestion;
    /**
     * 返回结果总数
     */
    private String count;
    /**
     * 返回状态码
     */
    private String infocode;
    /**
     * POI信息列表
     */
    private List<POI> pois;
    /**
     * 返回状态值
     */
    private String status;
    /**
     * 返回结果说明
     */
    private String info;

    /**
     * 搜索建议信息
     */
    @Data
    public static class Suggestion {
        /**
         * 关键字建议列表，若无建议返回[]
         */
        private String keywords;
        /**
         * 城市建议列表，若无建议返回[]
         */
        private String cities;
    }

    /**
     * POI信息类
     */
    @Data
    public static class POI {
        /**
         * 父地点ID，若无返回[]
         */
        private String parent;
        /**
         * 详细地址
         */
        private String address;
        /**
         * 距离中心点距离，单位：米
         */
        private String distance;
        /**
         * 所在省份名称
         */
        private String pname;
        /**
         * 重要性，若无返回[]
         */
        private String importance;
        /**
         * 扩展信息
         */
        @JsonProperty("biz_ext")
        private BizExt bizExt;
        /**
         * 业务类型，若无返回[]
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
         * 图片信息列表
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
         * 权重，若无返回[]
         */
        private String poiweight;
        /**
         * 子类型，若无返回[]
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
         * 电话号码，若无返回[]
         */
        private String tel;
        /**
         * 商铺ID，若无返回[]
         */
        private String shopid;
        /**
         * POI的ID
         */
        private String id;
    }

    /**
     * 扩展业务信息
     */
    @Data
    public static class BizExt {
        /**
         * 费用信息，若无返回[]
         */
        private String cost;
        /**
         * 评分信息，若无返回[]
         */
        private String rating;
    }

    /**
     * POI图片信息
     */
    @Data
    public static class Photo {
        /**
         * 图片标题，若无返回[]
         */
        private String title;
        /**
         * 图片URL
         */
        private String url;
    }
}
