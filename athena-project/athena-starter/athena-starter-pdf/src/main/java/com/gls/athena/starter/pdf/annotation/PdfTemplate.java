package com.gls.athena.starter.pdf.annotation;

import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.engine.thymeleaf.ThymeleafEngine;

import java.lang.annotation.*;

/**
 * PdfTemplate 注解
 * 用于标记方法，用于标记返回值为PDF的响应
 * 该注解可以用于生成PDF模板
 *
 * @author george
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PdfTemplate {
    /**
     * 模板名称
     */
    String name() default "";

    /**
     * 字符集
     */
    String charset() default "UTF-8";

    /**
     * 模板路径，如果ClassPath或者WebRoot模式，则表示相对路径
     */
    String path() default "templates";

    /**
     * 模板资源加载方式
     */
    TemplateConfig.ResourceMode resourceMode() default TemplateConfig.ResourceMode.CLASSPATH;

    /**
     * 模板引擎
     * 默认使用Thymeleaf引擎
     */
    Class<? extends TemplateEngine> customEngine() default ThymeleafEngine.class;

    /**
     * 是否使用缓存
     */
    boolean useCache() default true;
}
