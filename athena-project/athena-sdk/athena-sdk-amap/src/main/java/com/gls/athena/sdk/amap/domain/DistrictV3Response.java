package com.gls.athena.sdk.amap.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 行政区域查询响应
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DistrictV3Response extends BaseV3Response {
    /**
     * 返回结果总数目
     */
    private String count;

    /**
     * 建议结果列表
     */
    private Suggestion suggestion;

    /**
     * 行政区列表
     */
    private List<District> districts;

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
    public static class District {
        /**
         * 城市编码，如果当前为省级，则为空数组
         */
        private String citycode;

        /**
         * 区域编码
         */
        private String adcode;

        /**
         * 行政区名称
         */
        private String name;

        /**
         * 行政区边界坐标点，当一个行政区范围，由完全分隔两块或者多块的地块组成，每块地的polyline坐标串以"|"分隔
         */
        private String polyline;

        /**
         * 区域中心点坐标
         * 注：对于乡镇级别，此项为空
         */
        private String center;

        /**
         * 行政区划级别
         * country:国家
         * province:省份
         * city:市
         * district:区县
         * street:街道
         */
        private String level;

        /**
         * 下级行政区列表
         */
        private List<District> districts;
    }
}
