package com.gls.athena.sdk.amap.domain;

import com.gls.athena.sdk.amap.domain.dto.Route;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 步行路径规划响应
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WalkingResponse extends BaseResponse {
    /**
     * 返回结果总数目
     */
    private String count;
    /**
     * 路径规划方案
     */
    private Route route;
}
