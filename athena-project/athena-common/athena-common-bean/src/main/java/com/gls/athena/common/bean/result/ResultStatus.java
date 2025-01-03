package com.gls.athena.common.bean.result;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 统一响应状态码枚举
 *
 * <p>定义系统通用的响应状态码和对应的描述信息，用于标准化接口响应。
 * 状态码参考 HTTP 状态码设计，主要分为以下区间：</p>
 * <ul>
 *     <li>2xx：成功相关状态</li>
 *     <li>4xx：客户端错误相关状态</li>
 *     <li>5xx：服务端错误相关状态</li>
 * </ul>
 *
 * @author george
 */
@Getter
@RequiredArgsConstructor
public enum ResultStatus implements IResultStatus {

    /**
     * 请求成功
     */
    SUCCESS(200, "成功"),

    /**
     * 请求失败
     */
    FAIL(500, "失败"),

    /**
     * 用户未登录或登录已过期
     */
    UNAUTHORIZED(401, "未登录"),

    /**
     * 用户无权限访问
     */
    FORBIDDEN(403, "未授权"),

    /**
     * 请求的资源不存在
     */
    NOT_FOUND(404, "未找到"),

    /**
     * 请求参数验证失败
     */
    PARAM_ERROR(400, "参数错误"),

    /**
     * 服务器通用错误
     */
    SERVER_ERROR(500, "服务器错误"),

    /**
     * 服务器内部处理异常
     */
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    ;

    /**
     * 状态码
     * <p>参考 HTTP 状态码设计，用于标识请求处理的结果</p>
     */
    private final Integer code;

    /**
     * 状态描述
     * <p>对状态码的简要说明，用于向客户端传达处理结果的具体信息</p>
     */
    private final String message;

}
