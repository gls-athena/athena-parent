package com.gls.athena.starter.amap.domain;

import com.gls.athena.starter.amap.domain.dto.Regeocode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 逆地理编码响应
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RegeoResponse extends BaseResponse {
    /**
     * 逆地理编码列表
     */
    private Regeocode regeocode;
}
