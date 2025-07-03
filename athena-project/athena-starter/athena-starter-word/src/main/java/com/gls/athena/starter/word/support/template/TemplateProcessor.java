package com.gls.athena.starter.word.support.template;

import com.gls.athena.starter.word.annotation.WordResponse;
import com.gls.athena.starter.word.config.WordTemplateType;

import java.io.OutputStream;
import java.util.Map;

/**
 * 模板处理器接口
 * 定义了不同类型模板处理的统一接口
 *
 * @author lizy19
 */
public interface TemplateProcessor {

    /**
     * 处理模板并输出结果
     *
     * @param data         包含填充模板所需数据的映射
     * @param outputStream 用于输出生成的文件的流
     * @param wordResponse 提供与Word相关的响应处理
     */
    void processTemplate(Map<String, Object> data, OutputStream outputStream, WordResponse wordResponse);

    /**
     * 判断是否支持处理指定类型的模板
     *
     * @param templateType 模板类型
     * @return 是否支持
     */
    boolean supports(WordTemplateType templateType);
}
