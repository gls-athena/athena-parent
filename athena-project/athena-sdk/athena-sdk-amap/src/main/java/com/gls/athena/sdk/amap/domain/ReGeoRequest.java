package com.gls.athena.sdk.amap.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 逆地理编码请求
 *
 * @author george
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ReGeoRequest extends BaseRequest {
    /**
     * 经纬度坐标
     * 传入内容规则：经度在前，纬度在后，经纬度间以“,”分割，经纬度小数点后不要超过 6 位。
     */
    private String location;
    /**
     * 返回附近 POI 类型
     * 以下内容需要 extensions 参数为 all 时才生效。
     * 逆地理编码在进行坐标解析之后不仅可以返回地址描述，也可以返回经纬度附近符合限定要求的 POI 内容（在 extensions 字段值为 all 时才会返回 POI 内容）。设置 POI 类型参数相当于为上述操作限定要求。参数仅支持传入 POI TYPECODE，可以传入多个 POI TYPECODE，相互之间用“|”分隔。获取 POI TYPECODE 可以参考 POI 分类码表。
     */
    private String poitype;
    /**
     * 搜索半径
     * radius 取值范围：0~3000，默认值：1000。单位：米
     */
    private Integer radius = 1000;
    /**
     * 返回结果控制
     * extensions 参数默认取值是 base，也就是返回基本地址信息；
     * extensions 参数取值为 all 时会返回基本地址信息、附近 POI 内容、道路信息以及道路交叉口信息。
     */
    private String extensions = "all";
    /**
     * 道路等级
     * 以下内容需要 extensions 参数为 all 时才生效。
     * 可选值：0，1  当 roadlevel=0时，显示所有道路 ； 当 roadlevel=1时，过滤非主干道路，仅输出主干道路数据
     */
    private String roadlevel = "0";
    /**
     * 是否优化 POI 返回顺序
     * 以下内容需要 extensions 参数为 all 时才生效。
     * homeorcorp 参数的设置可以影响召回 POI 内容的排序策略，目前提供三个可选参数：
     * 0：不对召回的排序策略进行干扰。
     * 1：综合大数据分析将居家相关的 POI 内容优先返回，即优化返回结果中 pois 字段的poi 顺序。
     * 2：综合大数据分析将公司相关的 POI 内容优先返回，即优化返回结果中 pois 字段的poi 顺序。
     */
    private String homeorcorp = "0";
}
