package com.gls.athena.common.bean.result;

/**
 * 统一返回状态接口定义
 *
 * <p>该接口规范了系统响应的标准格式，包含状态码和状态消息。
 * 实现该接口的类需要提供状态码和状态消息的具体实现，
 * 通常用于枚举类型定义系统的各种响应状态。</p>
 *
 * @author george
 * @since 1.0
 */
public interface IResultStatus {

    /**
     * 获取状态码
     *
     * @return 操作结果的状态码，用于标识不同的业务场景
     */
    Integer getCode();

    /**
     * 获取状态消息
     *
     * @return 状态描述信息，用于提供具体的提示信息
     */
    String getMessage();

    /**
     * 创建不带数据的标准返回结果
     *
     * @param <T> 数据对象类型
     * @return 仅包含状态信息的标准返回结果
     */
    default <T> Result<T> toResult() {
        return new Result<T>()
                .setCode(getCode())
                .setMessage(getMessage());
    }

    /**
     * 创建带数据的标准返回结果
     *
     * @param data 业务数据对象
     * @param <T>  数据对象类型
     * @return 包含状态信息和业务数据的标准返回结果
     */
    default <T> Result<T> toResult(T data) {
        return new Result<T>()
                .setCode(getCode())
                .setMessage(getMessage())
                .setData(data);
    }
}
