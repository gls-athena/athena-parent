package com.gls.athena.sdk.amap.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 高德地图天气查询响应实体类
 *
 * <p>用于封装高德地图天气查询 API 的响应数据。继承自 {@link BaseV3Response}，
 * 包含实况天气数据和天气预报数据。根据请求参数的不同，{@code lives} 和 {@code forecasts}
 * 字段会有选择性返回。</p>
 *
 * @author george
 * @see BaseV3Response
 * @see WeatherV3Request
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WeatherV3Response extends BaseV3Response {
    /**
     * 返回结果总数目
     * <p>表示本次查询返回的天气数据条数</p>
     */
    private String count;

    /**
     * 实况天气数据信息列表
     * <p>当请求参数 extensions=base 时返回实况天气数据</p>
     */
    private List<Live> lives;

    /**
     * 预报天气数据信息列表
     * <p>当请求参数 extensions=all 时返回天气预报数据</p>
     */
    private List<Forecast> forecasts;

    /**
     * 实况天气数据信息
     *
     * <p>包含某个城市当前的天气现象、温度、湿度、风向等实时气象数据</p>
     */
    @Data
    public static class Live implements Serializable {
        /**
         * 省份名
         * <p>例如：北京市</p>
         */
        private String province;

        /**
         * 城市名
         * <p>例如：北京市</p>
         */
        private String city;

        /**
         * 区域编码
         * <p>高德地图行政区划编码，例如：110000</p>
         */
        private String adcode;

        /**
         * 天气现象（汉字描述）
         * <p>例如：晴、多云、阴、小雨等</p>
         */
        private String weather;

        /**
         * 实时气温
         * <p>单位：摄氏度，整数值</p>
         */
        private String temperature;

        /**
         * 风向描述
         * <p>例如：东北风、西南风等</p>
         */
        private String winddirection;

        /**
         * 风力级别
         * <p>单位：级，例如：3级、4级等</p>
         */
        private String windpower;

        /**
         * 空气湿度
         * <p>单位：百分比，整数值</p>
         */
        private String humidity;

        /**
         * 数据发布时间
         * <p>格式：yyyy-MM-dd HH:mm:ss</p>
         */
        private String reporttime;

        /**
         * 实时气温浮点数
         * <p>单位：摄氏度，精确到小数点后一位</p>
         */
        @JsonProperty("temperature_float")
        private String temperatureFloat;

        /**
         * 空气湿度浮点数
         * <p>单位：百分比，精确到小数点后一位</p>
         */
        @JsonProperty("humidity_float")
        private String humidityFloat;
    }

    /**
     * 天气预报数据信息
     *
     * <p>包含某个城市未来几天的天气预报信息，包括天气现象、温度、风向等预报数据</p>
     */
    @Data
    public static class Forecast implements Serializable {
        /**
         * 城市名称
         * <p>例如：北京市</p>
         */
        private String city;

        /**
         * 区域编码
         * <p>高德地图行政区划编码，例如：110000</p>
         */
        private String adcode;

        /**
         * 省份名
         * <p>例如：北京市</p>
         */
        private String province;

        /**
         * 预报发布时间
         * <p>格式：yyyy-MM-dd HH:mm:ss</p>
         */
        private String reporttime;

        /**
         * 预报数据列表
         * <p>包含未来几天的天气预报数据，通常为4天的预报数据</p>
         */
        private List<Cast> casts;
    }

    /**
     * 具体某一天的天气预报数据
     *
     * <p>包含某一天的白天和夜间天气预报详细信息，包括天气现象、温度、风向等</p>
     */
    @Data
    public static class Cast implements Serializable {
        /**
         * 预报日期
         * <p>格式：yyyy-MM-dd</p>
         */
        private String date;

        /**
         * 星期几
         * <p>1-7 分别代表周一至周日</p>
         */
        private String week;

        /**
         * 白天天气现象
         * <p>例如：晴、多云、阴、小雨等</p>
         */
        @JsonProperty("dayweather")
        private String dayWeather;

        /**
         * 晚上天气现象
         * <p>例如：晴、多云、阴、小雨等</p>
         */
        @JsonProperty("nightweather")
        private String nightWeather;

        /**
         * 白天温度
         * <p>单位：摄氏度，整数值</p>
         */
        @JsonProperty("daytemp")
        private String dayTemp;

        /**
         * 晚上温度
         * <p>单位：摄氏度，整数值</p>
         */
        @JsonProperty("nighttemp")
        private String nightTemp;

        /**
         * 白天风向
         * <p>例如：东北风、西南风等</p>
         */
        @JsonProperty("daywind")
        private String dayWind;

        /**
         * 晚上风向
         * <p>例如：东北风、西南风等</p>
         */
        @JsonProperty("nightwind")
        private String nightWind;

        /**
         * 白天风力
         * <p>单位：级，例如：3级、4级等</p>
         */
        @JsonProperty("daypower")
        private String dayPower;

        /**
         * 晚上风力
         * <p>单位：级，例如：3级、4级等</p>
         */
        @JsonProperty("nightpower")
        private String nightPower;

        /**
         * 白天温度浮点数
         * <p>单位：摄氏度，精确到小数点后一位</p>
         */
        @JsonProperty("daytemp_float")
        private String dayTempFloat;

        /**
         * 晚上温度浮点数
         * <p>单位：摄氏度，精确到小数点后一位</p>
         */
        @JsonProperty("nighttemp_float")
        private String nightTempFloat;
    }
}
