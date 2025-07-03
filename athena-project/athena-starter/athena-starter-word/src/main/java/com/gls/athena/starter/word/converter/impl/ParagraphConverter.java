package com.gls.athena.starter.word.converter.impl;

import com.gls.athena.starter.word.converter.HtmlTagConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 段落标签(p)转换器
 *
 * @author lizy19
 */
@Slf4j
@Component
public class ParagraphConverter implements HtmlTagConverter {

    private static final List<String> SUPPORTED_TAGS = Arrays.asList("p");

    @Override
    public boolean supports(String tagName) {
        return SUPPORTED_TAGS.contains(tagName);
    }

    @Override
    public void convert(Element element, XWPFDocument document) {
        XWPFParagraph paragraph = document.createParagraph();
        processParagraphContent(element, paragraph);
    }

    /**
     * 处理段落内容，支持内联样式
     * 此方法负责将HTML元素转换为Word文档中的段落内容，包括文本和内联样式
     *
     * @param element   Jsoup解析的HTML元素，包含段落内容和内联样式
     * @param paragraph POI库中的XWPFParagraph对象，用于创建和管理Word文档中的段落
     */
    private void processParagraphContent(Element element, XWPFParagraph paragraph) {
        // 检查元素是否没有子元素，如果是，则直接添加文本到段落中
        if (element.children().isEmpty()) {
            // 没有子元素，直接添加文本
            XWPFRun run = paragraph.createRun();
            run.setText(element.text());
            return;
        }

        // 处理混合内容，即文本和内联样式同时存在的情况
        for (Node node : element.childNodes()) {
            if (node instanceof TextNode) {
                // 处理文本节点
                String text = ((TextNode) node).text();
                if (!text.trim().isEmpty()) {
                    XWPFRun run = paragraph.createRun();
                    run.setText(text);
                }
            } else if (node instanceof Element) {
                processInlineElement((Element) node, paragraph);
            }
        }
    }

    /**
     * 处理内联样式元素
     *
     * @param element   内联样式元素
     * @param paragraph 段落对象
     */
    private void processInlineElement(Element element, XWPFParagraph paragraph) {
        // 创建一个运行并设置文本
        XWPFRun run = paragraph.createRun();
        run.setText(element.text());

        // 应用样式
        applyInlineStyle(element, run);
    }

    /**
     * 应用内联样式到运行对象
     *
     * @param element 内联元素
     * @param run     运行对象
     */
    private void applyInlineStyle(Element element, XWPFRun run) {
        String tagName = element.tagName().toLowerCase();

        // 根据不同的HTML标签应用相应的样式
        switch (tagName) {
            case "b":
            case "strong":
                run.setBold(true);
                break;
            case "i":
            case "em":
                run.setItalic(true);
                break;
            case "u":
                run.setUnderline(org.apache.poi.xwpf.usermodel.UnderlinePatterns.SINGLE);
                break;
            case "s":
            case "strike":
            case "del":
                run.setStrikeThrough(true);
                break;
            case "sub":
                run.setSubscript(org.apache.poi.xwpf.usermodel.VerticalAlign.SUBSCRIPT);
                break;
            case "sup":
                run.setSubscript(org.apache.poi.xwpf.usermodel.VerticalAlign.SUPERSCRIPT);
                break;
        }

        // 处理颜色属性
        String color = element.attr("color");
        if (!color.isEmpty()) {
            run.setColor(color.replace("#", ""));
        }

        // 处理字体大小属性
        String fontSize = element.attr("size");
        if (!fontSize.isEmpty()) {
            try {
                run.setFontSize(Integer.parseInt(fontSize));
            } catch (NumberFormatException e) {
                log.warn("无效的字体大小: {}", fontSize);
            }
        }
    }
}
