package com.gls.athena.sdk.amap.domain.v3;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 高德地图V3版本地理编码请求对象
 * 用于将详细的结构化地址转换为高德经纬度坐标
 *
 * @author george
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class GeoV3Request extends BaseV3Request {
    /**
     * 结构化地址信息
     * 标准格式：国家、省份、城市、区县、城镇、乡村、街道、门牌号码、屋邨、大厦
     * 示例：北京市朝阳区阜通东大街6号
     * 注意：地址越详细，查询的精度越高
     */
    private String address;
    /**
     * 指定查询的城市
     * 支持以下格式：
     * 1. 中文城市名：如"北京"
     * 2. 中文全拼：如"beijing"
     * 3. citycode：如"010"
     * 4. adcode：如"110000"
     * <p>
     * 注意事项：
     * - 不支持县级市
     * - 当city为空时，将进行全国范围内的地址检索
     * - adcode可参考高德开放平台城市编码表
     */
    private String city;
}
