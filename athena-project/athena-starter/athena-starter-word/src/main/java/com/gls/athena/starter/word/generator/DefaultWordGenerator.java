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
     * 通用配置key（可通过数据Map传递）
     */
    private static final String KEY_TITLE = "_title";
    private static final String KEY_HEADERS = "_headers";
    private static final String KEY_FIELDS = "_fields";
    private static final String KEY_EMPTY_MSG = "_emptyMsg";
    private static final String DEFAULT_TITLE = "数据报告";
    private static final String DEFAULT_EMPTY_MSG = "无数据";
    private static final String[] DEFAULT_HEADERS = {"字段名", "值"};
    /**
     * Jackson对象映射器，用于对象与Map的转换。
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Collection<String> safeGetFields(Object fieldsObj) {
        if (fieldsObj instanceof Collection<?> raw) {
            boolean allString = raw.stream().allMatch(o -> o instanceof String);
            if (allString) {
                @SuppressWarnings("unchecked")
                Collection<String> casted = (Collection<String>) raw;
                return casted;
            }
        }
        return null;
    }

    /**
     * 生成Word文档内容，支持自定义标题、表头、字段顺序、空数据提示。
     */
    private void generateContent(XWPFDocument document, Map<String, Object> dataMap) {
        // 处理自定义标题
        String title = dataMap.containsKey(KEY_TITLE) ? String.valueOf(dataMap.get(KEY_TITLE)) : DEFAULT_TITLE;
        // 添加标题
        XWPFParagraph titleParagraph = document.createParagraph();
        XWPFRun titleRun = titleParagraph.createRun();
        titleRun.setText(title);
        titleRun.setBold(true);
        titleRun.setFontSize(16);
        document.createParagraph(); // 空行

        // 处理自定义表头
        String[] headers = dataMap.containsKey(KEY_HEADERS) ?
                ((Collection<?>) dataMap.get(KEY_HEADERS)).stream().map(String::valueOf).toArray(String[]::new)
                : DEFAULT_HEADERS;

        // 处理字段顺序（类型安全）
        Collection<String> fields = dataMap.containsKey(KEY_FIELDS) ?
                safeGetFields(dataMap.get(KEY_FIELDS)) : null;

        // 过滤掉配置key
        Map<String, Object> realData = dataMap.entrySet().stream()
                .filter(e -> !e.getKey().startsWith("_"))
                .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        XWPFTable table = document.createTable();
        // 设置表头
        XWPFTableRow headerRow = table.getRow(0);
        for (int i = 0; i < headers.length; i++) {
            if (i == 0) {
                headerRow.getCell(0).setText(headers[0]);
            } else {
                headerRow.addNewTableCell().setText(headers[i]);
            }
        }

        if (realData.isEmpty()) {
            // 空数据友好提示
            XWPFTableRow row = table.createRow();
            row.getCell(0).setText(dataMap.containsKey(KEY_EMPTY_MSG) ? String.valueOf(dataMap.get(KEY_EMPTY_MSG)) : DEFAULT_EMPTY_MSG);
            for (int i = 1; i < headers.length; i++) {
                row.addNewTableCell().setText("");
            }
        } else {
            // 按字段顺序输出
            if (fields != null && !fields.isEmpty()) {
                for (String key : fields) {
                    if (realData.containsKey(key)) {
                        addRow(table, key, realData.get(key), 0);
                    }
                }
            } else {
                for (Map.Entry<String, Object> entry : realData.entrySet()) {
                    addRow(table, entry.getKey(), entry.getValue(), 0);
                }
            }
        }
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
    public void generate(Object data, WordResponse template, OutputStream outputStream) {
        try (XWPFDocument document = new XWPFDocument()) {
            // 转换数据并生成内容
            Map<String, Object> dataMap = convertToMap(data);
            generateContent(document, dataMap);
            document.write(outputStream);
        } catch (Exception e) {
            log.error("生成默认Word文档失败", e);
            throw new RuntimeException("生成Word文档失败", e);
        }
    }

    @Override
    public boolean supports(WordResponse wordResponse) {
        return StrUtil.isBlank(wordResponse.template())
                && wordResponse.generator() == WordGenerator.class;
    }
}
