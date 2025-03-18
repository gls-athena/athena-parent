package com.gls.athena.starter.json.config;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.gls.athena.starter.json.support.DefaultDateFormat;
import com.gls.athena.starter.json.support.GenericExceptionMixin;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Jackson 配置
 *
 * @author george
 */
@Configuration
public class JacksonConfig {

    /**
     * Jackson 全局配置
     *
     * <p>
     * Jackson 是 Spring Boot 默认的 JSON 序列化与反序列化工具。
     * 通过该配置函数，可以对 Jackson 的行为进行全局设置，包括地区、时区、日期格式、未知属性处理以及空值处理等。
     * </p>
     *
     * <p>
     * 该函数返回一个 {@link Jackson2ObjectMapperBuilderCustomizer} 实例，用于自定义 Jackson 的 ObjectMapper 配置。
     * </p>
     *
     * @return {@link Jackson2ObjectMapperBuilderCustomizer} 返回一个自定义的 Jackson 配置器，用于全局配置 Jackson 的行为。
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            // 配置 Jackson 的全局地区为中国
            builder.locale(Locale.CHINA);

            // 配置 Jackson 的全局时区为系统默认时区
            builder.timeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));

            // 配置 Jackson 的全局日期格式为默认日期格式
            builder.dateFormat(new DefaultDateFormat());

            // 配置 Jackson 在反序列化时忽略未知属性，避免抛出异常
            builder.failOnUnknownProperties(false);

            // 配置 Jackson 在序列化时始终包含所有字段，即使字段值为 null
            builder.serializationInclusion(JsonInclude.Include.ALWAYS);
        };
    }

    /**
     * 配置并返回一个 JavaTimeModule 实例，用于处理 Java 8 时间类型的序列化与反序列化规则。
     * 该模块定义了 LocalDateTime、LocalDate 和 LocalTime 的序列化与反序列化格式，
     * 并添加了异常类的序列化与反序列化规则。
     *
     * @return JavaTimeModule 实例，包含配置好的时间序列化与反序列化规则。
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public JavaTimeModule javaTimeModule() {
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        // ======================= 时间序列化规则 ===============================
        // 配置 LocalDateTime 的序列化格式为 "yyyy-MM-dd HH:mm:ss"
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DatePattern.NORM_DATETIME_FORMATTER));
        // 配置 LocalDate 的序列化格式为 "yyyy-MM-dd"
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DatePattern.NORM_DATE_FORMATTER));
        // 配置 LocalTime 的序列化格式为 "HH:mm:ss"
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DatePattern.NORM_TIME_FORMATTER));

        // ======================= 时间反序列化规则 ==============================
        // 配置 LocalDateTime 的反序列化格式为 "yyyy-MM-dd HH:mm:ss"
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DatePattern.NORM_DATETIME_FORMATTER));
        // 配置 LocalDate 的反序列化格式为 "yyyy-MM-dd"
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DatePattern.NORM_DATE_FORMATTER));
        // 配置 LocalTime 的反序列化格式为 "HH:mm:ss"
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DatePattern.NORM_TIME_FORMATTER));

        // ======================= 异常序列化与反序列化规则 ========================
        // 配置 Exception 类的序列化与反序列化规则，使用 GenericExceptionMixin 类来处理
        javaTimeModule.setMixInAnnotation(Exception.class, GenericExceptionMixin.class);

        // 返回配置好的 JavaTimeModule 实例
        return javaTimeModule;
    }

}
