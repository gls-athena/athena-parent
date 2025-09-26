package com.gls.athena.starter.excel.handler;

import com.gls.athena.starter.excel.annotation.ExcelResponse;
import com.gls.athena.starter.excel.generator.ExcelGenerator;
import com.gls.athena.starter.file.base.BaseFileResponseHandler;

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

}

