package com.gls.athena.starter.excel.support;

import com.gls.athena.starter.excel.annotation.ExcelResponse;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Excel异步请求封装类
 * <p>
 * 用于封装Excel导出的异步请求信息，包含Excel响应注解配置和实际数据
 * </p>
 *
 * @author george
 */
@Data
@Accessors(chain = true)
public class ExcelAsyncRequest {
    /**
     * 任务ID
     * <p>
     * 用于标识异步任务，用于后续查询任务进度和结果
     * </p>
     */
    private String taskId;
    /**
     * Excel响应注解配置
     * <p>
     * 用于配置Excel导出的相关参数，如文件名、表头等信息
     * </p>
     */
    private ExcelResponse excelResponse;

    /**
     * 导出数据对象
     * <p>
     * 实际需要导出的数据内容，可以是各种数据结构
     * </p>
     */
    private Object data;
}

