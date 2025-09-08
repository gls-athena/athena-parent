package com.gls.athena.starter.excel.annotation;

import cn.idev.excel.support.ExcelTypeEnum;
import com.gls.athena.starter.excel.generator.ExcelGenerator;

import java.lang.annotation.*;

/**
 * 用于标记方法将响应以Excel形式返回的注解
 * 提供了一系列配置选项，以定制Excel的生成和响应行为
 *
 * @author george
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelResponse {

    /**
     * 指定Excel的配置信息
     * 默认值为空的ExcelConfig注解，表示使用默认配置
     */
    ExcelConfig config() default @ExcelConfig;

    /**
     * 是否在操作完成后自动关闭流
     * 默认值为true，表示自动关闭
     */
    boolean autoCloseStream() default true;

    /**
     * Excel文件的密码，用于加密文件
     * 默认值为空字符串，表示不加密
     */
    String password() default "";

    /**
     * 是否在内存中生成Excel文件
     * 默认值为false，表示不在内存中生成
     */
    boolean inMemory() default false;

    /**
     * 在出现异常时是否写入Excel
     * 默认值为true，表示即使出现异常也会尝试写入Excel
     */
    boolean writeExcelOnException() default true;

    /**
     * 指定Excel文件的类型
     * 默认值为XLSX类型
     */
    ExcelTypeEnum excelType() default ExcelTypeEnum.XLSX;

    /**
     * 指定Excel文件的字符集
     * 默认值为空字符串，表示使用默认字符集
     */
    String charset() default "";

    /**
     * 是否在Excel文件中包含BOM
     * 默认值为false，表示不包含BOM
     */
    boolean withBom() default false;

    /**
     * 指定Excel模板的路径
     * 默认值为空字符串，表示不使用模板
     */
    String template() default "";

    /**
     * 指定Excel工作表的配置
     * 默认值为一个带有默认值的ExcelSheet注解
     */
    ExcelSheet[] sheets() default @ExcelSheet(sheetNo = 0);

    /**
     * 指定生成的Excel文件名
     */
    String filename();

    /**
     * 是否强制使用InputStream返回Excel文件
     * 默认值为false，表示不强制使用InputStream
     */
    boolean mandatoryUseInputStream() default false;

    /**
     * 指定生成Excel文件的生成器类
     * 默认值为ExcelGenerator.class，表示使用默认的Excel生成器
     */
    Class<? extends ExcelGenerator> generator() default ExcelGenerator.class;

    /**
     * 是否异步生成Excel文件
     * 默认值为false，表示同步生成
     */
    boolean async() default false;
}
