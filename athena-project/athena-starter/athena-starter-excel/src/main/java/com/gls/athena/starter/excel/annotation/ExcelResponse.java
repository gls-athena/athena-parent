package com.gls.athena.starter.excel.annotation;

import cn.idev.excel.support.ExcelTypeEnum;

import java.lang.annotation.*;

/**
 * Excel响应注解
 * <p>
 * 该注解用于标记需要返回Excel文件的Controller方法。
 * 提供了一系列Excel导出相关的配置选项，包括文件格式、密码保护、
 * 内存模式等特性。
 *
 * @author george
 * @since 1.0.0
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelResponse {

    /**
     * Excel参数配置
     *
     * @return Excel基础参数配置对象
     */
    ExcelConfig config() default @ExcelConfig;

    /**
     * 是否自动关闭输出流
     * <p>
     * 设置为true时会在响应完成后自动关闭流资源
     *
     * @return true表示自动关闭，false需手动处理
     */
    boolean autoCloseStream() default true;

    /**
     * Excel文件的访问密码
     * <p>
     * 设置后，打开文件需要输入该密码
     *
     * @return 文件密码，空字符串表示无密码
     */
    String password() default "";

    /**
     * 是否使用内存模式
     * <p>
     * true: 所有数据加载到内存中处理
     * false: 使用文件系统临时文件，适合大数据量
     *
     * @return 是否启用内存模式
     */
    boolean inMemory() default false;

    /**
     * 发生异常时是否继续写入Excel
     * <p>
     * true: 异常时仍然生成文件
     * false: 异常时中断处理
     *
     * @return 是否在异常时继续写入
     */
    boolean writeExcelOnException() default true;

    /**
     * Excel文件类型
     * <p>
     * 支持XLSX、XLS等格式
     *
     * @return Excel文件格式类型
     */
    ExcelTypeEnum excelType() default ExcelTypeEnum.XLSX;

    /**
     * 文件编码格式
     * <p>
     * 默认使用系统编码，可指定特定编码如UTF-8
     *
     * @return 文件编码
     */
    String charset() default "";

    /**
     * 是否添加BOM标记
     * <p>
     * 用于处理特殊编码场景，如UTF-8-BOM
     *
     * @return 是否包含BOM标记
     */
    boolean withBom() default false;

    /**
     * Excel模板文件路径
     * <p>
     * 可指定一个模板文件作为基础进行数据填充
     *
     * @return 模板文件路径
     */
    String template() default "";

    /**
     * Excel工作表配置
     * <p>
     * 可配置多个Sheet页的属性
     *
     * @return Sheet页配置数组
     */
    ExcelSheet[] sheets() default @ExcelSheet(sheetNo = 0);

    /**
     * 导出文件名
     * <p>
     * 设置生成的Excel文件名称，不需要包含扩展名
     *
     * @return 导出的文件名
     */
    String filename();

    /**
     * 是否强制使用InputStream
     * <p>
     * true: 强制使用InputStream进行数据处理
     * false: 根据文件大小选择使用InputStream或FileChannel进行数据处理
     *
     * @return 是否强制使用InputStream
     */
    boolean mandatoryUseInputStream() default false;
}
