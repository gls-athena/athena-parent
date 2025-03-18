package com.gls.athena.sdk.core.config;

import com.gls.athena.common.core.constant.ClientTypeEnums;
import com.gls.athena.common.core.constant.IConstants;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Feign配置
 *
 * @author george
 */
@Configuration
public class FeignConfig {

    /**
     * Feign请求拦截器
     * <p>
     * 该方法用于创建一个Feign请求拦截器，拦截器会在每次Feign请求发送前执行。
     * 拦截器的主要作用是为请求添加一个自定义的请求头，该请求头包含客户端类型信息。
     *
     * @return RequestInterceptor 返回一个实现了RequestInterceptor接口的实例，该实例会在请求发送前添加指定的请求头。
     */
    @Bean
    public RequestInterceptor feignRequestInterceptor() {
        // 返回一个Lambda表达式，该表达式实现了RequestInterceptor接口的apply方法
        // 在apply方法中，为请求模板添加了一个自定义的请求头，请求头的键为IConstants.CLIENT_TYPE，值为ClientTypeEnums.FEIGN.getCode()
        return template -> template.header(IConstants.CLIENT_TYPE, ClientTypeEnums.FEIGN.getCode());
    }

}
