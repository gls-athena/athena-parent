package com.gls.athena.sdk.amap.domain;

import com.gls.athena.sdk.amap.domain.dto.Regeocode;
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
