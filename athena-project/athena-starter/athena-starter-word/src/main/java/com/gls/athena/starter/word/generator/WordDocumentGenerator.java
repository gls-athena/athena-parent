package com.gls.athena.starter.word.generator;

import com.gls.athena.starter.word.annotation.WordResponse;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

/**
 * Word文档生成器接口
 *
 * @author athena
 */
public interface WordDocumentGenerator {

    /**
     * 生成Word文档
     *
     * @param data         数据对象
     * @param wordResponse 注解信息
     * @return Word文档对象
     */
    XWPFDocument generate(Object data, WordResponse wordResponse);

    /**
     * 检查是否支持处理该数据类型
     *
     * @param dataClass 数据类型
     * @return 是否支持
     */
    boolean supports(Class<?> dataClass);
}
