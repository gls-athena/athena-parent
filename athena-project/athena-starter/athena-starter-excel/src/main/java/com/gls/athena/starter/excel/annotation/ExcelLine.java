package com.gls.athena.starter.excel.annotation;

import java.lang.annotation.*;

/**
 * Excel行号标记注解
 *
 * <p>该注解用于在实体类中标记Excel行号字段。当解析Excel文件时，
 * 被该注解标记的字段将自动接收当前处理行的行号值。</p>
 *
 * <p>使用场景：
 * <ul>
 *     <li>需要记录Excel数据在原文件中的行号位置</li>
 *     <li>用于数据校验时的错误行定位</li>
 *     <li>导入异常时的问题排查</li>
 * </ul>
 * </p>
 *
 * @author george
 * @since 1.0.0
 */
@Documented
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelLine {
}
