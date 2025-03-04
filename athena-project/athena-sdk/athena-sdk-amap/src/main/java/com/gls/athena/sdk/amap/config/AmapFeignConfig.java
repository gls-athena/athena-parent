package com.gls.athena.sdk.amap.config;

import com.gls.athena.sdk.amap.support.AmapJsonDecoder;
import com.gls.athena.sdk.amap.support.AmapRequestInterceptor;
import com.gls.athena.sdk.amap.support.JsonQueryMapEncoder;
import feign.QueryMapEncoder;
import feign.RequestInterceptor;
import feign.codec.Decoder;
import org.springframework.context.annotation.Bean;

/**
 * 高德配置
 *
 * @author george
 */
public class AmapFeignConfig {
    /**
     * 高德json解码器
     *
     * @return Decoder
     */
    @Bean
    public Decoder amapJsonDecoder() {
        return new AmapJsonDecoder();
    }

    /**
     * 请求拦截器
     *
     * @param amapProperties 高德配置
     * @return RequestInterceptor
     */
    @Bean
    public RequestInterceptor requestInterceptor(AmapProperties amapProperties) {
        return new AmapRequestInterceptor(amapProperties);
    }

    /**
     * 查询映射编码器
     */
    @Bean
    public QueryMapEncoder queryMapEncoder() {
        return new JsonQueryMapEncoder();
    }
}