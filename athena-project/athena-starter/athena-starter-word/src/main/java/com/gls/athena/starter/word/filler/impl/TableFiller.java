package com.gls.athena.starter.word.filler.impl;

import com.gls.athena.starter.word.filler.DocumentFiller;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 表格填充器
 * 负责填充文档中的表格内容，支持动态表格和静态表格
 *
 * @author athena
 */
@Slf4j
@Component
public class TableFiller implements DocumentFiller {

    @Autowired
    private ParagraphFiller paragraphFiller;

    @Override
    public void fill(XWPFDocument document, Map<String, Object> data) {
        // 填充主体表格
        fillTables(document.getTables(), data);

        // 填充页眉表格
        for (XWPFHeader header : document.getHeaderList()) {
            fillTables(header.getTables(), data);
        }

        // 填充页脚表格
        for (XWPFFooter footer : document.getFooterList()) {
            fillTables(footer.getTables(), data);
        }
    }

    @Override
    public boolean supports(Object element) {
        return element instanceof XWPFTable;
    }

    private void fillTables(List<XWPFTable> tables, Map<String, Object> data) {
        for (XWPFTable table : tables) {
            fillTable(table, data);
        }
    }

    private void fillTable(XWPFTable table, Map<String, Object> data) {
        List<XWPFTableRow> rows = table.getRows();
        if (rows.isEmpty()) {
            return;
        }

        // 检查是否为动态表格（包含列表数据）
        if (isDynamicTable(table)) {
            fillDynamicTable(table, data);
        } else {
            // 普通表格填充
            fillStaticTable(table, data);
        }
    }

