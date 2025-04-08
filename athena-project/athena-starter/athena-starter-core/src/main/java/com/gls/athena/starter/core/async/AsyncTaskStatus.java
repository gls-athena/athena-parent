package com.gls.athena.starter.core.async;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 异步任务状态
 *
 * @author george
 */
@Getter
@RequiredArgsConstructor
public enum AsyncTaskStatus {
    /**
     * 待执行
     */
    WAITING(0, "待执行"),
    /**
     * 执行中
     */
    EXECUTING(1, "执行中"),
    /**
     * 执行成功
     */
    SUCCESS(2, "执行成功"),
    /**
     * 执行失败
     */
    FAIL(3, "执行失败");
    /**
     * 状态码
     */
    private final Integer code;
    /**
     * 状态名称
     */
    private final String name;

}
