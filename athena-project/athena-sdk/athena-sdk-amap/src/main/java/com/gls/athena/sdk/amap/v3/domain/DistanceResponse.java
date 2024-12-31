package com.gls.athena.sdk.amap.v3.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 距离测量响应
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DistanceResponse extends BaseResponse {
    /**
     * 距离测量结果数目
     */
    private String count;
    /**
     * 距离测量结果列表
     */
    private List<Result> results;

    /**
     * 距离测量结果
     */
    @Data
    public static class Result implements Serializable {
        /**
         * 起点坐标，起点坐标序列号（从１开始）
         */
        @JsonProperty("origin_id")
        private String originId;
        /**
         * 终点坐标，终点坐标序列号（从１开始）
         */
        @JsonProperty("dest_id")
        private String destId;
        /**
         * 路径距离，单位：米
         */
        private String distance;
        /**
         * 预计行驶时间，单位：秒
         */
        private String duration;
        /**
         * 仅在出错的时候显示该字段。大部分显示“未知错误”
         * 由于此接口支持批量请求，建议不论批量与否用此字段判断请求是否成功
         */
        private String info;
        /**
         * 仅在出错的时候显示此字段。
         * 在驾车模式下：
         * 1，指定地点之间没有可以行车的道路
         * 2，起点/终点 距离所有道路均距离过远（例如在海洋/矿业）
         * 3，起点/终点不在中国境内
         */
        private String code;
    }

}
