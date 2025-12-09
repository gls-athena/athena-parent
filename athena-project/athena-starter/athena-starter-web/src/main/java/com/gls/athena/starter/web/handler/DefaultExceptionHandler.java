package com.gls.athena.starter.web.handler;

import com.gls.athena.common.core.constant.IConstants;
import com.gls.athena.common.core.domain.Result;
import com.gls.athena.common.core.enums.ResultStatus;
import com.gls.athena.common.core.result.ResultException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 异常通知
 *
 * @author george
 */
@Slf4j
@RestControllerAdvice(basePackages = IConstants.BASE_PACKAGE_PREFIX)
public class DefaultExceptionHandler {

    /**
     * 处理ResultException异常
     *
     * @param e 异常
     * @return Result
     */
    @ExceptionHandler(ResultException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<?> resultExceptionHandler(ResultException e) {
        // 记录异常日志
        log.error(e.getMessage(), e);
        // 返回异常结果
        return ResultStatus.FAIL.toResult().setCode(e.getCode()).setMessage(e.getMessage());
    }

    /**
     * 处理RuntimeException异常
     *
     * @param e 异常
     * @return Result
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<?> runtimeExceptionHandler(RuntimeException e) {
        // 记录异常日志
        log.error(e.getMessage(), e);
        // 返回异常结果
        return ResultStatus.SERVER_ERROR.toResult().setMessage(e.getMessage());
    }

    /**
     * 处理Exception异常
     *
     * @param e 异常
     * @return Result
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<?> exceptionHandler(Exception e) {
        // 记录异常日志
        log.error(e.getMessage(), e);
        // 返回异常结果
        return ResultStatus.SERVER_ERROR.toResult().setMessage(e.getMessage());
    }
}
