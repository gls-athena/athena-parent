package com.gls.athena.sdk.amap.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 高德地图公交路径规划 V5 版本请求对象
 * 用于查询公共交通换乘方案，支持跨城市公交路径规划
 *
 * @author george
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class TransitIntegratedV5Request extends BaseV3Request {

    /**
     * 出发点坐标
     * 格式：经度,纬度（小数点后不超过6位）
     * 示例：116.434307,39.90909
     */
    private String origin;

    /**
     * 目的地坐标
     * 格式：经度,纬度（小数点后不超过6位）
     * 示例：116.434307,39.90909
     */
    private String destination;

    /**
     * 起点POI的ID
     * 可通过POI搜索接口获取，作为起点的参考位置
     */
    private String originpoi;

    /**
     * 终点POI的ID
     * 可通过POI搜索接口获取，作为终点的参考位置
     */
    private String destinationpoi;

    /**
     * 起点所在行政区域编码
     * 六位数字的行政区域编码
     */
    private String ad1;

    /**
     * 终点所在行政区域编码
     * 六位数字的行政区域编码
     */
    private String ad2;

    /**
     * 起点所在城市名称或城市编码
     * 支持城市中文、中文全拼或城市编码
     */
    private String city1;

    /**
     * 终点所在城市名称或城市编码
     * 支持城市中文、中文全拼或城市编码
     */
    private String city2;

    /**
     * 公共交通换乘策略
     * 0：最快捷模式
     * 1：最经济模式
     * 2：最少换乘模式
     * 3：最少步行模式
     * 4：最舒适模式
     * 5：不乘地铁模式
     * 默认值：0
     */
    private String strategy = "0";

    /**
     * 返回可选方案的数量
     * 默认返回2条，最多返回5条
     */
    @JsonProperty("AlternativeRoute")
    private String alternativeRoute;

    /**
     * 地铁站点出入口数量
     * 返回地铁站点的出入口数量
     */
    private String multiexport;

    /**
     * 是否考虑夜班车
     * 0：不考虑夜班车
     * 1：考虑夜班车
     * 默认值：0
     */
    private String nightflag = "0";

    /**
     * 出发日期
     * 格式：yyyy-MM-dd
     * 用于未来日期的路径规划
     */
    private String date;

    /**
     * 出发时间
     * 格式：HH:mm
     * 24小时制，用于指定出发时间
     */
    private String time;

    /**
     * 返回结果字段控制
     * 通过该字段控制返回结果中包含的数据项
     * 多个字段用"|"分隔
     */
    @JsonProperty("show_fields")
    private String showFields;
}
