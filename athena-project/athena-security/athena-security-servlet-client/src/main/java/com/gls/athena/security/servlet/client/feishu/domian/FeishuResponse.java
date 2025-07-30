package com.gls.athena.security.servlet.client.feishu.domian;

import lombok.Data;

/**
 * 飞书响应
 *
 * @author george
 */
@Data
public class FeishuResponse<T> {
    /**
     * 响应码
     */
    private Integer code;
    /**
     * 响应消息
     */
    private String msg;
    /**
     * 数据
     */
    private T data;
}
