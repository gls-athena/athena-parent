package com.gls.athena.starter.word.converter.impl;

import com.gls.athena.starter.word.converter.HtmlTagConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 表格标签(table)转换器
 *
 * @author lizy19
 */
@Slf4j
@Component
public class TableConverter implements HtmlTagConverter {

    private static final List<String> SUPPORTED_TAGS = Collections.singletonList("table");

    @Override
    public boolean supports(String tagName) {
        return SUPPORTED_TAGS.contains(tagName);
    }

    @Override
    public void convert(Element element, XWPFDocument document) {
        // 选择表格中的所有行
        Elements rows = element.select("tr");
        if (rows.isEmpty()) {
            return;
        }

        // 确定列数
        int numCols = rows.stream()
                .mapToInt(row -> row.select("td, th").size())
                .max()
                .orElse(0);

        if (numCols == 0) {
            return;
        }

        // 创建表格
        XWPFTable table = document.createTable(rows.size(), numCols);
        table.setWidth("100%");

        // 填充表格内容
        for (int i = 0; i < rows.size(); i++) {
            Element row = rows.get(i);
            Elements cells = row.select("td, th");

            for (int j = 0; j < cells.size(); j++) {
                Element cell = cells.get(j);
                XWPFTableCell tableCell = table.getRow(i).getCell(j);

                // 设置单元格内容
                if (cell.children().isEmpty()) {
                    tableCell.setText(cell.text());
                } else {
                    for (Element childElement : cell.children()) {
                        XWPFParagraph cellParagraph = tableCell.getParagraphArray(0);
                        if (cellParagraph == null) {
                            cellParagraph = tableCell.addParagraph();
                        }
                        processParagraphContent(childElement, cellParagraph);
                    }
                }

                // 表头单元格应用粗体
                if (cell.tagName().equalsIgnoreCase("th")) {
                    for (XWPFParagraph p : tableCell.getParagraphs()) {
                        for (XWPFRun run : p.getRuns()) {
                            run.setBold(true);
                        }
                    }
                }
            }
        }
    }

    /**
     * 处理段落内容
     *
     * @param element   段落元素
     * @param paragraph 段落对象
     */
    private void processParagraphContent(Element element, XWPFParagraph paragraph) {
        // 简单处理，仅提取文本
        XWPFRun run = paragraph.createRun();
        run.setText(element.text());
    }
}
