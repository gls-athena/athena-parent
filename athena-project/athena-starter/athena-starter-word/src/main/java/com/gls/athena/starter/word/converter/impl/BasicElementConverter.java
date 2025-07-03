package com.gls.athena.starter.word.converter.impl;

import com.gls.athena.starter.word.converter.HtmlTagConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.Borders;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 基础标签转换器，处理基础HTML元素如br、hr等
 *
 * @author lizy19
 */
@Slf4j
@Component
public class BasicElementConverter implements HtmlTagConverter {

    private static final List<String> SUPPORTED_TAGS = Arrays.asList("br", "hr", "div");

    @Override
    public boolean supports(String tagName) {
        return SUPPORTED_TAGS.contains(tagName);
    }

    @Override
    public void convert(Element element, XWPFDocument document) {
        String tagName = element.tagName().toLowerCase();

        switch (tagName) {
            case "br":
                // 添加空段落作为换行
                document.createParagraph();
                break;
            case "hr":
                // 添加水平线
                XWPFParagraph hr = document.createParagraph();
                hr.setBorderBottom(Borders.BASIC_THIN_LINES);
                break;
            case "div":
                // 对于div，递归处理其子元素
                // 注意：这部分逻辑应该在WordHelper中处理，因为需要调用processElement方法
                // 在这里我们仅添加一个空段落来表示div
                document.createParagraph();
                break;
        }
    }
}
