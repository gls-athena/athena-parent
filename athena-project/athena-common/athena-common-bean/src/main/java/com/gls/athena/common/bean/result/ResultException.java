package com.gls.athena.common.bean.result;

import lombok.Getter;

/**
 * 自定义结果异常类
 * 用于封装业务异常，包含错误码和错误信息
 *
 * @author george
 */
@Getter
public class ResultException extends RuntimeException {
    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误信息
     */
    private final String message;

    /**
     * 使用错误码和错误信息构造异常
     *
     * @param code    错误码
     * @param message 错误信息
     */
    public ResultException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 使用错误码、错误信息和原始异常构造异常
     *
     * @param code    错误码
     * @param message 错误信息
     * @param cause   原始异常
     */
    public ResultException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    /**
     * 完整构造函数
     *
     * @param code               错误码
     * @param message            错误信息
     * @param cause              原始异常
     * @param enableSuppression  是否启用异常抑制
     * @param writableStackTrace 是否生成堆栈跟踪
     */
    public ResultException(Integer code, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
        this.message = message;
    }

    /**
     * 使用结果状态枚举构造异常
     *
     * @param enums 结果状态枚举，实现了 IResultStatus 接口
     */
    public ResultException(IResultStatus enums) {
        super(enums.getMessage());
        this.code = enums.getCode();
        this.message = enums.getMessage();
    }

    /**
     * 使用结果状态枚举和原始异常构造异常
     *
     * @param enums 结果状态枚举，实现了 IResultStatus 接口
     * @param cause 原始异常
     */
    public ResultException(IResultStatus enums, Throwable cause) {
        super(enums.getMessage(), cause);
        this.code = enums.getCode();
        this.message = enums.getMessage();
    }

    /**
     * 使用结果状态枚举的完整构造函数
     *
     * @param enums              结果状态枚举，实现了 IResultStatus 接口
     * @param cause              原始异常
     * @param enableSuppression  是否启用异常抑制
     * @param writableStackTrace 是否生成堆栈跟踪
     */
    public ResultException(IResultStatus enums, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(enums.getMessage(), cause, enableSuppression, writableStackTrace);
        this.code = enums.getCode();
        this.message = enums.getMessage();
    }
}
