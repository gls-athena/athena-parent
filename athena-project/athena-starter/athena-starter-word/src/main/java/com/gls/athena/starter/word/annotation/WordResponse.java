package com.gls.athena.starter.word.annotation;

import com.gls.athena.common.core.constant.FileTypeEnums;
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
     *
     * @return 文件名，默认为"document.docx"
     */
    String filename() default "document.docx";

    /**
     * 文档文件类型(默认docx)
     *
     * @return 文件类型枚举，默认为DOCX格式
     */
    FileTypeEnums fileType() default FileTypeEnums.DOCX;

    /**
     * 模板路径
     *
     * @return 模板文件路径，默认为空字符串
     */
    String template() default "";

    /**
     * 生成器类
     *
     * @return Word生成器实现类，默认使用WordGenerator类
     */
    Class<? extends WordGenerator> generator() default WordGenerator.class;

    /**
     * 是否异步处理
     *
     * @return true表示异步处理，false表示同步处理，默认为false
     */
    boolean async() default false;

    /**
     * 代码标识
     *
     * @return 代码字符串，默认为空字符串
     */
    String code() default "";

    /**
     * 名称
     *
     * @return 名称字符串，默认为空字符串
     */
    String name() default "";

    /**
     * 描述信息
     *
     * @return 描述字符串，默认为空字符串
     */
    String description() default "";

}
