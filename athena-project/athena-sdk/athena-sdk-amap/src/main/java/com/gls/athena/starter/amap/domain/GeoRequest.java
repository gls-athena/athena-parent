package com.gls.athena.starter.amap.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 地理编码请求
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GeoRequest extends BaseRequest {
    /**
     * 结构化地址信息
     * 规则遵循：国家、省份、城市、区县、城镇、乡村、街道、门牌号码、屋邨、大厦，如：北京市朝阳区阜通东大街6号。
     */
    private String address;
    /**
     * 指定查询的城市
     * 可选输入内容包括：指定城市的中文（如北京）、指定城市的中文全拼（beijing）、citycode（010）、adcode（110000），不支持县级市。当指定城市查询内容为空时，会进行全国范围内的地址转换检索。
     * adcode 信息可参考 城市编码表 获取
     */
    private String city;
}
