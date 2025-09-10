package com.gls.athena.starter.excel.web.domain;

import com.gls.athena.starter.excel.annotation.ExcelResponse;
import lombok.Data;
import lombok.experimental.Accessors;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * Excel异步请求封装类
 * <p>
 * 用于封装Excel导出的异步处理请求信息，包含任务ID、Excel响应注解信息以及切点信息
 * </p>
 */
@Data
@Accessors(chain = true)
public class ExcelAsyncRequest {

    /**
     * 异步任务唯一标识符
     */
    private String taskId;

    /**
     * Excel响应注解信息
     * <p>
     * 包含Excel导出的相关配置信息，如文件名、表头等
     * </p>
     */
    private ExcelResponse excelResponse;

    /**
     * 切点信息
     * <p>
     * Spring AOP的切点对象，用于获取被拦截方法的执行信息
     * </p>
     */
    private ProceedingJoinPoint joinPoint;
}


