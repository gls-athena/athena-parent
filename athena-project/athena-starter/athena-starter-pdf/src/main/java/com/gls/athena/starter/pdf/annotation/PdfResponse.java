package com.gls.athena.starter.pdf.annotation;

import com.gls.athena.starter.pdf.generator.PdfGenerator;

import java.lang.annotation.*;

/**
 * 将响应转换为PDF文档的注解
 *
 * @author george
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PdfResponse {

    /**
     * 文档文件名(默认使用当前时间)
     *
     * @return PDF文件名
     */
    String filename() default "";

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
    Class<? extends PdfGenerator> generator() default PdfGenerator.class;

    /**
     * 是否异步生成PDF
     *
     * @return true表示异步生成，false表示同步生成
     */
    boolean async() default false;

    /**
     * PDF导出编码
     *
     * @return 导出功能编码
     */
    String code() default "pdf_export";

    /**
     * PDF导出名称
     *
     * @return 导出功能名称
     */
    String name() default "PDF导出";

    /**
     * PDF导出描述
     *
     * @return 导出功能描述信息
     */
    String description() default "PDF导出";
}

