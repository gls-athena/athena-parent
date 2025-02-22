package com.gls.athena.sdk.amap.domain.v3;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 高德地图周边搜索V3请求参数
 *
 * @author george
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class AroundV3Request extends BaseV3Request {
    /**
     * 中心点坐标(经度和纬度用","分割，经度在前，纬度在后，经纬度小数点后不得超过6位)
     */
    private String location;

    /**
     * 查询关键字(只支持一个关键字)
     */
    private String keywords;

    /**
     * 查询POI类型(多个类型用"|"分割)
     */
    private String types;

    /**
     * 查询城市(可选值：城市中文、中文全拼、citycode、adcode)
     */
    private String city;

    /**
     * 查询半径(取值范围:0-50000,单位：米)
     */
    private Integer radius = 5000;

    /**
     * 排序规则(distance:按距离排序 weight:综合排序)
     */
    private String sortrule = "distance";

    /**
     * 每页记录数据(建议不超过25)
     */
    private Integer offset = 20;

    /**
     * 当前页数
     */
    private Integer page = 1;

    /**
     * 返回结果控制(base:基本地址信息 all:返回地址信息、附近POI、道路以及道路交叉口信息)
     */
    private String extensions = "base";

}
