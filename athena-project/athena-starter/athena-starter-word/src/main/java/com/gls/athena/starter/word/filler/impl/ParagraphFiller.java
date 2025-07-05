package com.gls.athena.starter.word.filler.impl;

import com.gls.athena.starter.word.filler.DocumentFiller;
import com.gls.athena.starter.word.processor.PlaceholderProcessorFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 段落填充器
 * 负责填充文档中的段落内容
 *
 * @author athena
 */
@Slf4j
@Component
public class ParagraphFiller implements DocumentFiller {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([^}]+)}");

    @Autowired
    private PlaceholderProcessorFactory processorFactory;

    @Override
    public void fill(XWPFDocument document, Map<String, Object> data) {
        // 填充主体段落
        fillParagraphs(document.getParagraphs(), data);

        // 填充页眉段落
        for (XWPFHeader header : document.getHeaderList()) {
            fillParagraphs(header.getParagraphs(), data);
        }

        // 填充页脚段落
        for (XWPFFooter footer : document.getFooterList()) {
            fillParagraphs(footer.getParagraphs(), data);
        }
    }

    @Override
    public boolean supports(Object element) {
        return element instanceof XWPFParagraph;
    }

    private void fillParagraphs(List<XWPFParagraph> paragraphs, Map<String, Object> data) {
        for (XWPFParagraph paragraph : paragraphs) {
            fillParagraph(paragraph, data);
        }
    }

    /**
     * 填充单个段落（包可见，供TableFiller使用）
     *
     * @param paragraph 段落对象
     * @param data      数据Map
     */
    void fillParagraph(XWPFParagraph paragraph, Map<String, Object> data) {
        String fullText = getParagraphText(paragraph);
        if (!StringUtils.hasText(fullText)) {
            return;
        }

        // 检查是否包含占位符
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(fullText);
        if (!matcher.find()) {
            return;
        }

        // 处理占位符
        String newText = replacePlaceholders(fullText, data);

        // 保留第一个run的格式，清除其他runs
        List<XWPFRun> runs = paragraph.getRuns();
        if (!runs.isEmpty()) {
            XWPFRun firstRun = runs.get(0);

            // 清除所有runs
            clearParagraphRuns(paragraph);

            // 创建新的run并复制格式
            XWPFRun newRun = paragraph.createRun();
            copyRunFormat(firstRun, newRun);
            newRun.setText(newText);
        } else {
            // 如果没有run，直接创建新的
            XWPFRun newRun = paragraph.createRun();
            newRun.setText(newText);
        }
    }

    private String getParagraphText(XWPFParagraph paragraph) {
        StringBuilder fullText = new StringBuilder();
        for (XWPFRun run : paragraph.getRuns()) {
            String text = run.getText(0);
            if (text != null) {
                fullText.append(text);
            }
        }
        return fullText.toString();
    }

    private String replacePlaceholders(String text, Map<String, Object> data) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        StringBuilder result = new StringBuilder();
        int lastEnd = 0;

        while (matcher.find()) {
            String placeholder = matcher.group(1);

            // 添加占位符前的文本
            result.append(text, lastEnd, matcher.start());

            // 使用处理器工厂处理占位符
            String replacement = processorFactory.process(placeholder, data);
            result.append(replacement);

            lastEnd = matcher.end();
        }

        result.append(text.substring(lastEnd));
        return result.toString();
    }

    private void clearParagraphRuns(XWPFParagraph paragraph) {
        int runCount = paragraph.getRuns().size();
        for (int i = runCount - 1; i >= 0; i--) {
            paragraph.removeRun(i);
        }
    }

    private void copyRunFormat(XWPFRun source, XWPFRun target) {
        if (source.getFontFamily() != null) {
            target.setFontFamily(source.getFontFamily());
        }
        if (source.getFontSizeAsDouble() != null) {
            target.setFontSize(source.getFontSizeAsDouble());
        }
        target.setBold(source.isBold());
        target.setItalic(source.isItalic());
        target.setUnderline(source.getUnderline());
        target.setStrikeThrough(source.isStrikeThrough());
        if (source.getColor() != null) {
            target.setColor(source.getColor());
        }
    }
}
