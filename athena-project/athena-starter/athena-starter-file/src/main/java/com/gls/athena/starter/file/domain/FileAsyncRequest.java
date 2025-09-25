package com.gls.athena.starter.file.domain;

import com.gls.athena.starter.file.base.BaseFileResponseWrapper;
import lombok.Data;
import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.annotation.Annotation;

/**
 * 文件异步请求封装类
 * 用于封装文件异步处理相关的请求信息，包括任务ID、响应注解和连接点信息
 *
 * @param <Response> 响应注解类型，必须继承自Annotation
 * @author lizy19
 */
@Data
public class FileAsyncRequest<Response extends Annotation> {

    /**
     * 任务ID，用于标识唯一的异步任务
     */
    private String taskId;

    /**
     * 响应注解，用于处理异步任务完成后的响应逻辑
     */
    private BaseFileResponseWrapper<Response> response;

    /**
     * 连接点对象，包含被拦截方法的执行信息
     */
    private ProceedingJoinPoint joinPoint;
}

