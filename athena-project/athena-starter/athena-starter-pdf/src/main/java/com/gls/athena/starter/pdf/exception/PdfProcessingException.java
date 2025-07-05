package com.gls.athena.starter.pdf.exception;

/**
 * PDF处理异常
 * 应用自定义异常处理策略
 *
 * @author george
 */
public class PdfProcessingException extends RuntimeException {

    public PdfProcessingException(String message) {
        super(message);
    }

    public PdfProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public PdfProcessingException(Throwable cause) {
        super(cause);
    }
}
