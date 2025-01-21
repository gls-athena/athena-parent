package com.gls.athena.sdk.amap.domain.v3;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 高德地图 V3 版本逆地理编码请求对象
 * 用于将经纬度坐标转换为结构化的地理位置信息
 *
 * @author george
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ReGeoV3Request extends BaseV3Request {
    /**
     * 经纬度坐标
     * 格式要求：
     * - 经度在前，纬度在后
     * - 经纬度用英文逗号","分隔
     * - 经纬度最多保留6位小数
     * 示例：116.481488,39.990464
     */
    private String location;

    /**
     * POI类型过滤条件
     * 注意：仅在 extensions=all 时生效
     * 支持传入多个 POI TYPECODE，使用"|"分隔
     * 具体类型参考高德地图 POI分类码表
     */
    private String poitype;

    /**
     * 搜索半径
     * 单位：米
     * 取值范围：0-3000
     * 默认值：1000
     */
    private Integer radius = 1000;

    /**
     * 返回结果控制
     * 可选值：
     * - base：仅返回基本地址信息
     * - all：返回基本地址信息 + 附近POI + 道路信息 + 道路交叉口信息
     * 默认值：all
     */
    private String extensions = "all";

    /**
     * 道路等级过滤
     * 注意：仅在 extensions=all 时生效
     * 可选值：
     * - 0：显示所有道路（默认）
     * - 1：仅显示主干道路
     */
    private String roadlevel = "0";

    /**
     * POI返回顺序优化
     * 注意：仅在 extensions=all 时生效
     * 可选值：
     * - 0：默认排序，不进行优化
     * - 1：优先返回居家相关POI
     * - 2：优先返回公司相关POI
     */
    private String homeorcorp = "0";
}
