package com.gls.athena.sdk.amap.domain;

import lombok.Data;

import java.util.List;

/**
 * 高德地图驾车路径规划V4版本响应实体
 * 用于封装高德地图驾车路径规划API的返回结果
 *
 * @author george
 * @since 1.0.0
 */
@Data
public class DrivingV4Response {
    /**
     * 响应的核心业务数据
     * 包含路径规划的详细信息，如距离和路径点
     */
    private DrivingData data;

    /**
     * 响应状态码
     * 0: 请求成功
     * 非0: 请求失败，具体错误信息参考errdetail字段
     */
    private Integer errcode;

    /**
     * 错误详细信息
     * 当请求失败时，此字段会详细说明具体的错误原因
     */
    private String errdetail;

    /**
     * 返回状态说明
     * 值为 "OK" 时表示请求成功
     */
    private String errmsg;

    /**
     * 扩展字段
     * 用于存储额外的响应信息
     */
    private String ext;

    @Data
    public static class DrivingData {
        /**
         * 行驶总距离
         * 单位：米
         * 表示规划路径的完整距离
         */
        private Double distance;

        /**
         * 路径坐标点列表
         * 按顺序记录了完整的行驶路径
         */
        private List<Point> points;
    }

    @Data
    public static class Point {
        /**
         * 经度坐标
         * 范围：-180 到 180
         */
        private Double x;

        /**
         * 纬度坐标
         * 范围：-90 到 90
         */
        private Double y;
    }
}
