package com.gls.athena.starter.pdf.strategy;

import cn.hutool.core.util.StrUtil;
import com.gls.athena.starter.pdf.annotation.PdfResponse;
import com.gls.athena.starter.pdf.config.PdfTemplateType;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * PDF文档模板处理策略
 *
 * @author george
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PdfDocumentHandler implements ITemplateHandler {

    @Override
    public void handle(Map<String, Object> data, OutputStream outputStream, PdfResponse pdfResponse) throws IOException {
        // 加载模板
        InputStream template = new ClassPathResource(pdfResponse.template()).getInputStream();
        log.debug("加载PDF模板: {}", pdfResponse.template());
        // 填充数据到PDF模板
        fillPdfTemplate(template, data, outputStream);
    }

    @Override
    public boolean supports(PdfTemplateType templateType) {
        return PdfTemplateType.PDF.equals(templateType);
    }

    /**
     * 填充PDF模板表单字段并输出结果
     *
     * @param inputStream  包含PDF模板的输入流，必须是一个可读的PDF文件
     * @param data         包含字段名和对应值的映射，将用于填充PDF表单字段
     * @param outputStream 用于输出填充后PDF文档的输出流
     * @throws IOException 如果读取输入流或写入输出流时发生I/O错误
     */
    private void fillPdfTemplate(InputStream inputStream, Map<String, Object> data,
                                 OutputStream outputStream) throws IOException {
        // 初始化PDF文档处理器
        PdfReader reader = new PdfReader(inputStream);
        PdfStamper stamper = new PdfStamper(reader, outputStream);
        // 获取PDF表单字段并填充数据
        AcroFields fields = stamper.getAcroFields();

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // 处理动态表数据
            if (value instanceof List<?>) {
                processTableData(stamper, key, (List<?>) value);
            } else {
                // 处理普通字段
                String stringValue = StrUtil.toString(value);
                try {
                    fields.setField(key, stringValue);
                } catch (IOException e) {
                    log.warn("填充字段失败: {}", key, e);
                }
            }
        }

        // 扁平化表单字段并关闭资源
        stamper.setFormFlattening(true);
        stamper.close();
        reader.close();
    }

    /**
     * 处理动态表格数据
     *
     * @param stamper   PDF填充器
     * @param tableKey  表格的键名
     * @param tableData 表格数据列表
     */
    @SuppressWarnings("unchecked")
    private void processTableData(PdfStamper stamper, String tableKey, List<?> tableData) {
        if (tableData.isEmpty()) {
            log.debug("表格数据为空: {}", tableKey);
            return;
        }

        try {
            // 获取表格页面
            PdfContentByte canvas = stamper.getOverContent(1);

            // 假设第一行是表头或包含列名信息
            Map<String, Object> firstRow = (Map<String, Object>) tableData.get(0);

            // 表格起始位置和��式可以根据实际需求调整
            float startY = 500; // 表格起始Y坐标
            float startX = 50;  // 表格起始X坐标
            float rowHeight = 20; // 行高
            float[] columnWidths = calculateColumnWidths(firstRow, 500); // 列宽度，总宽度为500

            // 绘制表头
            drawTableHeader(canvas, firstRow, startX, startY, columnWidths, rowHeight);

            // 绘制表格内容
            float currentY = startY - rowHeight;
            for (Object rowData : tableData) {
                Map<String, Object> row = (Map<String, Object>) rowData;
                drawTableRow(canvas, row, startX, currentY, columnWidths, rowHeight);
                currentY -= rowHeight;
            }

            log.debug("成功填充表格数据: {}, 行数: {}", tableKey, tableData.size());
        } catch (Exception e) {
            log.error("填充表格数据失败: {}", tableKey, e);
        }
    }

    /**
     * 计算表格列宽
     *
     * @param firstRow   第一行数据
     * @param totalWidth 总宽度
     * @return 每列宽度的数组
     */
    private float[] calculateColumnWidths(Map<String, Object> firstRow, float totalWidth) {
        int columnCount = firstRow.size();
        float[] widths = new float[columnCount];
        float columnWidth = totalWidth / columnCount;

        Arrays.fill(widths, columnWidth);
        return widths;
    }

    /**
     * 计算浮点数组的总和
     *
     * @param array 浮点数组
     * @return 总和
     */
    private float sumArray(float[] array) {
        float sum = 0;
        for (float value : array) {
            sum += value;
        }
        return sum;
    }

    /**
     * 绘制表格表头
     *
     * @param canvas       PDF画布
     * @param headerRow    表头数据
     * @param startX       起始X坐标
     * @param startY       起始Y坐标
     * @param columnWidths 列宽数组
     * @param rowHeight    行高
     */
    private void drawTableHeader(PdfContentByte canvas, Map<String, Object> headerRow,
                                 float startX, float startY, float[] columnWidths, float rowHeight) {
        try {
            // 设置表头样式
            canvas.saveState();
            canvas.setLineWidth(1f);

            // 计算表格总宽度
            float totalWidth = sumArray(columnWidths);

            // ���制表头背景
            canvas.rectangle(startX, startY - rowHeight, totalWidth, rowHeight);
            canvas.setColorFill(Color.LIGHT_GRAY);
            canvas.fill();

            // 绘制表头边框
            canvas.setColorStroke(Color.BLACK);
            canvas.rectangle(startX, startY - rowHeight, totalWidth, rowHeight);
            canvas.stroke();

            // 绘制表头文本
            float x = startX;
            int index = 0;
            BaseFont bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            canvas.beginText();
            canvas.setFontAndSize(bf, 10);
            canvas.setColorFill(Color.BLACK);

            for (Map.Entry<String, Object> entry : headerRow.entrySet()) {
                // 绘制列分隔线
                if (index > 0) {
                    canvas.moveTo(x, startY);
                    canvas.lineTo(x, startY - rowHeight);
                    canvas.stroke();
                }

                // 绘制列名
                String columnName = entry.getKey();
                float textWidth = bf.getWidthPoint(columnName, 10);
                float textX = x + (columnWidths[index] - textWidth) / 2;
                canvas.showTextAligned(Element.ALIGN_CENTER, columnName, textX + columnWidths[index] / 2, startY - rowHeight / 2, 0);

                x += columnWidths[index];
                index++;
            }

            canvas.endText();
            canvas.restoreState();
        } catch (Exception e) {
            log.error("绘制表头失败", e);
        }
    }

    /**
     * 绘制表格行
     *
     * @param canvas       PDF画布
     * @param rowData      行数据
     * @param startX       起始X坐标
     * @param startY       起始Y坐标
     * @param columnWidths 列宽数组
     * @param rowHeight    行高
     */
    private void drawTableRow(PdfContentByte canvas, Map<String, Object> rowData,
                              float startX, float startY, float[] columnWidths, float rowHeight) {
        try {
            // 设置行样式
            canvas.saveState();
            canvas.setLineWidth(0.5f);

            // 计算表格总宽度
            float totalWidth = sumArray(columnWidths);

            // 绘制行边框
            canvas.rectangle(startX, startY - rowHeight, totalWidth, rowHeight);
            canvas.stroke();

            // 绘制行文本
            float x = startX;
            int index = 0;
            BaseFont bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            canvas.beginText();
            canvas.setFontAndSize(bf, 10);

            for (Map.Entry<String, Object> entry : rowData.entrySet()) {
                // 绘制列分隔线
                if (index > 0) {
                    canvas.moveTo(x, startY);
                    canvas.lineTo(x, startY - rowHeight);
                    canvas.stroke();
                }

                // 绘制单元格内容
                String cellValue = StrUtil.toString(entry.getValue());
                float textWidth = bf.getWidthPoint(cellValue, 10);
                float textX = x + (columnWidths[index] - textWidth) / 2;
                canvas.showTextAligned(Element.ALIGN_CENTER, cellValue, textX + columnWidths[index] / 2, startY - rowHeight / 2, 0);

                x += columnWidths[index];
                index++;
            }

            canvas.endText();
            canvas.restoreState();
        } catch (Exception e) {
            log.error("绘制表格行失败", e);
        }
    }
}
