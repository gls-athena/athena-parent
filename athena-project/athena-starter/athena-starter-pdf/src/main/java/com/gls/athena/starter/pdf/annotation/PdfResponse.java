package com.gls.athena.starter.pdf.annotation;

import com.gls.athena.starter.pdf.enums.PageOrientation;
import com.gls.athena.starter.pdf.enums.PaperSize;

import java.lang.annotation.*;

/**
 * PdfResponse 注解
 *
 * @author george
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PdfResponse {
    /**
     * 文件名(不含扩展名)
     */
    String filename() default "document";

    /**
     * 模板路径，为空则直接生成PDF
     */
    String template() default "";

    /**
     * 纸张大小
     */
    PaperSize paperSize() default PaperSize.A4;

    /**
     * 页面方向
     */
    PageOrientation orientation() default PageOrientation.PORTRAIT;

    /**
     * 是否显示页码
     */
    boolean showPageNumbers() default true;

}
