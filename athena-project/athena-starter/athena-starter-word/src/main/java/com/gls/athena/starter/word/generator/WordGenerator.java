package com.gls.athena.starter.word.generator;

import java.io.OutputStream;

/**
 * Word文档生成器接口
 *
 * @author athena
 */
public interface WordGenerator {

    /**
     * 生成Word文档
     *
     * @param data         数据对象
     * @param template     模板路径
     * @param outputStream 输出流
     * @throws Exception 生成异常
     */
    void generate(Object data, String template, OutputStream outputStream) throws Exception;

    /**
     * 是否支持该模板
     *
     * @param template 模板路径
     * @return 是否支持
     */
    boolean supports(String template);
}
