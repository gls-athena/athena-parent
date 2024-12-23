package com.gls.athena.sdk.amap.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 商圈信息
 *
 * @author george
 */
@Data
public class BusinessArea implements Serializable {
    /**
     * 商圈中心点经纬度
     */
    private String location;
    /**
     * 商圈名称
     */
    private String name;
    /**
     * 商圈所在区域的 adcode
     */
    private String id;
}
