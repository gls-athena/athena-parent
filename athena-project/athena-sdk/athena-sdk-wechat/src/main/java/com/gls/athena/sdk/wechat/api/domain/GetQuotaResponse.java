package com.gls.athena.sdk.wechat.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 查询API调用额度的响应结果，包含接口的配额信息和速率限制信息。
 * 该类用于解析从微信服务器获取的API调用额度信息。
 *
 * @author george
 * @see <a href="https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/openApi-mgnt/getApiQuota.html">接口文档</a>
 */
@Data
public class GetQuotaResponse {
    /**
     * 错误码
     */
    @JsonProperty("errcode")
    private Integer errcode;

    /**
     * 错误信息
     */
    @JsonProperty("errmsg")
    private String errmsg;

    /**
     * 配额信息，包含当天该账号可调用该接口的次数、已经调用的次数和剩余调用次数。
     */
    private Quota quota;

    /**
     * 速率限制信息，包含时间窗口内的调用次数和时间窗口大小。
     */
    @JsonProperty("rate_limit")
    private RateLimit rateLimit;

    /**
     * 第三方平台速率限制信息，包含第三方平台时间窗口内的调用次数和时间窗口大小。
     */
    @JsonProperty("component_rate_limit")
    private ComponentRateLimit componentRateLimit;

    @Data
    public static class Quota {
        /**
         * 当天该账号可调用该接口的总次数。
         */
        @JsonProperty("daily_limit")
        private Long dailyLimit;

        /**
         * 当天已经调用该接口的次数。
         */
        @JsonProperty("used")
        private Long used;

        /**
         * 当天剩余可调用该接口的次数。
         */
        @JsonProperty("remain")
        private Long remain;
    }

    @Data
    public static class RateLimit {
        /**
         * 在指定时间窗口内允许调用该接口的次数。
         * 例如：若refresh_second=5，call_count=10，则表示5秒内可调用10次。
         */
        @JsonProperty("call_count")
        private Long callCount;

        /**
         * 计算调用频率的时间窗口大小，单位为秒。
         */
        @JsonProperty("refresh_second")
        private Long refreshSecond;
    }

    @Data
    public static class ComponentRateLimit {
        /**
         * 在指定时间窗口内第三方平台允许调用该接口的次数。
         * 例如：若refresh_second=5，call_count=10，则表示5秒内可调用10次。
         */
        @JsonProperty("call_count")
        private Long callCount;

        /**
         * 计算第三方平台调用频率的时间窗口大小，单位为秒。
         */
        @JsonProperty("refresh_second")
        private Long refreshSecond;
    }
}
