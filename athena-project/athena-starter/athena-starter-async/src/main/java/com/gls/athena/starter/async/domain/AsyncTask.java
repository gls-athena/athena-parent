package com.gls.athena.starter.async.domain;

import com.gls.athena.common.bean.base.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.Map;

/**
 * 异步任务实体类
 * 用于封装异步任务的信息
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AsyncTask extends BaseVo {

    /**
     * 任务ID
     */
    private String taskId;
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

}