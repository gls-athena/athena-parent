package com.gls.athena.starter.word.converter.impl;

import com.gls.athena.starter.word.converter.HtmlTagConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

/**
 * 列表标签(ul, ol)转换器
 *
 * @author lizy19
 */
@Slf4j
@Component
public class ListConverter implements HtmlTagConverter {

    private static final List<String> SUPPORTED_TAGS = Arrays.asList("ul", "ol");
    private static final int LIST_INDENTATION_LEFT = 720;

    @Override
    public boolean supports(String tagName) {
        return SUPPORTED_TAGS.contains(tagName);
    }

    @Override
    public void convert(Element element, XWPFDocument document) {
        boolean isOrdered = "ol".equals(element.tagName().toLowerCase());
        Elements items = element.select("li");

        for (Element item : items) {
            XWPFParagraph paragraph = document.createParagraph();
            paragraph.setIndentationLeft(LIST_INDENTATION_LEFT);
            paragraph.setNumID(getOrCreateNumbering(document, isOrdered));

            if (item.children().isEmpty()) {
                String text = item.text();
                if (!text.isEmpty()) {
                    XWPFRun run = paragraph.createRun();
                    run.setText(text);
                }
            } else {
                processParagraphContent(item, paragraph);
            }
        }
    }

    /**
     * 获取或创建编号样式
     *
     * @param document  文档对象
     * @param isOrdered 是否有序列表
     * @return 编号样式ID
     */
    private BigInteger getOrCreateNumbering(XWPFDocument document, boolean isOrdered) {
        // 简化实现，返回固定值
        return BigInteger.valueOf(isOrdered ? 1 : 2);
    }

    /**
     * 处理段落内容
     *
     * @param element   元素
     * @param paragraph 段落
     */
    private void processParagraphContent(Element element, XWPFParagraph paragraph) {
        // 简单处理，仅提取文本
        XWPFRun run = paragraph.createRun();
        run.setText(element.text());
    }
}
