package com.gls.athena.sdk.amap.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * aoi 信息
 *
 * @author george
 */
@Data
public class Aoi implements Serializable {
    /**
     * 所属 aoi 的 id
     */
    private String id;
    /**
     * 所属 aoi 名称
     */
    private String name;
    /**
     * 所属 aoi 所在区域编码
     */
    private String adcode;
    /**
     * 所属 aoi 中心点坐标
     */
    private String location;
    /**
     * 所属 aoi 点面积
     */
    private String area;
    /**
     * 输入经纬度是否在 aoi 面之中
     */
    private String distance;
    /**
     * 所属 aoi 类型
     */
    private String type;
}
