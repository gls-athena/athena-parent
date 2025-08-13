package com.gls.athena.common.bean.result;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 统一响应结果封装类
 *
 * @param <T> 响应数据的类型参数
 * @author george
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
public class Result<T> {

    /**
     * 响应状态码
     * 200: 成功
     * 4xx: 客户端错误
     * 5xx: 服务端错误
     */
    private Integer code;

    /**
     * 响应消息
     * 用于描述处理结果
     */
    private String message;

    /**
     * 响应数据
     * 成功时返回的业务数据
     */
    private T data;

    /**
     * 时间戳
     * 响应生成的时间戳
     */
    private Long timestamp;

    /**
     * 请求追踪ID
     * 用于链路追踪和问题排查
     */
    private String traceId;

    /**
     * 无参构造方法，初始化时间戳为当前系统时间
     */
    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 带参数的构造方法，用于初始化响应结果对象
     *
     * @param code    响应状态码
     * @param message 响应消息
     * @param data    响应数据
     */
    public Result(Integer code, String message, T data) {
        this();
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 创建一个成功的响应结果（不包含响应数据）
     *
     * @param <T> 泛型类型，表示响应数据的类型
     * @return 返回封装好的成功响应结果对象
     */
    public static <T> Result<T> success() {
        return new Result<>(ResultStatus.SUCCESS.getCode(), ResultStatus.SUCCESS.getMessage(), null);
    }

    /**
     * 创建一个成功的响应结果（包含响应数据）
     *
     * @param data 响应数据
     * @param <T>  泛型类型，表示响应数据的类型
     * @return 返回封装好的成功响应结果对象
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultStatus.SUCCESS.getCode(), ResultStatus.SUCCESS.getMessage(), data);
    }

    /**
     * 创建一个成功的响应结果（自定义消息并包含响应数据）
     *
     * @param message 自定义响应消息
     * @param data    响应数据
     * @param <T>     泛型类型，表示响应数据的类型
     * @return 返回封装好的成功响应结果对象
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(ResultStatus.SUCCESS.getCode(), message, data);
    }

    /**
     * 创建一个默认的失败响应结果（无响应数据）
     *
     * @param <T> 泛型类型，表示响应数据的类型
     * @return 返回封装好的失败响应结果对象
     */
    public static <T> Result<T> error() {
        return new Result<>(ResultStatus.INTERNAL_SERVER_ERROR.getCode(), ResultStatus.INTERNAL_SERVER_ERROR.getMessage(), null);
    }

    /**
     * 创建一个带有自定义消息的失败响应结果（无响应数据）
     *
     * @param message 自定义响应消息
     * @param <T>     泛型类型，表示响应数据的类型
     * @return 返回封装好的失败响应结果对象
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(ResultStatus.INTERNAL_SERVER_ERROR.getCode(), message, null);
    }

    /**
     * 根据指定的状态枚举创建一个失败响应结果（无响应数据）
     *
     * @param status 状态枚举，包含状态码和消息
     * @param <T>    泛型类型，表示响应数据的类型
     * @return 返回封装好的失败响应结果对象
     */
    public static <T> Result<T> error(IResultStatus status) {
        return new Result<>(status.getCode(), status.getMessage(), null);
    }

    /**
     * 创建一个完全自定义的失败响应结果（无响应数据）
     *
     * @param code    自定义状态码
     * @param message 自定义响应消息
     * @param <T>     泛型类型，表示响应数据的类型
     * @return 返回封装好的失败响应结果对象
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    /**
     * 判断当前响应是否为成功状态
     *
     * @return 如果状态码为成功状态码则返回 true，否则返回 false
     */
    public boolean isSuccess() {
        return ResultStatus.SUCCESS.getCode().equals(this.code);
    }

    /**
     * 判断当前响应是否为失败状态
     *
     * @return 如果不是成功状态则返回 true，否则返回 false
     */
    public boolean isError() {
        return !isSuccess();
    }

    /**
     * 设置追踪ID，并返回当前对象以支持链式调用
     *
     * @param traceId 追踪ID
     * @return 当前 Result 对象
     */
    public Result<T> withTraceId(String traceId) {
        this.traceId = traceId;
        return this;
    }
}
