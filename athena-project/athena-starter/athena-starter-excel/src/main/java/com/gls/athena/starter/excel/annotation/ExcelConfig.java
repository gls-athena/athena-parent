package com.gls.athena.starter.excel.annotation;

import cn.idev.excel.converters.Converter;
import cn.idev.excel.enums.CacheLocationEnum;
import cn.idev.excel.write.handler.WriteHandler;

import java.lang.annotation.*;

/**
 * Excel参数配置注解
 * <p>
 * 用于配置实体类的Excel导入导出相关参数，支持字段级别和方法级别的注解
 *
 * @author george
 */
@Documented
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelConfig {
    /**
     * Excel表头配置
     *
     * @return 表头名称数组，支持多级表头配置，默认为空数组
     */
    String[] head() default {};

    /**
     * Excel表头对应的字段名
     *
     * @return 字段名数组，支持多级表头配置，默认为空数组
     */
    Class<?> clazz() default Object.class;

    /**
     * 数据类型转换器
     *
     * @return 自定义转换器类数组，用于特殊数据类型的转换，默认为空数组
     */
    Class<? extends Converter<?>>[] converter() default {};

    /**
     * Excel日期系统设置
     *
     * @return true表示使用1904年为起始时间，false表示使用1900年，默认为false
     */
    boolean use1904windowing() default false;

    /**
     * 区域设置
     *
     * @return 指定区域设置的字符串，例如："zh_CN"，默认为空
     */
    String locale() default "";

    /**
     * 字段缓存策略
     *
     * @return 缓存位置枚举值，默认使用THREAD_LOCAL策略
     */
    CacheLocationEnum filedCacheLocation() default CacheLocationEnum.THREAD_LOCAL;

    /**
     * 自动去除数据前后空格
     *
     * @return true表示自动去除空格，作用于表头和数据单元格，默认为true
     */
    boolean autoTrim() default true;

    /**
     * 表头偏移行数
     *
     * @return 指定开始写入数据时，距离顶部的空行数，默认为0
     */
    int relativeHeadRowIndex() default 0;

    /**
     * 是否写入表头
     *
     * @return true表示写入表头，false表示不写入，默认为true
     */
    boolean needHead() default true;

    /**
     * 自定义写入处理器
     *
     * @return 处理器类数组，用于自定义写入逻辑，默认为空数组
     */
    Class<? extends WriteHandler>[] writeHandler() default {};

    /**
     * 默认样式启用设置
     *
     * @return true表示使用默认样式，false表示完全自定义，默认为true
     */
    boolean useDefaultStyle() default true;

    /**
     * 表头自动合并设置
     *
     * @return true表示自动合并相同的表头，false表示不合并，默认为true
     */
    boolean automaticMergeHead() default true;

    /**
     * 排除列索引配置
     *
     * @return 要排除的列索引数组，这些列不会被导出，默认为空数组
     */
    int[] excludeColumnIndexes() default {};

    /**
     * 排除字段名配置
     *
     * @return 要排除的字段名数组，这些字段不会被导出，默认为空数组
     */
    String[] excludeColumnFieldNames() default {};

    /**
     * 包含列索引配置
     *
     * @return 要包含的列索引数组，仅导出这些列，默认为空数组
     */
    int[] includeColumnIndexes() default {};

    /**
     * 包含字段名配置
     *
     * @return 要包含的字段名数组，仅导出这些字段，默认为空数组
     */
    String[] includeColumnFieldNames() default {};

    /**
     * 包含列的排序设置
     *
     * @return true表示按照include配置的顺序排序，false表示使用默认顺序，默认为false
     */
    boolean orderByIncludeColumn() default false;

    /**
     * 科学计数法设置
     *
     * @return true表示使用科学计数法，false表示不使用科学计数法，默认为false
     */
    boolean useScientificFormat() default false;
}
