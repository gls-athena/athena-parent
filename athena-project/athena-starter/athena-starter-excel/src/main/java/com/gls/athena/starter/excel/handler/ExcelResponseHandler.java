package com.gls.athena.starter.excel.handler;

import com.gls.athena.starter.excel.annotation.ExcelResponse;
import com.gls.athena.starter.excel.generator.ExcelGenerator;
import com.gls.athena.starter.file.base.BaseFileResponseHandler;
import com.gls.athena.starter.web.util.WebUtil;
import org.springframework.web.context.request.NativeWebRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Excel响应处理器，用于处理带有@ExcelResponse注解的方法返回值
 *
 * @author george
 */
public class ExcelResponseHandler extends BaseFileResponseHandler<ExcelGenerator, ExcelResponse> {

    public ExcelResponseHandler(List<ExcelGenerator> excelGenerators) {
        super(excelGenerators);
    }

    /**
     * 判断Excel响应是否为异步处理
     *
     * @param excelResponse Excel响应注解对象
     * @return true表示异步处理，false表示同步处理
     */
    @Override
    protected boolean isAsync(ExcelResponse excelResponse) {
        return excelResponse.async();
    }

    /**
     * 获取响应注解的类型
     *
     * @return ExcelResponse注解类的Class对象
     */
    @Override
    protected Class<ExcelResponse> getResponseClass() {
        return ExcelResponse.class;
    }

    /**
     * 创建输出流用于写入Excel文件数据
     *
     * @param webRequest    原生Web请求对象
     * @param excelResponse Excel响应注解对象，包含文件名和Excel类型等信息
     * @return OutputStream 输出流对象
     * @throws IOException 当创建输出流失败时抛出
     */
    @Override
    protected OutputStream createOutputStream(NativeWebRequest webRequest, ExcelResponse excelResponse) throws IOException {
        return WebUtil.createOutputStream(webRequest, excelResponse.filename(), excelResponse.excelType().getValue());
    }
}

