package com.gls.athena.starter.pdf.annotation;

import com.gls.athena.starter.pdf.generator.PdfGenerator;

import java.lang.annotation.*;

/**
 * 将响应转换为PDF文档的注解
 *
 * @author athena
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PdfResponse {

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
    Class<? extends PdfGenerator> generator() default PdfGenerator.class;
}
