package com.gls.athena.sdk.amap.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 高德地图电动车路径规划V5版本请求对象
 * 用于构建电动车路径规划的API请求参数
 * 继承自BaseV3Request基础请求类
 *
 * @author george
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ElectrobikeV5Request extends BaseV3Request {
    /**
     * 起点经纬度
     * 格式：longitude,latitude
     * 示例：116.481028,39.989643
     */
    private String origin;

    /**
     * 终点经纬度
     * 格式：longitude,latitude
     * 示例：116.481028,39.989643
     */
    private String destination;

    /**
     * 返回结果字段控制
     * 支持以下值：
     * cost - 花费信息
     * navi - 导航信息
     * cities - 途径城市信息
     */
    @JsonProperty("show_fields")
    private String showFields;

    /**
     * 返回方案条数控制
     * 默认1条，最多返回3条
     * 取值范围：1-3
     */
    @JsonProperty("alternative_route")
    private String alternativeRoute;
}
