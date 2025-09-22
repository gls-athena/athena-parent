package com.gls.athena.starter.async.task;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 异步任务实体类
 * 用于封装异步任务的信息
 *
 * @author george
 */
@Data
@Accessors(chain = true)
public class AsyncTask implements Serializable {

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 跟踪ID
     */
    private String traceId;

    /**
     * 任务类型
     */
    private String type;

    /**
     * 任务编码
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
     * 任务参数
     */
    private Map<String, Object> params;

    /**
     * 任务状态
     */
    private AsyncTaskStatus status;

    /**
     * 任务进度
     */
    private Integer progress;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 任务结果
     */
    private Map<String, Object> result;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}