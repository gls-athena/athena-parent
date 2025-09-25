package com.gls.athena.starter.jasper.annotation;

import com.gls.athena.common.core.constant.FileTypeEnums;
import com.gls.athena.starter.jasper.generator.JasperGenerator;

import java.lang.annotation.*;

/**
 * JasperResponse 注解
 * 用于标记需要进行Jasper报表生成和导出的方法
 *
 * @author george
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JasperResponse {
    /**
     * 导出任务的编码标识
     * 默认值为"jasper_export"
     */
    String code() default "jasper_export";

    /**
     * 导出任务的名称
     * 默认值为"Jasper导出"
     */
    String name() default "Jasper导出";

    /**
     * 导出任务的描述信息
     * 默认值为"Jasper异步导出任务"
     */
    String description() default "Jasper异步导出任务";

    /**
     * 指定生成的Jasper文件名(不包含扩展名)
     */
    String filename();

    /**
     * 输出文件的类型
     * 默认值为PDF类型
     */
    FileTypeEnums fileType() default FileTypeEnums.PDF;

    /**
     * 是否异步生成Jasper文件
     * 默认值为false，表示同步生成
     */
    boolean async() default false;

    /**
     * 模板名
     * 指定使用的Jasper报表模板名称
     */
    String template() default "";

    /**
     * 生成器
     * 指定用于生成报表的自定义生成器类
     */
    Class<? extends JasperGenerator> generator() default JasperGenerator.class;

}
