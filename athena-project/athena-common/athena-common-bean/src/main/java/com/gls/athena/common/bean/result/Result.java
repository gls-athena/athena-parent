package com.gls.athena.common.bean.result;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 统一响应结果封装类
 *
 * @param <T> 响应数据的类型参数
 * @author george
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

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

}
