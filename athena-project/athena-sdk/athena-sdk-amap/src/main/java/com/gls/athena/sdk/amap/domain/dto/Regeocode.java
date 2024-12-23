package com.gls.athena.sdk.amap.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 逆地理编码
 *
 * @author george
 */
@Data
public class Regeocode implements Serializable {
    /**
     * 商圈信息列表
     */
    private String formatted_address;
    /**
     * 地址元素列表
     */
    private AddressComponent addressComponent;
    /**
     * poi 信息列表
     */
    private List<Poi> pois;
    /**
     * 道路信息列表
     */
    private List<Road> roads;
    /**
     * 道路交叉口列表
     */
    private List<Roadinter> roadinters;
    /**
     * aoi 信息列表
     */
    private List<Aoi> aois;

}
