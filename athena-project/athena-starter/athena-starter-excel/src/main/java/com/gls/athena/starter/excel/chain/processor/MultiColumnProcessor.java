package com.gls.athena.starter.excel.chain.processor;

import cn.idev.excel.metadata.data.ReadCellData;
import com.gls.athena.starter.excel.annotation.ExcelMultiColumn;
import com.gls.athena.starter.excel.chain.AbstractExcelProcessor;
import com.gls.athena.starter.excel.support.ExcelProcessContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 多列数据处理器
 * <p>
 * 负责处理带有@ExcelMultiColumn注解的字段，提取多列数据
 *
 * @author george
 */
@Slf4j
public class MultiColumnProcessor extends AbstractExcelProcessor {

    @Override
    protected boolean doProcess(ExcelProcessContext context) {
        Object data = context.getData();

        if (data == null) {
            return true;
        }

        Field[] fields = data.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(ExcelMultiColumn.class) && field.getType().equals(Map.class)) {
                processMultiColumnField(context, data, field);
            }
        }

        return true;
    }

    /**
     * 处理多列字段
     */
    private void processMultiColumnField(ExcelProcessContext context, Object data, Field field) {
        try {
            ExcelMultiColumn annotation = field.getAnnotation(ExcelMultiColumn.class);
            Map<String, Object> multiColumnData = extractMultiColumnData(context, annotation);

            field.setAccessible(true);
            field.set(data, multiColumnData);

            log.debug("设置多列数据: {} -> {}", field.getName(), multiColumnData);
        } catch (IllegalAccessException e) {
            log.warn("设置多列数据失败: {}", field.getName(), e);
            context.addError("设置多列数据失败: " + field.getName());
        }
    }

    /**
     * 提取多列数据
     */
    private Map<String, Object> extractMultiColumnData(ExcelProcessContext context, ExcelMultiColumn annotation) {
        int start = annotation.start();
        int end = Math.min(annotation.end(), context.getHeadMap().size());

        Map<String, Object> multiColumnMap = new HashMap<>(end - start);
        Map<Integer, String> headMap = context.getHeadMap();
        Map<Integer, Object> cellMap = context.getCellMap();

        for (int i = start; i < end; i++) {
            if (headMap.containsKey(i) && cellMap.containsKey(i)) {
                Object cellValue = extractCellValue(cellMap.get(i));
                multiColumnMap.put(headMap.get(i), cellValue);
            }
        }

        return multiColumnMap;
    }

    /**
     * 提取单元格值
     */
    private Object extractCellValue(Object cell) {
        if (!(cell instanceof ReadCellData<?> readCellData)) {
            return cell;
        }

        return switch (readCellData.getType()) {
            case STRING -> readCellData.getStringValue();
            case BOOLEAN -> readCellData.getBooleanValue();
            case NUMBER -> readCellData.getNumberValue();
            default -> readCellData.getData();
        };
    }

    @Override
    public String getProcessorName() {
        return "MultiColumnProcessor";
    }
}
