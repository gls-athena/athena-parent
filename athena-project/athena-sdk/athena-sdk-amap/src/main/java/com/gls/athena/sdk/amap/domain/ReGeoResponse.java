package com.gls.athena.sdk.amap.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 逆地理编码响应
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ReGeoResponse extends BaseResponse {

    private ReGeocode regeocode;

    @Data
    public static class ReGeocode implements Serializable {
        private List<Road> roads;
        private List<RoadInter> roadinters;
        private String formatted_address;
        private AddressComponent addressComponent;
        private List<Aoi> aois;
        private List<Poi> pois;
    }

    @Data
    public static class Road implements Serializable {
        private String id;
        private String location;
        private String direction;
        private String name;
        private String distance;
    }

    @Data
    public static class RoadInter implements Serializable {
        private String second_name;
        private String first_id;
        private String second_id;
        private String location;
        private String distance;
        private String first_name;
        private String direction;
    }

    @Data
    public static class AddressComponent implements Serializable {
        private String city;
        private String province;
        private String adcode;
        private String district;
        private String towncode;
        private StreetNumber streetNumber;
        private String country;
        private String township;
        private List<BusinessArea> businessAreas;
        private Building building;
        private Neighborhood neighborhood;
        private String citycode;
    }

    @Data
    public static class StreetNumber implements Serializable {
        private String number;
        private String location;
        private String direction;
        private String distance;
        private String street;
    }

    @Data
    public static class BusinessArea implements Serializable {
        private String location;
        private String name;
        private String id;
    }

    @Data
    public static class Aoi implements Serializable {
        private String area;
        private String type;
        private String id;
        private String location;
        private String adcode;
        private String name;
        private String distance;
    }

    @Data
    public static class Poi implements Serializable {
        private String id;
        private String direction;
        private String businessarea;
        private String address;
        private String poiweight;
        private String name;
        private String location;
        private String distance;
        private String tel;
        private String type;
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
