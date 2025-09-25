package com.gls.athena.starter.excel.async;

import com.gls.athena.starter.async.manager.IAsyncTaskManager;
import com.gls.athena.starter.excel.annotation.ExcelResponse;
import com.gls.athena.starter.excel.generator.ExcelGenerator;
import com.gls.athena.starter.excel.support.ExcelResponseWrapper;
import com.gls.athena.starter.file.base.BaseFileAsyncAspect;
import com.gls.athena.starter.file.base.BaseFileResponseWrapper;
import com.gls.athena.starter.file.manager.IFileManager;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * Excel异步处理切面
 * <p>
 * 在Controller方法执行前判断是否需要异步处理Excel导出。
 * 如果需要异步处理，则立即返回任务ID，然后在后台异步执行Controller方法获取数据并生成Excel文件。
 * </p>
 *
 * @author george
 */
@Slf4j
@Aspect
@Component
public class ExcelAsyncAspect extends BaseFileAsyncAspect<ExcelGenerator, ExcelResponse> {

    public ExcelAsyncAspect(List<ExcelGenerator> excelGenerators,
                            IAsyncTaskManager<?> asyncTaskManager,
                            IFileManager fileManager,
                            Executor executor) {
        super(excelGenerators, asyncTaskManager, fileManager, executor);
    }

    /**
     * 环绕通知方法，拦截带有@ExcelResponse注解的方法调用
     * <p>
     * 根据注解配置决定是同步还是异步执行导出逻辑。
     * 若为异步模式，则提交任务到异步任务管理器，并返回任务ID；
     * 否则直接执行原方法并返回结果。
     * </p>
     *
     * @param joinPoint     连接点对象，代表被拦截的方法
     * @param excelResponse Excel响应注解对象，包含导出相关配置信息
     * @return 如果是异步导出则返回任务ID；否则返回实际导出结果
     * @throws Throwable 方法执行过程中可能抛出的异常
     */
    @Override
    @Around("@annotation(excelResponse)")
    public Object around(ProceedingJoinPoint joinPoint, ExcelResponse excelResponse) throws Throwable {
        return super.around(joinPoint, excelResponse);
    }

    @Override
    protected BaseFileResponseWrapper<ExcelResponse> getResponseWrapper(ExcelResponse excelResponse) {
        return new ExcelResponseWrapper(excelResponse);
    }

}
