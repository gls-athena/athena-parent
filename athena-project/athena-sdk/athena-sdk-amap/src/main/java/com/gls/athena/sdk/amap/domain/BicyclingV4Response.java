package com.gls.athena.sdk.amap.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 高德地图骑行路径规划 V4 版本响应实体
 *
 * @author george
 */
@Data
public class BicyclingV4Response implements Serializable {
    /**
     * 响应的主要业务数据
     */
    private Bicycling data;

    /**
     * 返回状态码
     * 值为 0 时表示成功，非 0 表示失败
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

    /**
     * 骑行路径规划的主要业务数据结构
     */
    @Data
    public static class Bicycling implements Serializable {
        /**
         * 起点坐标
         * 格式：经度,纬度
         */
        private String origin;

        /**
         * 终点坐标
         * 格式：经度,纬度
         */
        private String destination;

        /**
         * 骑行路线方案列表
         * 包含多个可选的骑行路线
         */
        private List<Path> paths;
    }

    /**
     * 骑行路线方案详细信息
     */
    @Data
    public static class Path implements Serializable {
        /**
         * 骑行总距离
         * 单位：米
         */
        private Integer distance;

        /**
         * 骑行预计耗时
         * 单位：秒
         */
        private Integer duration;

        /**
         * 骑行路线分段信息
         * 包含每个路段的详细导航信息
         */
        private List<Step> steps;
    }

    /**
     * 骑行路段详细信息
     */
    @Data
    public static class Step implements Serializable {
        /**
         * 路段导航指示
         * 例如："向北骑行100米"
         */
        private String instruction;

        /**
         * 当前路段的道路名称
         */
        private String road;

        /**
         * 当前路段的距离
         * 单位：米
         */
        private Integer distance;

        /**
         * 当前路段的骑行方向
         * 例如：东、南、西、北等
         */
        private String orientation;

        /**
         * 当前路段预计耗时
         * 单位：秒
         */
        private Integer duration;

        /**
         * 当前路段的坐标点序列
         * 格式：经度1,纬度1;经度2,纬度2;...
         */
        private String polyline;

        /**
         * 当前路段的主要导航动作
         * 例如：直行、左转、右转等
         */
        private String action;

        /**
         * 当前路段的辅助导航动作
         * 用于补充说明主要动作
         */
        @JsonProperty("assistant_action")
        private String assistantAction;

        /**
         * 当前路段的骑行类型
         * 0：普通骑行段
         * 1：需要步行段
         * 2：需要推行段
         */
        @JsonProperty("walk_type")
        private Integer walkType;
    }
}
