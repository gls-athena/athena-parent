package com.gls.athena.starter.file.exception;

/**
 * 文件操作异常类
 * 用于封装文件相关操作中的异常信息
 *
 * @author george
 */
public class FileException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private String code;

    public FileException(String message) {
        super(message);
    }

    public FileException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileException(String code, String message) {
        super(message);
        this.code = code;
    }

    public FileException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    /**
     * 文件不存在异常
     */
    public static class FileNotFoundException extends FileException {
        public FileNotFoundException(String message) {
            super("FILE_NOT_FOUND", message);
        }
    }

    /**
     * 文件读取异常
     */
    public static class FileReadException extends FileException {
        public FileReadException(String message, Throwable cause) {
            super("FILE_READ_ERROR", message, cause);
        }
    }

    /**
     * 文件写入异常
     */
    public static class FileWriteException extends FileException {
        public FileWriteException(String message, Throwable cause) {
            super("FILE_WRITE_ERROR", message, cause);
        }
    }

    /**
     * 文件删除异常
     */
    public static class FileDeleteException extends FileException {
        public FileDeleteException(String message, Throwable cause) {
            super("FILE_DELETE_ERROR", message, cause);
        }
    }

    /**
     * 文件验证异常
     */
    public static class FileValidationException extends FileException {
        public FileValidationException(String message) {
            super("FILE_VALIDATION_ERROR", message);
        }
    }

    /**
     * 文件生成器未找到异常
     */
    public static class GeneratorNotFoundException extends FileException {
        public GeneratorNotFoundException(String message) {
            super("GENERATOR_NOT_FOUND", message);
        }
    }
}

