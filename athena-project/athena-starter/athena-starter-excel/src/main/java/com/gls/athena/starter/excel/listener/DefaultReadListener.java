package com.gls.athena.starter.excel.listener;

import cn.hutool.extra.validation.BeanValidationResult;
import cn.hutool.extra.validation.ValidationUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.Cell;
import com.alibaba.excel.metadata.CellExtra;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.util.ConverterUtils;
import com.gls.athena.starter.excel.annotation.ExcelLine;
import com.gls.athena.starter.excel.annotation.ExcelMultiColumn;
import com.gls.athena.starter.excel.support.ExcelErrorMessage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Excel数据读取默认监听器
 * <p>
 * 主要功能：
 * 1. 读取Excel数据并转换为对象
 * 2. 支持数据校验
 * 3. 支持行号标记
 * 4. 支持多列数据读取
 * 5. 错误信息收集
 *
 * @param <T> 数据对象类型
 * @author george
 */
@Slf4j
@Getter
public class DefaultReadListener<T> implements IReadListener<T> {

    /**
     * 存储解析成功的数据对象集合
     */
    private final List<T> list = new ArrayList<>();

    /**
     * 存储数据校验错误信息集合
     */
    private final List<ExcelErrorMessage> errors = new ArrayList<>();

    /**
     * 存储Excel表头信息，Key为列索引，Value为表头名称
     */
    private final Map<Integer, String> headMap = new HashMap<>();

    /**
     * 处理Excel解析过程中的异常
     *
     * @param exception 解析过程中抛出的异常
     * @param context   解析上下文，包含当前处理的行信息等
     */
    @Override
    public void onException(Exception exception, AnalysisContext context) {
        log.error("Excel解析发生异常，行号: {}", context.readRowHolder().getRowIndex(), exception);
    }

    /**
     * 处理Excel表头数据
     *
     * @param headMap 原始表头数据Map
     * @param context 解析上下文
     */
    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        this.headMap.putAll(ConverterUtils.convertToStringMap(headMap, context));
        log.debug("解析到表头数据: {}", this.headMap);
    }

    /**
     * 处理每行数据
     * <p>
     * 执行步骤：
     * 1. 处理对象字段（行号、多列数据）
     * 2. 执行数据校验
     * 3. 添加到结果集
     *
     * @param data    转换后的数据对象
     * @param context 解析上下文
     */
    @Override
    public void invoke(T data, AnalysisContext context) {
        int rowIndex = context.readRowHolder().getRowIndex();
        Map<Integer, Cell> cellMap = context.readRowHolder().getCellMap();

        log.debug("正在解析第{}行数据: {}", rowIndex, JSONUtil.toJsonStr(cellMap));

        processFields(data, rowIndex, cellMap);
        validateData(data, rowIndex);
        list.add(data);
    }

    /**
     * 处理对象的所有字段
     * <p>
     * 包括：
     * - ExcelLine注解标记的行号字段
     * - ExcelMultiColumn注解标记的多列字段
     */
    private void processFields(T data, int rowIndex, Map<Integer, Cell> cellMap) {
        Arrays.stream(data.getClass().getDeclaredFields())
                .forEach(field -> {
                    processExcelLine(data, field, rowIndex);
                    processExcelMultiColumn(data, field, cellMap);
                });
    }

    /**
     * 处理ExcelLine注解，设置行号
     *
     * @param data     数据对象
     * @param field    待处理字段
     * @param rowIndex 当前行号
     */
    private void processExcelLine(T data, Field field, int rowIndex) {
        if (!field.isAnnotationPresent(ExcelLine.class) || !field.getType().equals(Integer.class)) {
            return;
        }

        try {
            field.setAccessible(true);
            field.set(data, rowIndex);
        } catch (IllegalAccessException e) {
            log.error("设置Excel行号失败, field: {}", field.getName(), e);
        }
    }

    /**
     * 处理ExcelMultiColumn注解，设置多列数据
     *
     * @param data    数据对象
     * @param field   待处理字段
     * @param cellMap 当前行的单元格数据
     */
    private void processExcelMultiColumn(T data, Field field, Map<Integer, Cell> cellMap) {
        if (!field.isAnnotationPresent(ExcelMultiColumn.class) || !field.getType().equals(Map.class)) {
            return;
        }

        try {
            field.setAccessible(true);
            ExcelMultiColumn annotation = field.getAnnotation(ExcelMultiColumn.class);
            Map<String, Object> columnData = extractMultiColumnData(annotation, cellMap);
            field.set(data, columnData);
        } catch (IllegalAccessException e) {
            log.error("设置Excel多列数据失败, field: {}", field.getName(), e);
        }
    }

    /**
     * 提取多列数据
     *
     * @param annotation ExcelMultiColumn注解信息
     * @param cellMap    单元格数据Map
     * @return 多列数据Map，key为表头名称，value为单元格值
     */
    private Map<String, Object> extractMultiColumnData(ExcelMultiColumn annotation, Map<Integer, Cell> cellMap) {
        int start = annotation.start();
        int end = Math.min(annotation.end(), headMap.size());
        Map<String, Object> multiColumnMap = new HashMap<>(end - start);

        for (int i = start; i < end; i++) {
            if (headMap.containsKey(i) && cellMap.containsKey(i)) {
                multiColumnMap.put(headMap.get(i), extractCellValue(cellMap.get(i)));
            }
        }
        return multiColumnMap;
    }

    /**
     * 提取单元格值
     * <p>
     * 支持的数据类型：
     * - 字符串
     * - 布尔值
     * - 数值
     * 其他类型返回原始数据
     *
     * @param cell 单元格对象
     * @return 转换后的Java对象
     */
    private Object extractCellValue(Cell cell) {
        if (!(cell instanceof ReadCellData<?> readCellData)) {
            return null;
        }

        return switch (readCellData.getType()) {
            case STRING -> readCellData.getStringValue();
            case BOOLEAN -> readCellData.getBooleanValue();
            case NUMBER -> readCellData.getNumberValue();
            default -> readCellData.getData();
        };
    }

    /**
     * 执行数据校验并收集错误信息
     *
     * @param data     待校验的数据对象
     * @param rowIndex 数据所在行号
     */
    private void validateData(T data, int rowIndex) {
        BeanValidationResult result = ValidationUtil.warpValidate(data);
        if (result.isSuccess()) {
            return;
        }

        result.getErrorMessages().forEach(error ->
                errors.add(new ExcelErrorMessage()
                        .setLine(rowIndex)
                        .setFieldName(error.getPropertyName())
                        .setErrorMessage(error.getMessage())
                        .setErrorValue(error.getValue()))
        );
    }

    /**
     * 处理Excel额外信息（如批注、合并单元格等）
     */
    @Override
    public void extra(CellExtra extra, AnalysisContext context) {
        log.debug("解析到额外数据: {}", extra);
    }

    /**
     * Excel解析完成后的回调方法
     * 输出解析统计信息
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("Excel解析完成, 共处理{}条数据, {}条错误数据", list.size(), errors.size());
    }
}
