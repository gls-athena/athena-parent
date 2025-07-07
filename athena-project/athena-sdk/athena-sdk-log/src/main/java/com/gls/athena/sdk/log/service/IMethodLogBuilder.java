package com.gls.athena.sdk.log.service;

import com.gls.athena.sdk.log.domain.MethodLogDto;
import com.gls.athena.sdk.log.method.MethodLog;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * 方法日志构建器接口
 * 职责：负责构建和填充方法日志数据
 *
 * @author george
 */
public interface IMethodLogBuilder {

    /**
     * 创建方法日志对象
     * 填充方法基本信息和开始时间
     *
     * @param point     切点信息
     * @param methodLog 方法日志注解
     * @return 方法日志对象
     */
    MethodLogDto createMethodLog(ProceedingJoinPoint point, MethodLog methodLog);

    /**
     * 填充成功执行结果
     *
     * @param logDto 日志对象
     * @param result 执行结果
     */
    void fillSuccessResult(MethodLogDto logDto, Object result);

    /**
     * 填充异常执行结果
     *
     * @param logDto    日志对象
     * @param throwable 异常信息
     */
    void fillErrorResult(MethodLogDto logDto, Throwable throwable);
}
