package com.gls.athena.sdk.amap.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 楼信息列表
 *
 * @author george
 */
@Data
public class Building implements Serializable {
    /**
     * 建筑名称
     */
    private String name;
    /**
     * 类型
     */
    private String type;
}
