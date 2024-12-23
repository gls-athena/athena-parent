package com.gls.athena.starter.amap.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 步行方案
 *
 * @author george
 */
@Data
public class Path implements Serializable {
    /**
     * 起点和终点的步行距离
     */
    private String distance;
    /**
     * 步行时间预计
     */
    private String duration;
    /**
     * 每段步行方案
     */
    private List<Step> steps;

}
