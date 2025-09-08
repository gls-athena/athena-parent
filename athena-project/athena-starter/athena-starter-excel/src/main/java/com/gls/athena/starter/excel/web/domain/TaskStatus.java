package com.gls.athena.starter.excel.web.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 任务状态枚举
 *
 * @author george
 */
@Getter
@RequiredArgsConstructor
public enum TaskStatus {
    /**
     * 等待处理
     */
    WAITING("等待处理"),

    /**
     * 处理中
     */
    PROCESSING("处理中"),

    /**
     * 已完成
     */
    COMPLETED("已完成"),

    /**
     * 失败
     */
    FAILED("失败"),

    /**
     * 已取消
     */
    CANCELLED("已取消");

    private final String description;

}
