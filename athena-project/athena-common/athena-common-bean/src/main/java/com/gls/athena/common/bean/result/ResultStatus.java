package com.gls.athena.common.bean.result;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 返回结果状态
 *
 * @author george
 */
@Getter
@RequiredArgsConstructor
public enum ResultStatus implements IResultStatus {

    /**
     * 成功
     */
    SUCCESS(200, "成功"),
    /**
     * 失败
     */
    FAIL(500, "失败"),
    /**
     * 未登录
     */
    UNAUTHORIZED(401, "未登录"),
    /**
     * 未授权
     */
    FORBIDDEN(403, "未授权"),
    /**
     * 未找到
     */
    NOT_FOUND(404, "未找到"),
    /**
     * 参数错误
     */
    PARAM_ERROR(400, "参数错误"),
    /**
     * 服务器错误
     */
    SERVER_ERROR(500, "服务器错误"),
    /**
     * 服务器内部错误
     */
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    ;

    /**
     * 状态码
     */
    private final Integer code;
    /**
     * 状态信息
     */
    private final String message;

}
