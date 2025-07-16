package com.gls.athena.starter.word.generator;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gls.athena.starter.word.annotation.WordResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

/**
 * 默认Word文档生成器（无模板）。
 * <p>
 * 该生成器将数据以表格形式导出到Word文档，适用于无模板的简单导出场景。
 * </p>
 *
 * @author athena
 */
@Slf4j
@Component
public class DefaultWordGenerator implements WordGenerator {

    /**
     * Jackson对象映射器，用于对象与Map的转换。
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 生成Word文档内容，将数据以表格形式写入。
     *
     * @param document Word文档对象
     * @param dataMap  需要导出的数据，key为字段名，value为字段值
     */
    private void generateContent(XWPFDocument document, Map<String, Object> dataMap) {
        XWPFTable table = document.createTable();
        // 设置表头：字段名、值
        XWPFTableRow headerRow = table.getRow(0);
        headerRow.getCell(0).setText("字段名");
        headerRow.addNewTableCell().setText("值");

        for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
            addRow(table, entry.getKey(), entry.getValue(), 0);
        }
        // 设置表格样式
        beautifyTable(table);
    }

    /**
     * 递归添加行，支持嵌套结构
     */
    private void addRow(XWPFTable table, String key, Object value, int level) {
        XWPFTableRow dataRow = table.createRow();
        String indent = "  ".repeat(level);
        dataRow.getCell(0).setText(indent + key);
        if (value instanceof Map<?, ?> map) {
            dataRow.getCell(1).setText("对象(" + map.size() + "个属性)");
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                addRow(table, String.valueOf(entry.getKey()), entry.getValue(), level + 1);
            }
        } else if (value instanceof Collection<?> collection) {
            dataRow.getCell(1).setText("集合(" + collection.size() + "个元素)");
            int idx = 0;
            for (Object item : collection) {
                addRow(table, "[" + idx++ + "]", item, level + 1);
            }
        } else {
            dataRow.getCell(1).setText(formatValue(value));
        }
    }

    /**
     * 美化表格样式
     */
    private void beautifyTable(XWPFTable table) {
        table.setWidth("10000");
        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                cell.getCTTc().addNewTcPr().addNewShd().setFill("E7E6E6");
                cell.getParagraphs().getFirst().setSpacingAfter(0);
            }
        }
    }

    /**
     * 格式化值
     */
    private String formatValue(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof Collection<?> collection) {
            return "集合(" + collection.size() + "个元素)";
        }
        if (value instanceof Map<?, ?> map) {
            return "对象(" + map.size() + "个属性)";
        }
        return String.valueOf(value);
    }

    /**
     * 将数据对象转换为Map
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> convertToMap(Object data) {
        if (data == null) {
            return Map.of();
        }

        if (data instanceof Map) {
            return (Map<String, Object>) data;
        }

        // 使用Jackson转换为Map
        return objectMapper.convertValue(data, Map.class);
    }

    @Override
    public void generate(Object data, WordResponse template, OutputStream outputStream) throws Exception {
        try (XWPFDocument document = new XWPFDocument()) {

            // 添加标题
            XWPFParagraph titleParagraph = document.createParagraph();
            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setText("数据报告");
            titleRun.setBold(true);
            titleRun.setFontSize(16);

            // 添加空行
            document.createParagraph();

            // 转换数据并生成内容
            Map<String, Object> dataMap = convertToMap(data);
            generateContent(document, dataMap);

            // 输出文档
            document.write(outputStream);

        } catch (Exception e) {
            log.error("生成默认Word文档失败", e);
            throw new RuntimeException("生成Word文档失败", e);
        }
    }

    @Override
    public boolean supports(WordResponse wordResponse) {
        return StrUtil.isBlank(wordResponse.template()) && wordResponse.generator() == WordGenerator.class;
    }
}
