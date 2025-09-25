package com.gls.athena.starter.excel.async;

import com.gls.athena.common.core.constant.FileTypeEnums;
import com.gls.athena.starter.async.manager.IAsyncTaskManager;
import com.gls.athena.starter.excel.annotation.ExcelResponse;
import com.gls.athena.starter.excel.generator.ExcelGenerator;
import com.gls.athena.starter.file.base.BaseFileAsyncAspect;
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

    /**
     * 获取当前任务的编码标识
     *
     * @return 返回固定的任务编码 "excel_export"
     */
    @Override
    protected String getCode() {
        return "excel_export";
    }

    /**
     * 获取当前任务的名称描述
     *
     * @return 返回固定的任务名称 "Excel导出"
     */
    @Override
    protected String getName() {
        return "Excel导出";
    }

    /**
     * 获取当前任务的详细描述信息
     *
     * @return 返回固定的任务描述 "Excel异步导出任务"
     */
    @Override
    protected String getDescription() {
        return "Excel异步导出任务";
    }

    /**
     * 根据注解配置生成文件名（包含扩展名）
     *
     * @param excelResponse Excel响应注解对象，包含文件名和Excel类型配置
     * @return 完整的文件名，格式为：filename + excelType扩展名
     */
    @Override
    protected String getFilename(ExcelResponse excelResponse) {
        return excelResponse.filename() + excelResponse.excelType().getValue();
    }

    /**
     * 根据注解中的Excel类型获取对应的文件类型枚举
     *
     * @param excelResponse Excel响应注解对象，包含Excel类型配置
     * @return 对应的文件类型枚举值
     */
    @Override
    protected FileTypeEnums getFileType(ExcelResponse excelResponse) {
        return FileTypeEnums.getFileEnums(excelResponse.excelType().getValue());
    }

    /**
     * 判断当前请求是否需要异步处理
     *
     * @param excelResponse Excel响应注解对象，包含异步处理开关配置
     * @return true表示需要异步处理，false表示同步处理
     */
    @Override
    protected boolean isAsync(ExcelResponse excelResponse) {
        return excelResponse.async();
    }
}
