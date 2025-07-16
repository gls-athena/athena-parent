package com.gls.athena.starter.pdf.generator;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.gls.athena.starter.pdf.annotation.PdfResponse;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

/**
 * 默认的PDF生成器实现
 * <p>
 * 该类负责将数据对象转换为PDF文档，支持简单的数据表格展示。
 * </p>
 *
 * @author athena
 */
@Slf4j
@Component
public class DefaultPdfGenerator implements PdfGenerator {
    /**
     * 生成PDF文档
     *
     * @param data         数据对象
     * @param pdfResponse  PDF响应注解，包含模板路径等信息
     * @param outputStream 输出流，用于写入生成的PDF文档
     * @throws Exception 生成PDF时可能抛出的异常
     */
    @Override
    public void generate(Object data, PdfResponse pdfResponse, OutputStream outputStream) throws Exception {
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
     * 添加数据表格到PDF文档
     *
     * @param document PDF文档对象
     * @param dataMap  数据映射，包含字段名和对应值
     */
    private void addDataTable(Document document, Map<String, Object> dataMap) {
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
     * 添加单元格到表格
     *
     * @param table   PDF表格对象
     * @param text    单元格文本内容
     * @param font    单元格字体样式
     * @param bgColor 单元格背景颜色
     */
    private void addTableCell(PdfPTable table, String text, Font font, Color bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bgColor);
        cell.setPadding(8);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    /**
     * 格式化数据值
     *
     * @param value 数据值
     * @return 格式化后的数据值
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
     * 将数据对象转换为Map格式，便于模板渲染
     *
     * @param data 数据对象
     * @return Map格式数据
     */
    private Map<String, Object> convertToMap(Object data) {
        if (data == null) {
            throw new IllegalArgumentException("数据对象不能为空");
        }
        if (data instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return BeanUtil.beanToMap(data);
    }

    /**
     * 判断当前生成器是否支持指定PDF响应注解
     *
     * @param pdfResponse PDF响应注解
     * @return true表示支持，false表示不支持
     */
    @Override
    public boolean supports(PdfResponse pdfResponse) {
        return StrUtil.isBlank(pdfResponse.template())
                && pdfResponse.generator().equals(PdfGenerator.class);
    }
}
