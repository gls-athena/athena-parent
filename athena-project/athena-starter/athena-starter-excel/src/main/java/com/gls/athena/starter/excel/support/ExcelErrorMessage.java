package com.gls.athena.starter.excel.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Excel错误消息
 *
 * @author george
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ExcelErrorMessage {
    /**
     * 行号
     */
    private Integer rowIndex;
    /**
     * 字段名
     */
    private String fieldName;
    /**
     * 错误信息
     */
    private String message;
    /**
     * 错误值
     */
    private String errorValue;
}
