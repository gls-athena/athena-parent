package com.gls.athena.starter.jasper.support;

import com.gls.athena.starter.async.manager.IAsyncTaskManager;
import com.gls.athena.starter.file.manager.IFileManager;
import com.gls.athena.starter.file.support.FileAsyncAspect;
import com.gls.athena.starter.jasper.annotation.JasperResponse;
import com.gls.athena.starter.jasper.generator.JasperGenerator;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * Jasper异步处理切面类，用于拦截带有{@link JasperResponse}注解的方法，
 * 并根据配置决定是否异步生成报表文件。
 * <p>
 * 该类继承自{@link FileAsyncAspect}，实现了对Jasper报表生成任务的异步处理逻辑。
 * </p>
 *
 * @author george
 */
@Slf4j
@Aspect
@Component
public class JasperAsyncAspect extends FileAsyncAspect<JasperGenerator, JasperResponse> {

    /**
     * 构造方法，初始化Jasper异步处理切面所需的依赖组件。
     *
     * @param jasperGenerators Jasper报表生成器列表，用于实际的报表内容生成
     * @param asyncTaskManager 异步任务管理器，用于管理异步执行的任务
     * @param fileManager      文件管理器，用于处理生成后的文件操作
     * @param executor         线程池执行器，用于执行异步任务
     */
    public JasperAsyncAspect(List<JasperGenerator> jasperGenerators, IAsyncTaskManager<?> asyncTaskManager, IFileManager fileManager, Executor executor) {
        super(jasperGenerators, asyncTaskManager, fileManager, executor);
    }

    /**
     * 环绕通知方法，拦截带有{@link JasperResponse}注解的方法调用。
     * 根据注解配置决定是否以异步方式生成报表文件。
     *
     * @param joinPoint      连接点对象，表示被拦截的方法
     * @param jasperResponse Jasper响应注解对象，包含报表生成的相关配置信息
     * @return 方法执行结果或异步任务标识
     * @throws Throwable 方法执行过程中可能抛出的异常
     */
    @Override
    @Around("@annotation(jasperResponse)")
    public Object around(ProceedingJoinPoint joinPoint, JasperResponse jasperResponse) throws Throwable {
        return super.around(joinPoint, jasperResponse);
    }

}
