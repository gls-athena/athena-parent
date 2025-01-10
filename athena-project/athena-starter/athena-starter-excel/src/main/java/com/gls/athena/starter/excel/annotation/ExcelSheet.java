package com.gls.athena.starter.excel.annotation;

import java.lang.annotation.*;

/**
 * Excel工作表注解
 * <p>
 * 该注解用于：
 * 1. 标记实体类中需要导出为Excel工作表的方法
 * 2. 配置Excel工作表的基本属性，如工作表名称、序号等
 * 3. 定义工作表中的数据表格结构
 *
 * @author george
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelSheet {

    /**
     * Excel参数配置
     *
     * @return Excel参数对象，默认使用空参数配置
     */
    ExcelParameter parameter() default @ExcelParameter;

    /**
     * 工作表序号
     * <p>
     * 用于指定在Excel文件中的工作表顺序
     *
     * @return 工作表序号，默认为0
     */
    int sheetNo();

    /**
     * 工作表名称
     *
     * @return 工作表名称，默认为"sheet1"
     */
    String sheetName() default "sheet1";

    /**
     * 工作表中的数据表格定义
     * <p>
     * 用于配置工作表中的数据展示结构，可以定义多个表格
     *
     * @return 数据表格配置数组，默认为空数组
     */
    ExcelTable[] tables() default {};
}
