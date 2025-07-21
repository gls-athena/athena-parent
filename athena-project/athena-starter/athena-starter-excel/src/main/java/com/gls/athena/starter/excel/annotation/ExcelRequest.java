package com.gls.athena.starter.excel.annotation;

import com.gls.athena.starter.excel.listener.DefaultReadListener;
import com.gls.athena.starter.excel.listener.IReadListener;

import java.lang.annotation.*;

/**
 * Excel文件上传请求参数注解
 * <p>
 * 该注解用于标记Controller方法中接收Excel文件上传的参数。
 * 使用此注解可以自动解析上传的Excel文件内容到指定对象中。
 * </p>
 * <p>
 * 使用示例:
 * <pre>
 * @PostMapping("/import")
 * public Result importData(@ExcelRequest List<UserDTO> userList) {
 *     // 处理导入的数据
 * }
 * </pre>
 *
 * @author george
 * @since 1.0.0
 */
@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelRequest {

    /**
     * 上传文件参数名
     * <p>
     * 默认值为"file"，对应前端上传文件的form字段名
     * </p>
     */
    String filename() default "file";

    /**
     * Excel表头行数
     * <p>
     * 指定Excel文件中表头占用的行数，默认为1行
     * 系统会跳过这些行数后开始读取数据内容
     * </p>
     */
    int headRowNumber() default 1;

    /**
     * 是否忽略空行
     * <p>
     * 设置为true时，读取Excel过程中会自动跳过空行
     * 设置为false时，空行会被保留并转换为对象
     * </p>
     */
    boolean ignoreEmptyRow() default true;

    /**
     * 自定义读取监听器
     * <p>
     * 可以指定自定义的Excel读取监听器实现类
     * 用于在读取过程中进行数据处理和验证
     * 默认使用{@link DefaultReadListener}
     * </p>
     */
    Class<? extends IReadListener> readListener() default DefaultReadListener.class;

    /**
     * 是否允许空结果
     * <p>
     * 设置为true时，即使Excel解析后没有数据也不会抛出异常
     * 设置为false时，如果解析结果为空会抛出异常
     * 默认为false，保持与之前版本的兼容性
     * </p>
     */
    boolean allowEmptyResult() default false;
}
