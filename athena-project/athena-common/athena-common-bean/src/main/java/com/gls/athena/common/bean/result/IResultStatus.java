package com.gls.athena.common.bean.result;

/**
 * 统一返回状态接口
 * 定义了返回结果的状态码和消息的标准接口，用于规范化系统响应
 *
 * @author george
 */
public interface IResultStatus {

    /**
     * 获取状态码
     *
     * @return 返回状态码，用于标识操作的结果类型
     */
    Integer getCode();

    /**
     * 获取状态消息
     *
     * @return 返回状态消息，用于描述操作结果的具体信息
     */
    String getMessage();

    /**
     * 将当前状态转换为不带数据的返回结果对象
     *
     * @param <T> 数据类型泛型参数
     * @return 返回一个包含当前状态码和消息的 Result 对象
     */
    default <T> Result<T> toResult() {
        return new Result<T>()
                .setCode(getCode())
                .setMessage(getMessage());
    }

    /**
     * 将当前状态转换为带数据的返回结果对象
     *
     * @param data 需要返回的数据对象
     * @param <T>  数据类型泛型参数
     * @return 返回一个包含当前状态码、消息和数据的 Result 对象
     */
    default <T> Result<T> toResult(T data) {
        return new Result<T>()
                .setCode(getCode())
                .setMessage(getMessage())
                .setData(data);
    }
}
