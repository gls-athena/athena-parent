package com.gls.athena.starter.pdf.support;

import com.gls.athena.starter.file.base.BaseFileResponseHandler;
import com.gls.athena.starter.pdf.annotation.PdfResponse;
import com.gls.athena.starter.pdf.generator.PdfGenerator;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * PDF响应处理器（优化版）
 * 用于处理PDF文件生成和响应的处理器，继承自基础文件响应处理器
 *
 * @author george
 */
@Slf4j
public class PdfResponseHandler extends BaseFileResponseHandler<PdfGenerator, PdfResponse> {

    /**
     * 构造函数，初始化PDF响应处理器
     *
     * @param pdfGenerators PDF生成器列表，用于处理PDF文件生成
     */
    public PdfResponseHandler(List<PdfGenerator> pdfGenerators) {
        super(pdfGenerators);
    }

    /**
     * 获取响应注解的类型类
     *
     * @return 返回PdfResponse类对象，表示该处理器处理的响应注解类型
     */
    @Override
    protected Class<PdfResponse> getResponseClass() {
        return PdfResponse.class;
    }

}

