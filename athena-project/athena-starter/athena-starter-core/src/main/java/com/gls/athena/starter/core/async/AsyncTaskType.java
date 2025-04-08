package com.gls.athena.starter.core.async;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 异步任务类型
 *
 * @author george
 */
@Getter
@RequiredArgsConstructor
public enum AsyncTaskType {
    /**
     * 导入
     */
    IMPORT(0, "导入"),
    /**
     * 导出
     */
    EXPORT(1, "导出"),
    /**
     * 其它
     */
    OTHER(2, "其它");

    /**
     * 异步任务类型
     */
    private final int code;
    /**
     * 异步任务类型名称
     */
    private final String name;

}
