package com.gls.athena.starter.pdf.strategy;

import java.io.OutputStream;
import java.util.Map;

/**
 * PDF处理策略接口
 *
 * @author george
 */
public interface PdfProcessingStrategy {

    /**
     * 处理PDF生成
     *
     * @param data         数据
     * @param template     模板名称
     * @param outputStream 输出流
     * @throws Exception 处理异常
     */
    void process(Map<String, Object> data, String template, OutputStream outputStream) throws Exception;

    /**
     * 获取支持的模板类型
     *
     * @return 模板类型
     */
    String getSupportedType();
}
