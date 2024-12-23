package com.gls.athena.starter.amap.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 路线
 *
 * @author george
 */
@Data
public class Route implements Serializable {
    /**
     * 起点坐标
     */
    private String origin;
    /**
     * 终点坐标
     */
    private String destination;
    /**
     * 步行方案
     */
    private List<Path> paths;
}
