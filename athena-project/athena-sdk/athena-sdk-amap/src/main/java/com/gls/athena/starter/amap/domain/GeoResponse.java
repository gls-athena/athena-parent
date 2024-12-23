package com.gls.athena.starter.amap.domain;

import com.gls.athena.starter.amap.domain.dto.Geocode;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 地理编码响应
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GeoResponse extends BaseResponse {
    /**
     * 返回结果数目
     */
    private String count;
    /**
     * 地理编码信息列表
     */
    private List<Geocode> geocodes = new ArrayList<>();
}
