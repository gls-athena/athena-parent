package com.gls.athena.starter.pdf.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gls.athena.starter.pdf.config.TemplateType;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.OutputStream;
import java.util.Map;

/**
 * 自定义报表风格的PDF生成器示例
 *
 * @author athena
 */
@Slf4j
@Component
public class ReportStylePdfGenerator implements PdfGenerator {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void generate(Object data, String template, TemplateType templateType, OutputStream outputStream) throws Exception {
        Document document = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // 添加报表标题
            addReportHeader(document);

            // 添加数据内容
            Map<String, Object> dataMap = convertToMap(data);
            addReportContent(document, dataMap);

            // 添加页脚
            addReportFooter(document);

        } catch (Exception e) {
            log.error("生成报表风格PDF文档失败", e);
            throw new RuntimeException("生成PDF文档失败", e);
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }

    @Override
    public boolean supports(TemplateType templateType) {
        // 这个生成器总是可用的
        return true;
    }

    /**
     * 添加报表标题
     */
    private void addReportHeader(Document document) throws DocumentException {
        // 标题
        Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD, new Color(0, 51, 102));
        Paragraph title = new Paragraph("数据分析报表", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10);
        document.add(title);

        // 副标题
        Font subTitleFont = new Font(Font.HELVETICA, 14, Font.NORMAL, Color.GRAY);
        Paragraph subTitle = new Paragraph("详细数据统计与分析", subTitleFont);
        subTitle.setAlignment(Element.ALIGN_CENTER);
        subTitle.setSpacingAfter(20);
        document.add(subTitle);

        // 分隔线
        Paragraph line = new Paragraph("_".repeat(80));
        line.setAlignment(Element.ALIGN_CENTER);
        line.setSpacingAfter(20);
        document.add(line);
    }

    /**
     * 添加报表内容
     */
    private void addReportContent(Document document, Map<String, Object> dataMap) throws DocumentException {
        if (dataMap.isEmpty()) {
            Paragraph emptyParagraph = new Paragraph("暂无数据", new Font(Font.HELVETICA, 12));
            emptyParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(emptyParagraph);
            return;
        }

        // 创建带边框的表格
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);

        // 设置列宽
        float[] columnWidths = {2f, 3f};
        table.setWidths(columnWidths);

        // 添加表头
        addReportTableHeader(table);

        // 添加数据行
        boolean isEvenRow = false;
        for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
            addReportTableRow(table, entry.getKey(), entry.getValue(), isEvenRow);
            isEvenRow = !isEvenRow;
        }

        document.add(table);
    }

    /**
     * 添加报表表头
     */
    private void addReportTableHeader(PdfPTable table) {
        Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE);

        PdfPCell fieldHeader = new PdfPCell(new Phrase("指标名称", headerFont));
        fieldHeader.setBackgroundColor(new Color(0, 51, 102));
        fieldHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
        fieldHeader.setPadding(12);
        fieldHeader.setBorderWidth(1);
        table.addCell(fieldHeader);

        PdfPCell valueHeader = new PdfPCell(new Phrase("数值", headerFont));
        valueHeader.setBackgroundColor(new Color(0, 51, 102));
        valueHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
        valueHeader.setPadding(12);
        valueHeader.setBorderWidth(1);
        table.addCell(valueHeader);
    }

    /**
     * 添加报表表格行
     */
    private void addReportTableRow(PdfPTable table, String fieldName, Object value, boolean isEvenRow) {
        Font cellFont = new Font(Font.HELVETICA, 11);
        Color backgroundColor = isEvenRow ? new Color(248, 249, 250) : Color.WHITE;

        // 字段名
        PdfPCell fieldCell = new PdfPCell(new Phrase(fieldName, cellFont));
        fieldCell.setPadding(10);
        fieldCell.setBackgroundColor(backgroundColor);
        fieldCell.setBorderWidth(0.5f);
        fieldCell.setBorderColor(Color.LIGHT_GRAY);
        table.addCell(fieldCell);

        // 值
        PdfPCell valueCell = new PdfPCell(new Phrase(formatValue(value), cellFont));
        valueCell.setPadding(10);
        valueCell.setBackgroundColor(backgroundColor);
        valueCell.setBorderWidth(0.5f);
        valueCell.setBorderColor(Color.LIGHT_GRAY);
        table.addCell(valueCell);
    }

    /**
     * 添加页脚
     */
    private void addReportFooter(Document document) throws DocumentException {
        // 添加空行
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));

        // 生成时间
        Font footerFont = new Font(Font.HELVETICA, 10, Font.ITALIC, Color.GRAY);
        Paragraph footer = new Paragraph("报表生成时间: " + java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), footerFont);
        footer.setAlignment(Element.ALIGN_RIGHT);
        document.add(footer);
    }

    /**
     * 格式化值
     */
    private String formatValue(Object value) {
        if (value == null) {
            return "-";
        }

        if (value instanceof Number) {
            return String.format("%.2f", ((Number) value).doubleValue());
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
}
