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
     * <p>
     * 该方法用于生成一个仅包含状态信息的标准返回结果对象。返回结果对象中不包含具体的数据对象，
     * 仅包含状态码和状态信息。该方法适用于不需要返回具体数据，仅需返回操作状态的场景。
     * </p>
     *
     * @param <T> 数据对象类型，泛型参数，表示返回结果中可能包含的数据类型
     * @return 返回一个仅包含状态信息的标准返回结果对象，该对象包含状态码和状态信息
     */
    default <T> Result<T> toResult() {
        // 创建一个新的Result对象，并设置状态码和状态信息
        return new Result<T>()
                .setCode(getCode())
                .setMessage(getMessage());
    }

    /**
     * 创建带数据的标准返回结果
     * <p>
     * 该方法用于生成一个包含状态信息和业务数据的标准返回结果对象。通过调用 `getCode()` 和 `getMessage()` 方法获取当前状态码和消息，
     * 并将其与传入的业务数据对象一起封装到 `Result` 对象中。
     *
     * @param data 业务数据对象，类型为泛型 T，表示返回结果中携带的具体业务数据
     * @param <T>  数据对象类型，泛型参数，表示业务数据的类型
     * @return 包含状态信息和业务数据的标准返回结果对象，类型为 `Result<T>`
     */
    default <T> Result<T> toResult(T data) {
        // 创建一个新的 Result 对象，并设置状态码、消息和业务数据
        return new Result<T>()
                .setCode(getCode())
                .setMessage(getMessage())
                .setData(data);
    }

}
