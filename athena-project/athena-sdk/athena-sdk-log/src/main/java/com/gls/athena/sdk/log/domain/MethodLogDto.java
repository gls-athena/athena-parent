package com.gls.athena.sdk.log.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.Map;

/**
 * 方法日志事件
 *
 * @author george
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class MethodLogDto extends MethodDto {
    /**
     * 参数
     */
    private Map<String, Object> args;
    /**
     * 结果
     */
    private Object result;
    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 错误信息
     */
    private String errorMessage;
    /**
     * 异常堆栈
     */
    private String throwable;
    /**
     * 方法日志类型
     */
    private MethodLogType type;
    /**
     * 跟踪ID
     */
    private String traceId;
}
