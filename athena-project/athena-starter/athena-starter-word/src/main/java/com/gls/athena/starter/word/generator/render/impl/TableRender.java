package com.gls.athena.starter.word.generator.render.impl;

import com.gls.athena.starter.word.generator.render.WordElementRender;
import com.gls.athena.starter.word.generator.render.WordRenderContext;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 表格渲染器
 *
 * @author athena
 */
@Component
public class TableRender implements WordElementRender {

    @Override
    public void render(Object data, WordRenderContext context) {
        if (!(data instanceof Collection)) {
            return;
        }

        Collection<?> collection = (Collection<?>) data;
        if (collection.isEmpty()) {
            return;
        }

        // 获取集合中的第一个元素
        Iterator<?> iterator = collection.iterator();
        Object firstItem = iterator.next();

        // 创建表格
        XWPFTable table = context.getDocument().createTable();
        table.setWidth("100%");

        // 表头样式
        XWPFTableRow headerRow = table.getRow(0);
        headerRow.setHeight(400);  // 设置行高

        // 根据不同类型设置表头和填充数据
        if (firstItem instanceof Map) {
            renderMapTable(table, headerRow, collection);
        } else {
            renderPojoTable(table, headerRow, collection);
        }
    }

    @Override
    public boolean supports(Object data, String path) {
        if (!(data instanceof Collection)) {
            return false;
        }

        Collection<?> collection = (Collection<?>) data;
        if (collection.isEmpty()) {
            return false;
        }

        // 获取第一个元素，判断是否为复杂类型
        Object firstItem = collection.iterator().next();

        // 表格数据通常是Map或POJO对象集合
        if (firstItem instanceof Map) {
            return true;
        }

        // 如果是基本类型或字符串，不适合生成表格
        if (firstItem == null ||
                firstItem instanceof String ||
                firstItem instanceof Number ||
                firstItem instanceof Boolean ||
                firstItem instanceof Character) {
            return false;
        }

        // 如果是自定义对象，检查是否有可访问的字段
        return firstItem.getClass().getDeclaredFields().length > 0;
    }

    @Override
    public int getOrder() {
        return 20; // 表格渲染优先级仅次于标题
    }

    /**
     * 渲染Map集合表格
     */
    @SuppressWarnings("unchecked")
    private void renderMapTable(XWPFTable table, XWPFTableRow headerRow, Collection<?> collection) {
        Object firstItem = collection.iterator().next();
        Map<String, Object> map = (Map<String, Object>) firstItem;
        List<String> headers = new ArrayList<>(map.keySet());

        // 创建表头
        for (int i = 0; i < headers.size(); i++) {
            XWPFTableCell cell = i == 0 ? headerRow.getCell(0) : headerRow.createCell();
            cell.setColor("EEEEEE");  // 设置背景色

            XWPFParagraph paragraph = cell.getParagraphs().get(0);
            paragraph.setAlignment(ParagraphAlignment.CENTER);

            XWPFRun run = paragraph.createRun();
            run.setText(headers.get(i));
            run.setBold(true);
        }

        // 添加数据行
        for (Object item : collection) {
            Map<String, Object> rowData = (Map<String, Object>) item;
            XWPFTableRow dataRow = table.createRow();

            for (int i = 0; i < headers.size(); i++) {
                String key = headers.get(i);
                Object value = rowData.get(key);

                XWPFTableCell cell = dataRow.getCell(i);
                XWPFParagraph paragraph = cell.getParagraphs().get(0);

                XWPFRun run = paragraph.createRun();
                run.setText(value != null ? value.toString() : "");
            }
        }
    }

    /**
     * 渲染POJO集合表格
     */
    private void renderPojoTable(XWPFTable table, XWPFTableRow headerRow, Collection<?> collection) {
        Object firstItem = collection.iterator().next();
        Field[] fields = firstItem.getClass().getDeclaredFields();
        List<String> headers = new ArrayList<>();

        for (Field field : fields) {
            field.setAccessible(true);
            headers.add(field.getName());
        }

        // 创建表头
        for (int i = 0; i < headers.size(); i++) {
            XWPFTableCell cell = i == 0 ? headerRow.getCell(0) : headerRow.createCell();
            cell.setColor("EEEEEE");  // 设置背景色

            XWPFParagraph paragraph = cell.getParagraphs().get(0);
            paragraph.setAlignment(ParagraphAlignment.CENTER);

            XWPFRun run = paragraph.createRun();
            run.setText(headers.get(i));
            run.setBold(true);
        }

        // 添加数据行
        for (Object item : collection) {
            XWPFTableRow dataRow = table.createRow();

            for (int i = 0; i < headers.size(); i++) {
                String fieldName = headers.get(i);
                Object value = null;

                try {
                    Field field = item.getClass().getDeclaredField(fieldName);
                    field.setAccessible(true);
                    value = field.get(item);
                } catch (Exception e) {
                    // 忽略异常
                }

                XWPFTableCell cell = dataRow.getCell(i);
                XWPFParagraph paragraph = cell.getParagraphs().get(0);

                XWPFRun run = paragraph.createRun();
                run.setText(value != null ? value.toString() : "");
            }
        }
    }
}
