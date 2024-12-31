package com.gls.athena.sdk.amap.v3.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 公交路径规划请求
 *
 * @author george
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class TransitIntegratedRequest extends BaseRequest {
    /**
     * 出发点
     */
    private String origin;
    /**
     * 目的地
     */
    private String destination;
    /**
     * 城市/跨城规划时的起点城市
     */
    private String city;
    /**
     * 跨城公交规划时的终点城市
     */
    private String cityd;
    /**
     * 返回结果详略
     */
    private String extensions = "all";
    /**
     * 公交换乘策略
     */
    private String strategy = "0";
    /**
     * 是否计算夜班车
     */
    private String nightflag = "0";
    /**
     * 出发日期
     */
    private String date;
    /**
     * 出发时间
     */
    private String time;
}
