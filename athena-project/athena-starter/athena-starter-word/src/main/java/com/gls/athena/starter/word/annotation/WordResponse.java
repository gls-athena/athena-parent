package com.gls.athena.starter.word.annotation;

import com.gls.athena.starter.web.enums.FileEnums;
import com.gls.athena.starter.word.generator.WordGenerator;

import java.lang.annotation.*;

/**
 * 将响应转换为Word文档的注解
 *
 * @author george
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WordResponse {

    /**
     * 文档文件名
     */
    String filename() default "document.docx";

    /**
     * 文档文件类型(默认docx)
     */
    FileEnums fileType() default FileEnums.DOCX;

    /**
     * 模板路径
     */
    String template() default "";

    /**
     * 生成器类
     */
    Class<? extends WordGenerator> generator() default WordGenerator.class;
}
