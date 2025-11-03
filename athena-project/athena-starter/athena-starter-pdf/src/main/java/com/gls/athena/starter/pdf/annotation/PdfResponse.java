package com.gls.athena.starter.pdf.annotation;

import com.gls.athena.common.core.constant.FileTypeEnums;
import com.gls.athena.starter.file.annotation.FileResponse;
import com.gls.athena.starter.pdf.generator.PdfGenerator;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 将响应转换为PDF文档的注解
 *
 * @author george
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@FileResponse(generator = PdfGenerator.class)
public @interface PdfResponse {

    /**
     * 导出任务的编码标识
     * 默认值为"pdf_export"
     */
    @AliasFor(annotation = FileResponse.class, attribute = "code")
    String code() default "pdf_export";

    /**
     * 导出任务的名称
     * 默认值为"Pdf导出"
     */
    @AliasFor(annotation = FileResponse.class, attribute = "name")
    String name() default "Pdf导出";

    /**
     * 导出任务的描述信息
     * 默认值为"Pdf异步导出任务"
     */
    @AliasFor(annotation = FileResponse.class, attribute = "description")
    String description() default "Pdf异步导出任务";

    /**
     * 指定生成的Pdf文件名(不包含扩展名)
     */
    @AliasFor(annotation = FileResponse.class, attribute = "filename")
    String filename();

    /**
     * 输出文件的类型
     * 默认值为PDF类型
     */
    @AliasFor(annotation = FileResponse.class, attribute = "fileType")
    FileTypeEnums fileType() default FileTypeEnums.PDF;

    /**
     * 是否异步生成Pdf文件
     * 默认值为false，表示同步生成
     */
    @AliasFor(annotation = FileResponse.class, attribute = "async")
    boolean async() default false;

    /**
     * 模板路径
     *
     * @return 模板文件路径
     */
    String template() default "";

    /**
     * 生成器类
     *
     * @return PDF生成器实现类
     */
    @AliasFor(annotation = FileResponse.class, attribute = "generator")
    Class<? extends PdfGenerator> generator() default PdfGenerator.class;

}

