package com.gls.athena.starter.excel.web.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * Excel异步任务状态管理类
 *
 * @author george
 */
@Data
@Accessors(chain = true)
public class ExcelAsyncTask {

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 任务状态
     */
    private TaskStatus status;

    /**
     * 任务创建时间
     */
    private LocalDateTime createTime;

    /**
     * 任务开始时间
     */
    private LocalDateTime startTime;

    /**
     * 任务完成时间
     */
    private LocalDateTime finishTime;

    /**
     * 文件名
     */
    private String filename;

    /**
     * 文件路径（生成完成后的存储路径）
     */
    private String filePath;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 进度百分比 (0-100)
     */
    private Integer progress;

    /**
     * 任务描述
     */
    private String description;

}
