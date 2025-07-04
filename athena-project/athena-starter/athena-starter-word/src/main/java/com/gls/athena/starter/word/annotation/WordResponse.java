package com.gls.athena.starter.word.annotation;

import java.lang.annotation.*;

/**
 * 将响应转换为Word文档的注解
 *
 * @author athena
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WordResponse {

    /**
     * 文档文件名(默认使用当前时间)
     */
    String fileName() default "";

    /**
     * 模板路径
     */
    String template() default "";

    /**
     * Word文档标题
     */
    String title() default "";

    /**
     * 是否需要分页
     */
    boolean pagination() default true;

    /**
     * 指定生成器类型
     * 默认值为空，将根据模板和数据类型自动选择生成器
     * 可以指定生成器的类名来强制使用特定的生成器
     */
    Class<?> generator() default void.class;
}
