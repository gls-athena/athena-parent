package com.gls.athena.sdk.amap.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 高德地图距离测量API V3版本响应对象
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DistanceV3Response extends BaseV3Response {
    /**
     * 本次距离测量的结果数量
     */
    private String count;
    /**
     * 距离测量结果集合
     */
    private List<Result> results;

    /**
     * 单次距离测量结果详情
     */
    @Data
    public static class Result implements Serializable {
        /**
         * 起点标识ID，从1开始的序列号
         */
        @JsonProperty("origin_id")
        private String originId;
        /**
         * 终点标识ID，从1开始的序列号
         */
        @JsonProperty("dest_id")
        private String destId;
        /**
         * 起点到终点的路径距离，单位：米
         */
        private String distance;
        /**
         * 预计行驶时间，单位：秒
         * 根据实际路况计算得出
         */
        private String duration;
        /**
         * 错误信息描述
         * 仅在请求出现错误时返回，可用于判断单次测量是否成功
         */
        private String info;
        /**
         * 错误码
         * 仅在请求出现错误时返回，驾车模式下的错误类型：
         * 1. 指定地点间无可行驶道路
         * 2. 起点/终点与道路距离过远（如位于海洋或矿区）
         * 3. 起点/终点不在中国境内
         */
        private String code;
    }
}
