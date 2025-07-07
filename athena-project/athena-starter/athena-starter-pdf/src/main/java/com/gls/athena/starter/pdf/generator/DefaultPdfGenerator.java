package com.gls.athena.starter.pdf.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gls.athena.starter.pdf.config.PdfProperties;
import com.gls.athena.starter.pdf.config.TemplateType;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

/**
 * 默认数据PDF生成器（无模板）
 *
 * @author athena
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultPdfGenerator implements PdfGenerator {

    private final PdfProperties pdfProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void generate(Object data, String template, TemplateType templateType, OutputStream outputStream) throws Exception {
        Document document = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // 添加标题
            addTitle(document);

            // 添加数据内容
            Map<String, Object> dataMap = convertToMap(data);
            addDataContent(document, dataMap);

        } catch (Exception e) {
            log.error("生成默认PDF文档失败", e);
            throw new RuntimeException("生成PDF文档失败", e);
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }

    @Override
    public boolean supports(TemplateType templateType) {
        return templateType == TemplateType.DATA ||
                templateType == TemplateType.AUTO;
    }

    /**
     * 添加标题
     */
    private void addTitle(Document document) throws DocumentException {
        Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD, Color.BLUE);
        Paragraph title = new Paragraph("数据报告", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);
    }

    /**
     * 添加数据内容
     */
    private void addDataContent(Document document, Map<String, Object> dataMap) throws DocumentException {
        if (dataMap.isEmpty()) {
            Paragraph emptyParagraph = new Paragraph("暂无数据", new Font(Font.HELVETICA, 12));
            emptyParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(emptyParagraph);
            return;
        }

        // 创建表格
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);

        // 设置列宽
        float[] columnWidths = {1f, 3f, 1f};
        table.setWidths(columnWidths);

        // 添加表头
        addTableHeader(table);

        // 添加数据行
        for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
            addTableRow(table, entry.getKey(), entry.getValue());
        }

        document.add(table);
    }

    /**
     * 添加表头
     */
    private void addTableHeader(PdfPTable table) {
        Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE);

        PdfPCell fieldHeader = new PdfPCell(new Phrase("字段名", headerFont));
        fieldHeader.setBackgroundColor(new Color(70, 130, 180));
        fieldHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
        fieldHeader.setPadding(10);
        table.addCell(fieldHeader);

        PdfPCell valueHeader = new PdfPCell(new Phrase("值", headerFont));
        valueHeader.setBackgroundColor(new Color(70, 130, 180));
        valueHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
        valueHeader.setPadding(10);
        table.addCell(valueHeader);

        PdfPCell typeHeader = new PdfPCell(new Phrase("类型", headerFont));
        typeHeader.setBackgroundColor(new Color(70, 130, 180));
        typeHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
        typeHeader.setPadding(10);
        table.addCell(typeHeader);
    }

    /**
     * 添加表格行
     */
    private void addTableRow(PdfPTable table, String fieldName, Object value) {
        Font cellFont = new Font(Font.HELVETICA, 10);

        // 字段名
        PdfPCell fieldCell = new PdfPCell(new Phrase(fieldName, cellFont));
        fieldCell.setPadding(8);
        fieldCell.setBackgroundColor(new Color(245, 245, 245));
        table.addCell(fieldCell);

        // 值
        PdfPCell valueCell = new PdfPCell(new Phrase(formatValue(value), cellFont));
        valueCell.setPadding(8);
        table.addCell(valueCell);

        // 类型
        PdfPCell typeCell = new PdfPCell(new Phrase(getValueType(value), cellFont));
        typeCell.setPadding(8);
        typeCell.setBackgroundColor(new Color(245, 245, 245));
        table.addCell(typeCell);
    }

    /**
     * 格式化值
     */
    private String formatValue(Object value) {
        if (value == null) {
            return "";
        }

        if (value instanceof Collection) {
            Collection<?> collection = (Collection<?>) value;
            return "集合(" + collection.size() + "个元素)";
        }

        if (value instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) value;
            return "对象(" + map.size() + "个属性)";
        }

        return String.valueOf(value);
    }

    /**
     * 获取值类型
     */
    private String getValueType(Object value) {
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
