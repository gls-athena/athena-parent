package com.gls.athena.starter.jasper.annotation;

import com.gls.athena.starter.jasper.config.ReportType;

import java.lang.annotation.*;

/**
 * PdfResponse 注解
 *
 * @author george
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JasperResponse {
    /**
     * 文件名(不含扩展名)
     */
    String filename() default "document";

    /**
     * 模板名
     */
    String template() default "";

    /**
     * 模板类型
     */
    ReportType reportType() default ReportType.HTML;

}
