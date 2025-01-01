package com.gls.athena.sdk.amap.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 行政区域查询请求
 *
 * @author george
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class DistrictV3Request extends BaseV3Request {
    /**
     * 查询关键字（可选）
     * 规则：只支持单个关键词语搜索关键词支持：行政区名称、citycode、adcode
     * 例如，在subdistrict=2，搜索省份（例如山东），能够显示市（例如济南），区（例如历下区）
     */
    private String keywords;

    /**
     * 子级行政区（可选，默认值：1）
     * 规则：设置显示下级行政区级数（行政区级别包括：国家、省/直辖市、市、区/县、乡镇/街道多级数据）
     * 可选值：0、1、2、3等数字
     * 0：不返回下级行政区
     * 1：返回下一级行政区
     * 2：返回下两级行政区
     * 3：返回下三级行政区
     */
    private Integer subdistrict = 1;

    /**
     * 需要第几页数据（可选，默认值：1）
     * 最外层的districts最多会返回20个数据，若超过限制，请用page请求下一页数据
     * 例如：page=2；page=3
     */
    private Integer page = 1;

    /**
     * 最外层返回数据个数（可选，默认值：20）
     */
    private Integer offset = 20;

    /**
     * 返回结果控制（可选，默认值：base）
     * 此项控制行政区信息中返回行政区边界坐标点
     * base：不返回行政区边界坐标点
     * all：只返回当前查询district的边界值，不返回子节点的边界值
     * 注：目前不能返回乡镇/街道级别的边界值
     */
    private String extensions = "base";

    /**
     * 根据区划过滤（可选）
     * 按照指定行政区划进行过滤，填入后则只返回该省/直辖市信息
     * 需填入adcode，为了保证数据的正确，强烈建议填入此参数
     */
    private String filter;
}
