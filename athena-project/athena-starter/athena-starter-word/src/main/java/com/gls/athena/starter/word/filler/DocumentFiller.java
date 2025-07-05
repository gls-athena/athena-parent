package com.gls.athena.starter.word.filler;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.util.Map;

/**
 * 文档填充器接口
 * 使用模板方法模式定义文档填充流程
 *
 * @author athena
 */
public interface DocumentFiller {

    /**
     * 填充文档
     *
     * @param document 待填充的文档
     * @param data     数据上下文
     */
    void fill(XWPFDocument document, Map<String, Object> data);

    /**
     * 判断是否支持该类型的文档元素
     *
     * @param element 文档元素
     * @return 是否支持
     */
    boolean supports(Object element);
}
