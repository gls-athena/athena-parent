package com.gls.athena.security.servlet.client.wechat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.Arrays;

/**
 * 微信 HTTP 消息转换器
 * <p>
 * 用于处理微信API响应的特殊消息转换器。微信API有时会返回text/plain内容类型，
 * 但实际内容是JSON格式。此转换器通过扩展支持的媒体类型来处理这种情况。
 * </p>
 *
 * @author george
 */
public class WechatHttpMessageConverter extends MappingJackson2HttpMessageConverter {

    /**
     * 默认构造函数
     * 使用默认的Jackson2ObjectMapperBuilder创建ObjectMapper实例
     */
    public WechatHttpMessageConverter() {
        this(Jackson2ObjectMapperBuilder.json().build());
    }

    /**
     * 使用自定义ObjectMapper构造转换器
     *
     * @param objectMapper 自定义的ObjectMapper实例，用于JSON序列化和反序列化
     */
    public WechatHttpMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper);
        // 设置支持的媒体类型，同时支持text/plain和application/json
        setSupportedMediaTypes(Arrays.asList(MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON));
    }
}
