package com.gls.athena.sdk.amap.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 高德地图文本搜索V3请求参数
 *
 * @author george
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class TextV3Request extends BaseV3Request {

    /**
     * 查询关键字（必填）
     * 规则：只支持一个关键字
     * 若不指定city，并且搜索泛词时，返回城市列表及结果数
     */
    private String keywords;

    /**
     * POI类型（可选）
     * 可选值：分类代码或汉字
     * 多个类型用"|"分隔
     * 默认为120000（商务住宅）&150000（交通设施服务）
     */
    private String types;

    /**
     * 查询城市（可选）
     * 可选值：城市中文、citycode、adcode
     * 如：北京/010/110000
     * 默认全国范围内搜索
     */
    private String city;

    /**
     * 仅返回指定城市数据（可选）
     * 可选值：true/false
     */
    private Boolean citylimit;

    /**
     * 是否按照层级展示子POI数据（可选）
     * 1：子POI归类到父POI中
     * 0：子POI都会显示（默认）
     */
    private String children;

    /**
     * 每页记录数据（可选）
     * 强烈建议不超过25，可能造成访问报错
     * 默认20
     */
    private Integer offset;

    /**
     * 当前页数（可选）
     * 默认1
     */
    private Integer page;

    /**
     * 返回结果控制（可选）
     * base：返回基本地址信息（默认）
     * all：返回地址信息、附近POI、道路以及道路交叉口信息
     */
    private String extensions;

}
