package com.gls.athena.starter.pdf.generator;

import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gls.athena.starter.pdf.config.TemplateType;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openpdf.pdf.ITextRenderer;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.awt.*;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

/**
 * 统一的PDF生成器，支持数据和HTML模板两种模式
 *
 * @author athena
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UnifiedPdfGenerator implements PdfGenerator {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void generate(Object data, String template, TemplateType templateType, OutputStream outputStream) {
        try {
            if (isHtmlTemplate(template, templateType)) {
                generateHtmlPdf(data, template, outputStream);
            } else {
                generateDataPdf(data, outputStream);
            }
        } catch (Exception e) {
            log.error("PDF生成失败: template={}, type={}", template, templateType, e);
            throw new RuntimeException("PDF生成失败", e);
        }
    }

    @Override
    public boolean supports(TemplateType templateType) {
        return true;
    }

    /**
     * 判断是否为HTML模板
     */
    private boolean isHtmlTemplate(String template, TemplateType templateType) {
        if (templateType == TemplateType.HTML) {
            return true;
        }
        if (templateType == TemplateType.DATA) {
            return false;
        }
        // AUTO模式下自动判断
        return StringUtils.hasText(template) &&
                (template.toLowerCase().endsWith(".html") || template.toLowerCase().endsWith(".htm"));
    }

    /**
     * 生成HTML模板PDF
     */
    private void generateHtmlPdf(Object data, String template, OutputStream outputStream) {
        try {
            // 渲染HTML模板
            TemplateConfig config = new TemplateConfig("", TemplateConfig.ResourceMode.CLASSPATH);
            String templatePath = template.startsWith("classpath:") ?
                    template.substring("classpath:".length()) : template;

            String html = TemplateUtil.createEngine(config)
                    .getTemplate(templatePath)
                    .render(convertToMap(data));

            // HTML转PDF
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(outputStream);
            renderer.finishPDF();

            log.info("HTML模板PDF生成成功: {}", template);
        } catch (Exception e) {
            throw new RuntimeException("HTML模板PDF生成失败: " + template, e);
        }
    }

    /**
     * 生成数据PDF（无模板）
     */
    private void generateDataPdf(Object data, OutputStream outputStream) {
        try (Document document = new Document(PageSize.A4)) {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // 添加标题
            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD, Color.BLUE);
            Paragraph title = new Paragraph("数据报告", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // 添加数据内容
            Map<String, Object> dataMap = convertToMap(data);
            addDataTable(document, dataMap);

            log.info("数据PDF生成成功");
        } catch (Exception e) {
            throw new RuntimeException("数据PDF生成失败", e);
        }
    }

    /**
     * 添加数据表格
     */
    private void addDataTable(Document document, Map<String, Object> dataMap) throws DocumentException {
        if (dataMap.isEmpty()) {
            document.add(new Paragraph("暂无数据", new Font(Font.HELVETICA, 12)));
            return;
        }

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2f, 3f});

        // 表头
        Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE);
        addTableCell(table, "字段名", headerFont, new Color(70, 130, 180));
        addTableCell(table, "值", headerFont, new Color(70, 130, 180));

        // 数据行
        Font cellFont = new Font(Font.HELVETICA, 10);
        boolean isEven = false;
        for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
            Color bgColor = isEven ? new Color(245, 245, 245) : Color.WHITE;
            addTableCell(table, entry.getKey(), cellFont, bgColor);
            addTableCell(table, formatValue(entry.getValue()), cellFont, bgColor);
            isEven = !isEven;
        }

        document.add(table);
    }

    /**
     * 添加表格单元格
     */
    private void addTableCell(PdfPTable table, String text, Font font, Color bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bgColor);
        cell.setPadding(8);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    /**
     * 格式化值
     */
    private String formatValue(Object value) {
        if (value == null) {
            return "";
        }

        return switch (value) {
            case Collection<?> collection -> "集合(" + collection.size() + "个元素)";
            case Map<?, ?> map -> "对象(" + map.size() + "个属性)";
            default -> String.valueOf(value);
        };
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
        return objectMapper.convertValue(data, Map.class);
    }
}
