package com.gls.athena.sdk.amap.config;

import com.gls.athena.sdk.amap.support.*;
import feign.QueryMapEncoder;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

/**
 * 高德配置
 *
 * @author george
 */
public class AmapFeignConfig {
    /**
     * 创建一个用于解码高德地图JSON数据的解码器Bean。
     * 该函数返回一个AmapJsonDecoder实例，用于将高德地图的JSON格式数据解码为Java对象。
     *
     * @return Decoder 返回一个实现了Decoder接口的AmapJsonDecoder实例，用于JSON数据的解码。
     */
    @Bean
    public Decoder amapJsonDecoder() {
        return new AmapJsonDecoder();
    }

    /**
     * 创建一个请求拦截器的Bean实例。
     * 该函数根据传入的高德配置（AmapProperties）生成一个AmapRequestInterceptor实例，
     * 并将其作为请求拦截器返回。该拦截器通常用于在请求处理过程中添加或修改请求头、参数等。
     *
     * @param amapProperties 高德地图的配置信息，包含API密钥、服务地址等必要参数。
     * @return RequestInterceptor 返回一个实现了RequestInterceptor接口的AmapRequestInterceptor实例，
     * 用于拦截和处理HTTP请求。
     */
    @Bean
    public RequestInterceptor requestInterceptor(AmapProperties amapProperties) {
        return new AmapRequestInterceptor(amapProperties);
    }

    /**
     * 创建一个QueryMapEncoder的Bean实例。
     * 该函数用于配置并返回一个JsonQueryMapEncoder对象，该对象实现了QueryMapEncoder接口。
     * QueryMapEncoder通常用于将查询参数映射为特定的编码格式，例如JSON。
     *
     * @return QueryMapEncoder 返回一个JsonQueryMapEncoder实例，用于处理查询参数的编码。
     */
    @Bean
    public QueryMapEncoder queryMapEncoder() {
        return new JsonQueryMapEncoder();
    }

    /**
     * 创建高德地图API错误解码器Bean
     * 用于解析高德地图API返回的错误响应，并转换为具体的异常类型
     *
     * @return ErrorDecoder 返回一个AmapErrorDecoder实例，用于错误响应的解码
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        return new AmapErrorDecoder();
    }

    /**
     * 创建高德地图API重试器Bean
     * 用于处理临时性错误的重试逻辑，采用指数退避策略
     * 支持通过配置文件自定义重试参数
     *
     * @param amapProperties 高德地图配置属性，包含重试相关配置
     * @return Retryer 返回一个AmapRetryer实例，用于重试失败的请求
     */
    @Bean
    public Retryer retryer(AmapProperties amapProperties) {
        return new AmapRetryer(amapProperties.getRetry());
    }

}