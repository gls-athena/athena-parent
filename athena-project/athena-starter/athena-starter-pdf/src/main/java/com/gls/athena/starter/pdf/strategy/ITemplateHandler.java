package com.gls.athena.starter.pdf.strategy;

import com.gls.athena.starter.pdf.annotation.PdfResponse;
import com.gls.athena.starter.pdf.config.PdfTemplateType;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * PDF模板处理策略接口
 *
 * @author george
 */
public interface ITemplateHandler {

    /**
     * 处理PDF模板并输出
     *
     * @param data         数据映射
     * @param outputStream 输出流
     * @param pdfResponse  PDF响应注解
     * @throws IOException 如果处理过程中发生I/O错误
     */
    void handle(Map<String, Object> data, OutputStream outputStream, PdfResponse pdfResponse) throws IOException;

    /**
     * 是否支持此类型的模板
     *
     * @param templateType 模板类型
     * @return 是否支持
     */
    boolean supports(PdfTemplateType templateType);
}