    private boolean isDynamicTable(XWPFTable table) {
        // 查找表格中是否包含列表占位符模式 ${list:dataKey}
        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                String cellText = getCellText(cell);
                if (cellText.contains("${list:")) {
                    return true;
                }
            }
        }
        return false;
    }

    private void fillDynamicTable(XWPFTable table, Map<String, Object> data) {
        List<XWPFTableRow> rows = table.getRows();
        XWPFTableRow templateRow = null;
        String listKey = null;
        int templateRowIndex = -1;

        // 查找模板行
        for (int i = 0; i < rows.size(); i++) {
            XWPFTableRow row = rows.get(i);
            String rowText = getRowText(row);
            Pattern listPattern = Pattern.compile("\\$\\{list:([^}]+)}");
            Matcher matcher = listPattern.matcher(rowText);
            if (matcher.find()) {
                templateRow = row;
                listKey = matcher.group(1);
                templateRowIndex = i;
                break;
            }
        }

        if (templateRow == null || listKey == null) {
            // 没有找到列表模板，按普通表格处理
            fillStaticTable(table, data);
            return;
        }

        // 获取列表数据
        Object listData = getNestedValue(data, listKey);
        if (!(listData instanceof Collection<?> items)) {
            log.warn("List key '{}' does not point to a Collection", listKey);
            return;
        }

        // 清除原模板行中的列表占位符
        clearListPlaceholders(templateRow);

        // 为每个数据项创建新行
        int currentIndex = templateRowIndex;
        for (Object item : items) {
            XWPFTableRow newRow;
            if (currentIndex == templateRowIndex) {
                // 第一行使用原模板行
                newRow = templateRow;
            } else {
                // 创建新行
                newRow = table.insertNewTableRow(currentIndex);
                copyRowStructure(templateRow, newRow);
            }

            // 填充行数据
            Map<String, Object> itemData = new HashMap<>(data);
            if (item instanceof Map) {
                itemData.putAll((Map<String, Object>) item);
            } else {
                itemData.put("item", item);
                // 将对象属性添加到数据中
                Map<String, Object> itemFields = convertToMap(item);
                itemData.putAll(itemFields);
            }

            fillTableRow(newRow, itemData);
            currentIndex++;
        }

        // 如果没有数据，移除模板行
        if (items.isEmpty()) {
            table.removeRow(templateRowIndex);
        }
    }

    private void fillStaticTable(XWPFTable table, Map<String, Object> data) {
        for (XWPFTableRow row : table.getRows()) {
            fillTableRow(row, data);
        }
    }

    private void fillTableRow(XWPFTableRow row, Map<String, Object> data) {
        for (XWPFTableCell cell : row.getTableCells()) {
            // 直接使用段落填充器填充单元格内的段落
            for (XWPFParagraph paragraph : cell.getParagraphs()) {
                paragraphFiller.fillParagraph(paragraph, data);
            }
        }
    }

    private String getCellText(XWPFTableCell cell) {
        StringBuilder text = new StringBuilder();
        for (XWPFParagraph paragraph : cell.getParagraphs()) {
            text.append(getParagraphText(paragraph));
        }
        return text.toString();
    }

    private String getRowText(XWPFTableRow row) {
        StringBuilder text = new StringBuilder();
        for (XWPFTableCell cell : row.getTableCells()) {
            text.append(getCellText(cell));
        }
        return text.toString();
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

    private void clearListPlaceholders(XWPFTableRow row) {
        for (XWPFTableCell cell : row.getTableCells()) {
            for (XWPFParagraph paragraph : cell.getParagraphs()) {
                String text = getParagraphText(paragraph);
                // 移除 ${list:key} 占位符
                String cleanText = text.replaceAll("\\$\\{list:[^}]+}", "");
                if (!text.equals(cleanText)) {
                    clearParagraphRuns(paragraph);
                    if (!cleanText.trim().isEmpty()) {
                        XWPFRun newRun = paragraph.createRun();
                        newRun.setText(cleanText);
                    }
                }
            }
        }
    }

    private void copyRowStructure(XWPFTableRow sourceRow, XWPFTableRow targetRow) {
        List<XWPFTableCell> sourceCells = sourceRow.getTableCells();

        // 确保目标行有足够的单元格
        while (targetRow.getTableCells().size() < sourceCells.size()) {
            targetRow.createCell();
        }

        List<XWPFTableCell> targetCells = targetRow.getTableCells();

        for (int i = 0; i < sourceCells.size() && i < targetCells.size(); i++) {
            XWPFTableCell sourceCell = sourceCells.get(i);
            XWPFTableCell targetCell = targetCells.get(i);

            // 复制单元格内容和格式
            copyCellContent(sourceCell, targetCell);
        }
    }

    private void copyCellContent(XWPFTableCell sourceCell, XWPFTableCell targetCell) {
        // 清除目标单元格现有内容
        targetCell.removeParagraph(0);

        // 复制段落
        for (XWPFParagraph sourceParagraph : sourceCell.getParagraphs()) {
            XWPFParagraph targetParagraph = targetCell.addParagraph();
            copyParagraphContent(sourceParagraph, targetParagraph);
        }
    }

    private void copyParagraphContent(XWPFParagraph source, XWPFParagraph target) {
        // 复制段落级别的格式
        target.setAlignment(source.getAlignment());
        target.setSpacingBetween(source.getSpacingBetween());

        // 复制runs
        for (XWPFRun sourceRun : source.getRuns()) {
            XWPFRun targetRun = target.createRun();
            copyRunFormat(sourceRun, targetRun);

            String text = sourceRun.getText(0);
            if (text != null) {
                targetRun.setText(text);
            }
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

    private void clearParagraphRuns(XWPFParagraph paragraph) {
        int runCount = paragraph.getRuns().size();
        for (int i = runCount - 1; i >= 0; i--) {
            paragraph.removeRun(i);
        }
    }

    private Object getNestedValue(Map<String, Object> data, String key) {
        if (key == null || key.trim().isEmpty()) {
            return null;
        }

        String[] parts = key.split("\\.");
        Object current = data;

        for (String part : parts) {
            if (current == null) {
                return null;
            }

            if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(part);
            } else {
                try {
                    java.lang.reflect.Field field = current.getClass().getDeclaredField(part);
                    field.setAccessible(true);
                    current = field.get(current);
                } catch (Exception e) {
                    return null;
                }
            }
        }

        return current;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> convertToMap(Object data) {
        if (data == null) {
            return new HashMap<>();
        }

        if (data instanceof Map) {
            return (Map<String, Object>) data;
        }

        Map<String, Object> result = new HashMap<>();
        try {
            java.lang.reflect.Field[] fields = data.getClass().getDeclaredFields();
            for (java.lang.reflect.Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(data);
                result.put(field.getName(), value);
            }
        } catch (IllegalAccessException e) {
            log.warn("Failed to convert object to map", e);
        }

        return result;
    }
}
