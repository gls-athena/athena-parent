package com.gls.athena.starter.file.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.annotation.Annotation;

/**
 * 文件异步处理上下文类
 * <p>
 * 该类用于封装文件异步处理过程中的上下文信息，包括任务标识、响应处理逻辑和方法执行信息
 * </p>
 *
 * @param <Response> 响应注解类型，必须继承自Annotation
 * @author george
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileAsyncContext<Response extends Annotation> {

    /**
     * 任务ID，用于标识唯一的异步任务
     */
    private String taskId;

    /**
     * 响应注解，用于处理异步任务完成后的响应逻辑
     */
    private FileResponseWrapper<Response> responseWrapper;

    /**
     * 连接点对象，包含被拦截方法的执行信息
     */
    private ProceedingJoinPoint joinPoint;
}
