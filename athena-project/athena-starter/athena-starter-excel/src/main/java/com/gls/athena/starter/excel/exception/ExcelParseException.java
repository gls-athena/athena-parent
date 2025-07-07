package com.gls.athena.starter.excel.exception;

/**
 * Excel解析异常
 * 用于表示在Excel文件处理过程中发生的各种异常情况
 *
 * @author george
 */
public class ExcelParseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 创建一个新的Excel解析异常
     */
    public ExcelParseException() {
        super();
    }

    /**
     * 创建一个带有错误消息的Excel解析异常
     *
     * @param message 错误消息
     */
    public ExcelParseException(String message) {
        super(message);
    }

    /**
     * 创建一个带有错误消息和原因的Excel解析异常
     *
     * @param message 错误消息
     * @param cause   导致此异常的原因
     */
    public ExcelParseException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 创建一个带有原因的Excel解析异常
     *
     * @param cause 导致此异常的原因
     */
    public ExcelParseException(Throwable cause) {
        super(cause);
    }
}
