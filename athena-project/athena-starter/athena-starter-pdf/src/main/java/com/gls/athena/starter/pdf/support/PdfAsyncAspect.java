package com.gls.athena.starter.pdf.support;

import com.gls.athena.starter.async.manager.IAsyncTaskManager;
import com.gls.athena.starter.file.manager.IFileManager;
import com.gls.athena.starter.file.support.FileAsyncAspect;
import com.gls.athena.starter.pdf.annotation.PdfResponse;
import com.gls.athena.starter.pdf.generator.PdfGenerator;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * PDF异步处理切面类
 * 用于拦截带有PdfResponse注解的方法，实现PDF文件的异步生成和处理
 *
 * @author george
 */
@Slf4j
@Aspect
@Component
public class PdfAsyncAspect extends FileAsyncAspect<PdfGenerator, PdfResponse> {

    /**
     * 构造函数，初始化PDF异步处理切面
     *
     * @param pdfGenerators    PDF生成器列表，用于处理不同类型的PDF生成任务
     * @param asyncTaskManager 异步任务管理器，用于管理异步任务的执行
     * @param fileManager      文件管理器，用于处理文件相关的操作
     * @param executor         线程池执行器，用于执行异步任务
     */
    public PdfAsyncAspect(List<PdfGenerator> pdfGenerators, IAsyncTaskManager asyncTaskManager, IFileManager fileManager, Executor executor) {
        super(pdfGenerators, asyncTaskManager, fileManager, executor);
    }

    /**
     * 环绕通知方法，拦截带有PdfResponse注解的方法执行
     *
     * @param joinPoint   连接点，包含被拦截方法的信息
     * @param pdfResponse PDF响应注解，包含PDF处理的相关配置
     * @return 处理结果对象
     * @throws Throwable 方法执行过程中可能抛出的异常
     */
    @Override
    @Around("@annotation(pdfResponse)")
    public Object around(ProceedingJoinPoint joinPoint, PdfResponse pdfResponse) throws Throwable {
        return super.around(joinPoint, pdfResponse);
    }

}

