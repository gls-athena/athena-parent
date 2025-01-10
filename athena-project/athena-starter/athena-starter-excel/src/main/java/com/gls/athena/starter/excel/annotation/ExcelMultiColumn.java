package com.gls.athena.starter.excel.annotation;

import java.lang.annotation.*;

/**
 * Excel多列注解
 * <p>
 * 用于标记实体类中需要映射到Excel多个连续列的字段。
 * 通过指定start和end参数来控制列的范围。
 *
 * @author george
 * @since 1.0.0
 */
@Documented
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelMultiColumn {

    /**
     * 指定多列映射的起始列号
     *
     * @return 起始列号(从0开始)
     */
    int start() default 0;

    /**
     * 指定多列映射的结束列号
     *
     * @return 结束列号(默认为最大整数值)
     */
    int end() default Integer.MAX_VALUE;
}
