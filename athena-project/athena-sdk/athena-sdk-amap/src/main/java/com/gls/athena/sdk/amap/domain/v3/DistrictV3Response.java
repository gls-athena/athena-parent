package com.gls.athena.sdk.amap.domain.v3;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 高德地图行政区域查询接口（V3版本）响应对象
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DistrictV3Response extends BaseV3Response {
    /**
     * 查询结果总数
     * 返回查询结果的总数量
     */
    private String count;

    /**
     * 查询建议对象
     * 包含关键字和城市的建议信息
     */
    private Suggestion suggestion;

    /**
     * 行政区域信息列表
     * 包含查询到的所有行政区域详细信息
     */
    private List<District> districts;

    @Data
    public static class Suggestion {
        /**
         * 建议关键字列表
         * 用于辅助优化搜索的关键字推荐
         */
        private String keywords;

        /**
         * 建议城市列表
         * 匹配到的相关城市推荐
         */
        private String cities;
    }

    @Data
    public static class District {
        /**
         * 城市编码
         * 城市的唯一标识编码，省级行政区划此字段为空
         */
        private String citycode;

        /**
         * 区域编码
         * 行政区划的唯一标识编码（六位数字）
         */
        private String adcode;

        /**
         * 行政区划名称
         * 行政区的标准名称
         */
        private String name;

        /**
         * 行政区边界坐标点
         * 行政区域的边界坐标点集合，多个地块间以"|"分隔
         * 格式：经度,纬度|经度,纬度...
         */
        private String polyline;

        /**
         * 区域中心点坐标
         * 行政区域的中心点坐标，格式：经度,纬度
         * 注：乡镇级别行政区划此字段为空
         */
        private String center;

        /**
         * 行政区划级别
         * 可能值：
         * - country：国家级
         * - province：省级
         * - city：市级
         * - district：区县级
         * - street：街道级
         */
        private String level;

        /**
         * 下级行政区划列表
         * 包含该行政区划下所有下级行政区域信息
         */
        private List<District> districts;
    }
}
