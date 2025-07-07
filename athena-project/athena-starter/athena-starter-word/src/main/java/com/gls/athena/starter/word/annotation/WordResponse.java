package com.gls.athena.starter.word.annotation;

import com.gls.athena.starter.word.generator.WordGenerator;

import java.lang.annotation.*;

/**
 * 将响应转换为Word文档的注解
 *
 * @author athena
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WordResponse {

    /**
     * 文档文件名(默认使用当前时间)
     */
    String fileName() default "";

    /**
     * 模板路径
     */
    String template() default "";

    /**
     * 生成器类
     */
    Class<? extends WordGenerator> generator() default WordGenerator.class;
}
