package com.gls.athena.sdk.amap.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 地理编码响应
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GeoResponse extends BaseResponse {

    private String count;
    private List<Geocode> geocodes;

    @Data
    public static class Geocode implements Serializable {
        private String formatted_address;
        private String country;
        private String province;
        private String citycode;
        private String city;
        private String district;
        private String township;
        private Neighborhood neighborhood;
        private Building building;
        private String adcode;
        private String street;
        private String number;
        private String location;
        private String level;
    }

    @Data
    public static class Neighborhood implements Serializable {
        private String name;
        private String type;
    }

    @Data
    public static class Building implements Serializable {
        private String name;
        private String type;
    }
}
