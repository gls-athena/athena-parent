package com.gls.athena.starter.word.converter;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.jsoup.nodes.Element;

/**
 * HTML标签转换器接口
 * 负责将HTML元素转换为Word文档元素
 *
 * @author lizy19
 */
public interface HtmlTagConverter {

    /**
     * 判断当前转换器是否支持给定的HTML标签
     *
     * @param tagName HTML标签名称（小写）
     * @return 如果支持该标签返回true，否则返回false
     */
    boolean supports(String tagName);

    /**
     * 将HTML元素转换为Word文档元素
     *
     * @param element  HTML元素
     * @param document Word文档对象
     */
    void convert(Element element, XWPFDocument document);
}
