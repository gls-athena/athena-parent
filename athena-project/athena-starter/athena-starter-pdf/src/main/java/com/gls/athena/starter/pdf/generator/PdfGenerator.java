package com.gls.athena.starter.pdf.generator;

import com.gls.athena.starter.pdf.config.TemplateType;

import java.io.OutputStream;

/**
 * PDF文档生成器接口
 *
 * @author athena
 */
public interface PdfGenerator {

    /**
     * 生成PDF文档
     *
     * @param data         数据对象
     * @param template     模板路径
     * @param templateType 模板类型
     * @param outputStream 输出流
     * @throws Exception 生成异常
     */
    void generate(Object data, String template, TemplateType templateType, OutputStream outputStream) throws Exception;

    /**
     * 是否支持该模板类型
     *
     * @param templateType 模板类型
     * @return 是否支持
     */
    boolean supports(TemplateType templateType);
}
