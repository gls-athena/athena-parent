package com.gls.athena.starter.excel.async;

import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.gls.athena.common.bean.result.Result;
import com.gls.athena.starter.excel.annotation.ExcelResponse;
import com.gls.athena.starter.excel.web.domain.ExcelAsyncRequest;
import com.gls.athena.starter.web.util.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Excel异步处理切面
 * <p>
 * 在Controller方法执行前判断是否需要异步处理Excel导出
 * 如果需要异步处理，则立即返回任务ID，然后在后台异步执行Controller方法获取数据并生成Excel
 * </p>
 *
 * @author george
 */
@Slf4j
@Aspect
@Component
public class ExcelAsyncAspect {

    /**
     * 环绕通知：拦截标注了@ExcelResponse的方法
     */
    @Around("@annotation(excelResponse)")
    public Object around(ProceedingJoinPoint joinPoint, ExcelResponse excelResponse) throws Throwable {

        // 获取@ExcelResponse注解
        if (excelResponse == null || !excelResponse.async()) {
            return joinPoint.proceed();
        }

        // 异步模式：立即返回任务ID，后台执行数据查询和Excel生成
        String taskId = IdUtil.randomUUID();

        ExcelAsyncRequest excelAsyncRequest = new ExcelAsyncRequest()
                .setTaskId(taskId)
                .setExcelResponse(excelResponse)
                .setJoinPoint(joinPoint);

        SpringUtil.publishEvent(excelAsyncRequest);

        log.info("异步Excel导出任务已提交，任务ID: {}, 方法: {}", taskId, joinPoint.getSignature().getName());

        // 立即响应客户端任务ID
        Result<String> result = Result.success("任务已提交，请稍后查看", taskId);
        WebUtil.writeJson(result);

        // 返回null，表示响应已经处理完成
        return null;
    }

}
