package com.gls.athena.starter.word.annotation;

import java.lang.annotation.*;

/**
 * WordResponse 注解
 *
 * @author george
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface WordResponse {
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
    TemplateType templateType() default TemplateType.DOCX;
}
