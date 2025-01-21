package com.gls.athena.sdk.amap.domain.v3;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 高德地图行政区域查询请求实体类
 * 用于封装行政区域查询的各项参数
 *
 * @author george
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class DistrictV3Request extends BaseV3Request {
    /**
     * 查询关键字
     * <p>
     * 支持以下类型的关键字：
     * - 行政区名称（如：北京市、海淀区）
     * - citycode（城市编码）
     * - adcode（行政区编码）
     * <p>
     * 注意：仅支持单个关键词搜索
     */
    private String keywords;

    /**
     * 返回下级行政区级数
     * <p>
     * 可选值说明：
     * - 0：仅返回当前行政区信息
     * - 1：返回当前行政区及其下一级行政区信息（默认值）
     * - 2：返回当前行政区及其下两级行政区信息
     * - 3：返回当前行政区及其下三级行政区信息
     * <p>
     * 行政区级别包含：国家、省/直辖市、市、区/县、乡镇/街道
     */
    private Integer subdistrict = 1;

    /**
     * 分页页码
     * <p>
     * 由于每页最多返回20条数据，当数据超过20条时，
     * 可通过该参数指定获取第几页的数据
     */
    private Integer page = 1;

    /**
     * 每页记录数
     * <p>
     * 最外层districts节点下返回的数据个数
     * 默认值：20
     */
    private Integer offset = 20;

    /**
     * 返回结果控制参数
     * <p>
     * 可选值：
     * - base：不返回行政区边界坐标点（默认值）
     * - all：仅返回当前查询行政区的边界值，不返回子节点边界值
     * <p>
     * 注意：暂不支持返回乡镇/街道级别的边界值
     */
    private String extensions = "base";

    /**
     * 行政区划过滤参数
     * <p>
     * 通过adcode进行过滤，填入后仅返回指定省/直辖市信息
     * 建议：为保证数据准确性，强烈建议填写此参数
     */
    private String filter;
}
