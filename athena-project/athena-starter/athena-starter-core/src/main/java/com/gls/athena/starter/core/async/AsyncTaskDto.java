package com.gls.athena.starter.core.async;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * 异步任务数据传输对象（DTO）
 * 用于封装异步任务的相关信息，包括任务ID、代码、名称、描述、类型、状态、参数、结果、错误信息和文件ID等。
 *
 * @author george
 */
@Data
@Accessors(chain = true)
public class AsyncTaskDto {

    /**
     * 任务唯一标识
     */
    private String taskId;

    /**
     * 任务代码
     */
    private String code;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 任务类型
     */
    private AsyncTaskType type;

    /**
     * 任务状态
     */
    private AsyncTaskStatus status;

    /**
     * 任务参数，以键值对形式存储
     */
    private Map<String, Object> params;

    /**
     * 任务结果
     */
    private Object result;

    /**
     * 任务错误信息
     */
    private String error;

    /**
     * 关联的文件ID
     */
    private String fileId;
}
