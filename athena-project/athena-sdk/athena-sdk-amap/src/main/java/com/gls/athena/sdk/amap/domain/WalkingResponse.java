package com.gls.athena.sdk.amap.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 步行路径规划响应
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WalkingResponse extends BaseResponse {

    private String count;
    private Route route;

    @Data
    public static class Route implements Serializable {
        private String origin;
        private String destination;
        private List<Path> paths;

    }

    @Data
    public static class Path implements Serializable {
        private String distance;
        private String duration;
        private List<Step> steps;

    }

    @Data
    public static class Step implements Serializable {
        private String instruction;
        private String orientation;
        private String road;
        private String distance;
        private String duration;
        private String polyline;
        private String action;
        private String assistant_action;
        private String walk_type;
    }

}
