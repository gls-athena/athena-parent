package com.gls.athena.common.bean.result;

/**
 * 统一返回状态接口
 *
 * <p>定义系统响应的标准格式，包含状态码和状态消息。
 * 通常由枚举类实现，用于定义系统的各种响应状态。</p>
 *
 * @author george
 * @since 1.0
 */
public interface IResultStatus {

    /**
     * 获取状态码
     *
     * @return 状态码，用于标识不同的业务场景
     */
    Integer getCode();

    /**
     * 获取状态消息
     *
     * @return 状态描述信息
     */
    String getMessage();

    /**
     * 创建不带数据的返回结果
     *
     * @param <T> 数据类型
     * @return 包含当前状态信息的Result对象
     */
    default <T> Result<T> toResult() {
        return new Result<T>()
                .setCode(getCode())
                .setMessage(getMessage());
    }

    /**
     * 创建带数据的返回结果
     *
     * @param data 业务数据
     * @param <T>  数据类型
     * @return 包含当前状态信息和业务数据的Result对象
     */
    default <T> Result<T> toResult(T data) {
        return new Result<T>()
                .setCode(getCode())
                .setMessage(getMessage())
                .setData(data);
    }

}
