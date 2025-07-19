package com.gls.athena.starter.jasper.annotation;

import com.gls.athena.starter.jasper.generator.JasperGenerator;
import com.gls.athena.starter.web.enums.FileEnums;

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
     * 文件类型
     */
    FileEnums fileType() default FileEnums.PDF;

    /**
     * 模板名
     */
    String template() default "";

    /**
     * 生成器
     */
    Class<? extends JasperGenerator> generator() default JasperGenerator.class;

}
