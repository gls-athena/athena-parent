package com.gls.athena.starter.word.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.util.Map;

/**
 * 自定义Excel风格的Word生成器示例
 *
 * @author athena
 */
@Slf4j
@Component
public class ExcelStyleWordGenerator implements WordGenerator {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void generate(Object data, String template, OutputStream outputStream) throws Exception {
        try (XWPFDocument document = new XWPFDocument()) {

            // 添加标题
            XWPFParagraph titleParagraph = document.createParagraph();
            titleParagraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setText("数据报表（Excel风格）");
            titleRun.setBold(true);
            titleRun.setFontSize(18);
            titleRun.setColor("2E75B6");

            // 添加空行
            document.createParagraph();

            // 转换数据并生成表格
            Map<String, Object> dataMap = convertToMap(data);
            generateExcelStyleTable(document, dataMap);

            // 输出文档
            document.write(outputStream);

        } catch (Exception e) {
            log.error("生成Excel风格Word文档失败", e);
            throw new RuntimeException("生成Word文档失败", e);
        }
    }

    @Override
    public boolean supports(String template) {
        // 这个生成器总是可用的
        return true;
    }

    /**
     * 生成Excel风格的表格
     */
    private void generateExcelStyleTable(XWPFDocument document, Map<String, Object> dataMap) {
        if (dataMap.isEmpty()) {
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText("暂无数据");
            return;
        }

        // 创建表格
        XWPFTable table = document.createTable();
        table.setWidth("100%");

        // 设置表格样式
        table.getCTTbl().getTblPr().addNewTblBorders();

        // 设置表头
        XWPFTableRow headerRow = table.getRow(0);
        setupHeaderCell(headerRow.getCell(0), "序号");
        setupHeaderCell(headerRow.addNewTableCell(), "字段名");
        setupHeaderCell(headerRow.addNewTableCell(), "值");
        setupHeaderCell(headerRow.addNewTableCell(), "数据类型");

        // 添加数据行
        int index = 1;
        for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
            XWPFTableRow dataRow = table.createRow();

            // 序号
            setupDataCell(dataRow.getCell(0), String.valueOf(index++));
            // 字段名
            setupDataCell(dataRow.getCell(1), entry.getKey());
            // 值
            setupDataCell(dataRow.getCell(2), formatValue(entry.getValue()));
            // 数据类型
            setupDataCell(dataRow.getCell(3), getDataType(entry.getValue()));
        }
    }

    /**
     * 设置表头单元格样式
     */
    private void setupHeaderCell(XWPFTableCell cell, String text) {
        cell.setColor("4F81BD");
        XWPFParagraph paragraph = cell.getParagraphs().get(0);
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setBold(true);
        run.setColor("FFFFFF");
        run.setFontSize(12);
    }

    /**
     * 设置数据单元格样式
     */
    private void setupDataCell(XWPFTableCell cell, String text) {
        XWPFParagraph paragraph = cell.getParagraphs().get(0);
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setFontSize(10);

        // 交替行颜色
        if (cell.getTableRow().getTable().getRows().indexOf(cell.getTableRow()) % 2 == 0) {
            cell.setColor("F2F2F2");
        }
    }

    /**
     * 格式化值
     */
    private String formatValue(Object value) {
        if (value == null) {
            return "";
        }
        return String.valueOf(value);
    }

    /**
     * 获取数据类型
     */
    private String getDataType(Object value) {
        if (value == null) {
            return "null";
        }
        return value.getClass().getSimpleName();
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
}
