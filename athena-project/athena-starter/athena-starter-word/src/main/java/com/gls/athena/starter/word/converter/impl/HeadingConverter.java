package com.gls.athena.starter.word.converter.impl;

import com.gls.athena.starter.word.converter.HtmlTagConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 标题标签(h1-h6)转换器
 *
 * @author lizy19
 */
@Slf4j
@Component
public class HeadingConverter implements HtmlTagConverter {

    private static final List<String> SUPPORTED_TAGS = Arrays.asList("h1", "h2", "h3", "h4", "h5", "h6");

    @Override
    public boolean supports(String tagName) {
        return SUPPORTED_TAGS.contains(tagName);
    }

    @Override
    public void convert(Element element, XWPFDocument document) {
        String tagName = element.tagName().toLowerCase();
        XWPFParagraph heading = document.createParagraph();
        heading.setStyle(tagName.toUpperCase());
        XWPFRun headingRun = heading.createRun();
        headingRun.setText(element.text());
        headingRun.setBold(true);

        // 根据标题级别计算字体大小
        int fontSize = 20 - (Integer.parseInt(tagName.substring(1)) - 1) * 2;
        headingRun.setFontSize(fontSize);
    }
}
