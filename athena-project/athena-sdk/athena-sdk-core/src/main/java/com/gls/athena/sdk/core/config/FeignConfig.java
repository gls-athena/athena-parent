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
     *
     * @return RequestInterceptor
     */
    @Bean
    public RequestInterceptor feignRequestInterceptor() {
        return template -> template.header(IConstants.CLIENT_TYPE, ClientTypeEnums.FEIGN.getCode());
    }
}
