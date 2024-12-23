package com.gls.athena.starter.amap.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 步行方案
 *
 * @author george
 */
@Data
public class Step implements Serializable {
    /**
     * 路段步行指示
     */
    private String instruction;
    /**
     * 方向
     */
    private String orientation;
    /**
     * 道路名称
     */
    private String road;
    /**
     * 此路段距离
     */
    private String distance;
    /**
     * 此路段预计步行时间
     */
    private String duration;
    /**
     * 此路段坐标点
     */
    private String polyline;
    /**
     * 步行主要动作
     */
    private String action;
    /**
     * 步行辅助动作
     */
    private String assistant_action;
    /**
     * 这段路是否存在特殊的方式
     */
    private String walk_type;

}
