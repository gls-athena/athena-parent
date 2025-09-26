package com.gls.athena.starter.excel.support;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Excel错误消息
 *
 * @author george
 */
@Data
@Accessors(chain = true)
public class ExcelErrorMessage {
    /**
     * 行号
     */
    private Integer line;
    /**
     * 字段名
     */
    private String fieldName;
    /**
     * 错误信息
     */
    private String errorMessage;
    /**
     * 错误值
     */
    private Object errorValue;

}
