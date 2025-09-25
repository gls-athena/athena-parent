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
     * 文件名(不含扩展名)
     * 指定导出文件的名称，不包含文件扩展名
     */
    String filename() default "document";

    /**
     * 文件类型
     * 指定导出文件的类型，如PDF、Excel等
     */
    FileTypeEnums fileType() default FileTypeEnums.PDF;

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

    /**
     * 是否异步执行
     * true表示异步生成报表，false表示同步生成
     */
    boolean async() default false;

    /**
     * 代码标识
     * 用于标识该导出功能的唯一代码
     */
    String code() default "jasper_export";

    /**
     * 功能名称
     * 该导出功能的显示名称
     */
    String name() default "Jasper导出";

    /**
     * 功能描述
     * 该导出功能的详细描述信息
     */
    String description() default "Jasper导出";

}
