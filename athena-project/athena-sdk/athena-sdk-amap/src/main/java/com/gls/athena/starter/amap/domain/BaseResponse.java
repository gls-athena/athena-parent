package com.gls.athena.starter.amap.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 基础响应
 *
 * @author george
 */
@Data
public abstract class BaseResponse implements Serializable {
    /**
     * 返回结果状态值
     */
    private String status;
    /**
     * 返回状态说明
     */
    private String info;
    /**
     * 返回状态说明编码
     */
    private String infocode;
}
