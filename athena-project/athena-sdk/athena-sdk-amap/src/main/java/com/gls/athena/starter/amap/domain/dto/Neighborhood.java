package com.gls.athena.starter.amap.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 社区信息列表
 *
 * @author george
 */
@Data
public class Neighborhood implements Serializable {
    /**
     * 社区名称
     */
    private String name;
    /**
     * POI 类型
     */
    private String type;
}
