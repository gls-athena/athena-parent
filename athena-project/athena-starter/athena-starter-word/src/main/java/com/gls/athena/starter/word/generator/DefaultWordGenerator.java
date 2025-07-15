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
 * 默认Word文档生成器（无模板）
 *
 * @author athena
 */
@Slf4j
@Component
public class DefaultWordGenerator implements WordGenerator {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 生成文档内容
     */
    private void generateContent(XWPFDocument document, Map<String, Object> dataMap) {
        if (dataMap.isEmpty()) {
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText("暂无数据");
            return;
        }

        // 创建数据表格
        XWPFTable table = document.createTable();

        // 设置表头
        XWPFTableRow headerRow = table.getRow(0);
        headerRow.getCell(0).setText("字段名");
        headerRow.addNewTableCell().setText("值");

        // 添加数据行
        for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
            XWPFTableRow dataRow = table.createRow();
            dataRow.getCell(0).setText(entry.getKey());
            dataRow.getCell(1).setText(formatValue(entry.getValue()));
        }
    }

    /**
     * 格式化值
     */
    private String formatValue(Object value) {
        if (value == null) {
            return "null";
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
