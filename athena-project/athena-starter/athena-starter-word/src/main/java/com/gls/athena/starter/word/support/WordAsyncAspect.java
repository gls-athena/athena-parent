package com.gls.athena.starter.word.support;

import com.gls.athena.starter.async.manager.IAsyncTaskManager;
import com.gls.athena.starter.file.manager.IFileManager;
import com.gls.athena.starter.file.support.FileAsyncAspect;
import com.gls.athena.starter.word.annotation.WordResponse;
import com.gls.athena.starter.word.generator.WordGenerator;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * Word文件异步处理切面类
 * 该类继承自BaseFileAsyncAspect，用于处理Word文件生成的异步操作
 * 通过AOP切面拦截带有@WordResponse注解的方法，实现异步文件生成和处理
 *
 * @author george
 */
@Aspect
@Component
public class WordAsyncAspect extends FileAsyncAspect<WordGenerator, WordResponse> {

    /**
     * 构造函数
     * 初始化Word异步处理切面所需的依赖组件
     *
     * @param wordGenerators   Word生成器列表，用于处理不同类型的Word文件生成
     * @param asyncTaskManager 异步任务管理器，用于管理异步任务的执行
     * @param fileManager      文件管理器，用于文件的存储和管理
     * @param executor         线程池执行器，用于执行异步任务
     */
    public WordAsyncAspect(List<WordGenerator> wordGenerators, IAsyncTaskManager asyncTaskManager, IFileManager fileManager, Executor executor) {
        super(wordGenerators, asyncTaskManager, fileManager, executor);
    }

    /**
     * 环绕通知方法
     * 拦截带有@WordResponse注解的方法执行，实现异步处理逻辑
     *
     * @param joinPoint    连接点对象，包含被拦截方法的信息
     * @param wordResponse Word响应注解，包含Word文件处理的相关配置
     * @return 处理结果对象
     * @throws Throwable 方法执行异常
     */
    @Override
    @Around("@annotation(wordResponse)")
    public Object around(ProceedingJoinPoint joinPoint, WordResponse wordResponse) throws Throwable {
        return super.around(joinPoint, wordResponse);
    }

}
