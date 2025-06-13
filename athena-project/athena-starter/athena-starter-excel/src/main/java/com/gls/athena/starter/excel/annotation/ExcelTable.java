package com.gls.athena.starter.excel.annotation;

import java.lang.annotation.*;

/**
 * Excel表格注解
 * <p>
 * 用于标记实体类中需要进行Excel表格映射的方法。
 * 该注解可以指定表格参数和表格序号，实现Excel数据的精确导入导出。
 *
 * @author george
 * @since 1.0.0
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelTable {

    /**
     * Excel表格参数配置
     * <p>
     * 包含表格的基础配置信息，如表头、单元格样式等
     *
     * @return ExcelParameter对象，默认使用空参数配置
     */
    ExcelConfig config() default @ExcelConfig;

    /**
     * Excel表格序号
     * <p>
     * 用于标识同一个实体类中多个表格的顺序
     *
     * @return 表格序号，默认值为0
     */
    int tableNo();
}
