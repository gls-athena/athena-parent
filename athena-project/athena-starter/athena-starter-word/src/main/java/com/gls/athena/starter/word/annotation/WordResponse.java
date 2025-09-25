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
     * 导出任务的编码标识
     * 默认值为"word_export"
     */
    String code() default "word_export";

    /**
     * 导出任务的名称
     * 默认值为"Word导出"
     */
    String name() default "Word导出";

    /**
     * 导出任务的描述信息
     * 默认值为"Word异步导出任务"
     */
    String description() default "Word异步导出任务";

    /**
     * 指定生成的Word文件名(不包含扩展名)
     */
    String filename();

    /**
     * 输出文件的类型
     * 默认值为PDF类型
     */
    FileTypeEnums fileType() default FileTypeEnums.DOCX;

    /**
     * 是否异步生成Word文件
     * 默认值为false，表示同步生成
     */
    boolean async() default false;

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

}
