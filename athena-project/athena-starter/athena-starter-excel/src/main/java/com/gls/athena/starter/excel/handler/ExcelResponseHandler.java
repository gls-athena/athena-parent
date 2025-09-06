package com.gls.athena.starter.excel.handler;

import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.gls.athena.common.bean.result.Result;
import com.gls.athena.starter.excel.annotation.ExcelResponse;
import com.gls.athena.starter.excel.generator.ExcelGeneratorManager;
import com.gls.athena.starter.excel.web.domain.ExcelAsyncRequest;
import com.gls.athena.starter.web.util.WebUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.io.OutputStream;
import java.util.Optional;

/**
 * Excel响应处理器，用于处理带有@ExcelResponse注解的方法返回值
 *
 * @author george
 */
@Slf4j
@RequiredArgsConstructor
public class ExcelResponseHandler implements HandlerMethodReturnValueHandler {

    private final ExcelGeneratorManager generatorManager;

    /**
     * 判断处理器是否支持处理该返回类型
     *
     * @param returnType 方法返回类型
     * @return 如果支持则返回true，否则返回false
     */
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.hasMethodAnnotation(ExcelResponse.class);
    }

    /**
     * 处理方法返回值，将数据导出为Excel文件
     *
     * @param returnValue  方法返回值
     * @param returnType   方法返回类型
     * @param mavContainer ModelAndView容器
     * @param webRequest   Web请求
     * @throws Exception 可能抛出的异常
     */
    @Override
    public void handleReturnValue(Object returnValue, @NonNull MethodParameter returnType,
                                  @NonNull ModelAndViewContainer mavContainer, @NonNull NativeWebRequest webRequest) throws Exception {
        // 标记请求已被处理，防止其他处理器继续处理
        mavContainer.setRequestHandled(true);

        // 获取@ExcelResponse注解配置
        ExcelResponse excelResponse = Optional.ofNullable(returnType.getMethodAnnotation(ExcelResponse.class))
                .orElseThrow(() -> new IllegalArgumentException("方法返回值必须使用@ExcelResponse注解标记"));

        // 根据注解配置的异步标记，选择同步或异步方式处理Excel导出
        if (excelResponse.async()) {
            excelAsyncHandle(returnValue, excelResponse, webRequest);
        } else {
            excelSyncHandle(returnValue, excelResponse, webRequest);
        }

    }

    /**
     * 异步处理Excel响应数据
     *
     * @param returnValue   需要处理的返回值对象
     * @param excelResponse Excel响应对象，用于存储处理结果
     * @param webRequest    原生Web请求对象，用于获取HTTP响应输出流
     */
    private void excelAsyncHandle(Object returnValue, ExcelResponse excelResponse, NativeWebRequest webRequest) {
        // 生成唯一任务ID并构建异步请求对象
        String taskId = IdUtil.randomUUID();
        ExcelAsyncRequest excelAsyncRequest = new ExcelAsyncRequest()
                .setTaskId(taskId)
                .setData(returnValue)
                .setExcelResponse(excelResponse);

        // 发布异步处理事件
        SpringUtil.publishEvent(excelAsyncRequest);

        log.info("异步处理Excel任务：{}", taskId);

        // 构造成功响应结果并写入HTTP响应
        Result<String> result = Result.success("任务已提交，请稍后查看", taskId);

        WebUtil.writeJson(webRequest, result);
    }

    /**
     * 处理Excel同步导出操作
     *
     * @param returnValue   方法返回值，用于生成Excel数据
     * @param excelResponse Excel响应注解，包含文件名和Excel类型等配置信息
     * @param webRequest    原生Web请求对象，用于获取HTTP响应输出流
     */
    private void excelSyncHandle(Object returnValue, ExcelResponse excelResponse, NativeWebRequest webRequest) {
        // 创建Excel文件输出流并生成Excel文件
        try (OutputStream outputStream = WebUtil.createOutputStream(webRequest, excelResponse.filename(), excelResponse.excelType().getValue())) {
            generatorManager.generate(returnValue, excelResponse, outputStream);
        } catch (Exception e) {
            log.error("导出Excel文件时发生错误", e);
        }
    }

}
