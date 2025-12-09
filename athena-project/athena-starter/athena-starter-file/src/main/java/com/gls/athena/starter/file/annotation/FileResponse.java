package com.gls.athena.starter.file.annotation;

import com.gls.athena.common.core.constant.FileTypeEnums;
import com.gls.athena.starter.file.generator.FileGenerator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 文件响应注解，用于标记文件导出相关的方法
 * 该注解定义了文件导出的基本配置信息，包括文件类型、文件名、生成器等
 *
 * @author george
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FileResponse {

    /**
     * 文件响应代码
     *
     * @return 响应代码，默认值为"FILE_RESPONSE"
     */
    String code() default "FILE_RESPONSE";

    /**
     * 文件名称
     *
     * @return 文件显示名称，默认值为"文件导出"
     */
    String name() default "文件导出";

    /**
     * 文件描述信息
     *
     * @return 文件描述，默认值为"文件导出响应"
     */
    String description() default "文件导出响应";

    /**
     * 导出文件的文件名（不包含扩展名）
     *
     * @return 文件名，默认值为"export_file"
     */
    String filename() default "export_file";

    /**
     * 文件类型枚举
     *
     * @return 文件类型，默认值为FileTypeEnums.DEFAULT
     */
    FileTypeEnums fileType() default FileTypeEnums.DEFAULT;

    /**
     * 是否异步处理
     *
     * @return true表示异步处理，false表示同步处理，默认值为false
     */
    boolean async() default false;

    /**
     * 文件生成器类
     *
     * @return 实现FileGenerator接口的生成器类，无默认值，必须指定
     */
    Class<? extends FileGenerator<?>> generator();
}

