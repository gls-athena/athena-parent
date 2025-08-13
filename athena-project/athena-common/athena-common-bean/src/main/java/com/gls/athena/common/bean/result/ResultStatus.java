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

    // ========== 2xx 成功状态 ==========
    /**
     * 请求成功
     */
    SUCCESS(200, "成功"),

    /**
     * 创建成功
     */
    CREATED(201, "创建成功"),

    /**
     * 更新成功
     */
    UPDATED(200, "更新成功"),

    /**
     * 删除成功
     */
    DELETED(200, "删除成功"),

    // ========== 4xx 客户端错误 ==========
    /**
     * 请求参数验证失败
     */
    PARAM_ERROR(400, "参数错误"),

    /**
     * 请求参数格式错误
     */
    PARAM_FORMAT_ERROR(400, "参数格式错误"),

    /**
     * 必填参数缺失
     */
    PARAM_MISSING(400, "必填参数缺失"),

    /**
     * 用户未登录或登录已过期
     */
    UNAUTHORIZED(401, "未登录"),

    /**
     * 登录凭证无效
     */
    INVALID_TOKEN(401, "登录凭证无效"),

    /**
     * 登录凭证已过期
     */
    TOKEN_EXPIRED(401, "登录凭证已过期"),

    /**
     * 用户无权限访问
     */
    FORBIDDEN(403, "未授权"),

    /**
     * 请求的资源不存在
     */
    NOT_FOUND(404, "未找到"),

    /**
     * 请求方法不支持
     */
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),

    /**
     * 请求冲突（如重复提交）
     */
    CONFLICT(409, "请求冲突"),

    /**
     * 请求过于频繁
     */
    TOO_MANY_REQUESTS(429, "请求过于频繁"),

    // ========== 5xx 服务器错误 ==========
    /**
     * 请求失败
     */
    FAIL(500, "失败"),

    /**
     * 服务器通用错误
     */
    SERVER_ERROR(500, "服务器错误"),

    /**
     * 服务器内部处理异常
     */
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),

    /**
     * 数据库操作异常
     */
    DATABASE_ERROR(500, "数据库操作异常"),

    /**
     * 外部服务调用异常
     */
    EXTERNAL_SERVICE_ERROR(500, "外部服务调用异常"),

    /**
     * 服务暂不可用
     */
    SERVICE_UNAVAILABLE(503, "服务暂不可用"),

    /**
     * 网关超时
     */
    GATEWAY_TIMEOUT(504, "网关超时"),

    // ========== 业务错误 (6xx自定义) ==========
    /**
     * 业务规则验证失败
     */
    BUSINESS_ERROR(600, "业务处理失败"),

    /**
     * 数据不存在
     */
    DATA_NOT_EXISTS(601, "数据不存在"),

    /**
     * 数据已存在
     */
    DATA_EXISTS(602, "数据已存在"),

    /**
     * 数据状态不正确
     */
    DATA_STATUS_ERROR(603, "数据状态不正确"),

    /**
     * 操作不被允许
     */
    OPERATION_NOT_ALLOWED(604, "操作不被允许"),

    /**
     * 文件上传失败
     */
    FILE_UPLOAD_ERROR(605, "文件上传失败"),

    /**
     * 文件类型不支持
     */
    FILE_TYPE_NOT_SUPPORTED(606, "文件类型不支持"),

    /**
     * 文件大小超限
     */
    FILE_SIZE_EXCEEDED(607, "文件大小超限");

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

    /**
     * 根据状态码获取对应的枚举
     *
     * @param code 状态码
     * @return 对应的状态枚举，如果未找到则返回 null
     */
    public static ResultStatus valueOf(Integer code) {
        if (code == null) {
            return null;
        }
        for (ResultStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 判断是否为成功状态
     *
     * @return 如果状态码在 200~299 范围内，则返回 true；否则返回 false
     */
    public boolean isSuccess() {
        return code != null && code >= 200 && code < 300;
    }

    /**
     * 判断是否为客户端错误
     *
     * @return 如果状态码在 400~499 范围内，则返回 true；否则返回 false
     */
    public boolean isClientError() {
        return code != null && code >= 400 && code < 500;
    }

    /**
     * 判断是否为服务器错误
     *
     * @return 如果状态码大于等于 500，则返回 true；否则返回 false
     */
    public boolean isServerError() {
        return code != null && code >= 500;
    }
}
