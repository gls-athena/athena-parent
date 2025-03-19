package com.gls.athena.starter.web.config;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * WebMvc配置
 *
 * @author george
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * Jackson2对象映射构建器
     */
    @Resource
    private Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder;

    /**
     * 增加GET请求参数中的时间类型转换处理
     * <p>
     * 本方法通过配置DateTimeFormatterRegistrar实现以下功能：
     * 1. 设置标准化时间格式（HH:mm:ss）
     * 2. 设置标准化日期格式（yyyy-MM-dd）
     * 3. 设置标准化日期时间格式（yyyy-MM-dd HH:mm:ss）
     * 最终将配置好的格式化器注册到Spring的格式化系统中
     *
     * @param registry 格式化器注册器，用于接收和存储自定义格式化规则，
     *                 需实现FormatterRegistry接口，通常由Spring框架提供
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        // 初始化日期时间格式注册器
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();

        // 配置三组标准时间格式（时间/日期/日期时间）
        registrar.setTimeFormatter(DatePattern.NORM_TIME_FORMATTER);
        registrar.setDateFormatter(DatePattern.NORM_DATE_FORMATTER);
        registrar.setDateTimeFormatter(DatePattern.NORM_DATETIME_FORMATTER);

        // 将配置好的格式化规则注入Spring框架
        registrar.registerFormatters(registry);
    }

    /**
     * 配置消息转换器
     * <p>
     * 功能说明：
     * 1. 移除默认Jackson转换器防止冲突
     * 2. 自定义Long类型序列化规则，解决JS处理长整型精度丢失问题
     * 3. 创建定制化的Jackson消息转换器并设置UTF-8编码
     *
     * @param converters 消息转换器列表，框架默认加载的转换器集合（包含MappingJackson2HttpMessageConverter等）
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 清理默认的Jackson转换器（根据知识库CSDN博客建议，避免框架默认转换器干扰）
        converters.removeIf(converter -> converter instanceof AbstractJackson2HttpMessageConverter);

        // 创建Long类型序列化模块（根据知识库长数字处理需求，将Long转为String类型）
        SimpleModule simpleModule = new SimpleModule();
        // 同时处理包装类型和基本类型
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);

        // 构建定制化的ObjectMapper（参考知识库Jackson配置建议）
        ObjectMapper objectMapper = jackson2ObjectMapperBuilder.build();
        objectMapper.registerModule(simpleModule);
        // 增强安全性：禁用默认类型推导（防止JSON反序列化漏洞）
        objectMapper.deactivateDefaultTyping();

        // 创建并配置自定义转换器（根据知识库HttpMessageConverter配置方式二）
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);
        // 统一设置响应字符编码（确保前端正确解析中文）
        converter.setDefaultCharset(StandardCharsets.UTF_8);

        converters.add(converter);
    }

}
