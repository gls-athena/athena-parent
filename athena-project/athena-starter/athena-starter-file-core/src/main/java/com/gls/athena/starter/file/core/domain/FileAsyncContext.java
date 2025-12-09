package com.gls.athena.starter.file.core.domain;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * 文件异步处理上下文类
 * <p>
 * 该类用于封装文件异步处理过程中的上下文信息，包括任务标识、响应处理逻辑和方法执行信息
 * </p>
 *
 * @param taskId          任务ID，用于标识唯一的异步任务
 * @param responseWrapper 响应注解，用于处理异步任务完成后的响应逻辑
 * @param joinPoint       连接点对象，包含被拦截方法的执行信息
 * @author george
 */
public record FileAsyncContext(String taskId, FileResponseWrapper<?> responseWrapper, ProceedingJoinPoint joinPoint) {

}
