package com.gls.athena.sdk.log.domain;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 方法日志事件
 *
 * @author george
 */
@Data
@Accessors(chain = true)
public class MethodDto {
    /**
     * 编码
     */
    private String code;
    /**
     * 名称
     */
    private String name;
    /**
     * 描述
     */
    private String description;
    /**
     * 应用名称
     */
    private String applicationName;
    /**
     * 类名
     */
    private String className;
    /**
     * 方法名
     */
    private String methodName;
}
