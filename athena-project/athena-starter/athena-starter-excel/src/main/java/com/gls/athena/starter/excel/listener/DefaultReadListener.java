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
     * <p>
     * 该方法用于捕获并记录在Excel解析过程中发生的异常。当解析过程中抛出异常时，该方法会被调用，
     * 并记录异常信息以及当前处理的行号，以便于后续的调试和问题排查。
     *
     * @param exception 解析过程中抛出的异常对象，包含异常的具体信息
     * @param context   解析上下文对象，包含当前处理的行信息等上下文数据
     */
    @Override
    public void onException(Exception exception, AnalysisContext context) {
        // 记录异常信息，包括当前处理的行号和异常堆栈
        log.error("Excel解析发生异常，行号: {}", context.readRowHolder().getRowIndex(), exception);
    }

    /**
     * 处理Excel表头数据
     * <p>
     * 该方法用于将传入的Excel表头数据转换为字符串格式的Map，并存储到当前对象的headMap中。
     * 转换过程中会使用ConverterUtils工具类进行数据类型转换。
     * 最后，该方法会记录解析到的表头数据到日志中，便于调试和跟踪。
     *
     * @param headMap 原始表头数据Map，键为列索引，值为单元格数据对象
     * @param context 解析上下文，包含当前解析的状态和配置信息
     */
    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        // 将原始表头数据转换为字符串格式的Map，并存储到当前对象的headMap中
        this.headMap.putAll(ConverterUtils.convertToStringMap(headMap, context));

        // 记录解析到的表头数据到日志中，便于调试和跟踪
        log.debug("解析到表头数据: {}", this.headMap);
    }

    /**
     * 处理每行数据
     * <p>
     * 该方法用于处理解析后的每行数据，主要执行以下步骤：
     * 1. 处理对象字段（行号、多列数据）
     * 2. 执行数据校验
     * 3. 将处理后的数据添加到结果集中
     *
     * @param data    转换后的数据对象，包含解析后的数据内容
     * @param context 解析上下文，提供当前行的解析信息和上下文环境
     */
    @Override
    public void invoke(T data, AnalysisContext context) {
        // 获取当前行的行号和单元格数据
        int rowIndex = context.readRowHolder().getRowIndex();
        Map<Integer, Cell> cellMap = context.readRowHolder().getCellMap();

        // 记录当前行的解析日志
        log.debug("正在解析第{}行数据: {}", rowIndex, JSONUtil.toJsonStr(cellMap));

        // 处理数据对象的字段，并将数据添加到结果集中
        processFields(data, rowIndex, cellMap);
        validateData(data, rowIndex);
        list.add(data);
    }

    /**
     * 处理对象的所有字段
     * <p>
     * 该方法会遍历传入对象的所有字段，并根据字段上的注解进行相应的处理。
     * 具体处理包括：
     * - 对于带有 {@code ExcelLine} 注解的字段，调用 {@code processExcelLine} 方法进行处理。
     * - 对于带有 {@code ExcelMultiColumn} 注解的字段，调用 {@code processExcelMultiColumn} 方法进行处理。
     *
     * @param data     包含需要处理字段的对象
     * @param rowIndex 当前处理的行索引，用于 {@code ExcelLine} 注解的处理
     * @param cellMap  单元格映射，包含列索引与单元格的对应关系，用于 {@code ExcelMultiColumn} 注解的处理
     */
    private void processFields(T data, int rowIndex, Map<Integer, Cell> cellMap) {
        // 遍历对象的所有字段，并根据注解进行相应的处理
        Arrays.stream(data.getClass().getDeclaredFields())
                .forEach(field -> {
                    processExcelLine(data, field, rowIndex);
                    processExcelMultiColumn(data, field, cellMap);
                });
    }

    /**
     * 处理ExcelLine注解，设置行号
     * <p>
     * 该方法用于处理带有ExcelLine注解的字段，并将当前行号设置到该字段中。如果字段类型不是Integer或未标注ExcelLine注解，则直接返回。
     *
     * @param data     数据对象，包含需要处理的字段
     * @param field    待处理的字段，该字段可能标注了ExcelLine注解
     * @param rowIndex 当前行号，将被设置到标注了ExcelLine注解的字段中
     */
    private void processExcelLine(T data, Field field, int rowIndex) {
        // 检查字段是否标注了ExcelLine注解，并且字段类型是否为Integer
        if (!field.isAnnotationPresent(ExcelLine.class) || !field.getType().equals(Integer.class)) {
            return;
        }

        try {
            // 设置字段可访问，并将当前行号赋值给该字段
            field.setAccessible(true);
            field.set(data, rowIndex);
        } catch (IllegalAccessException e) {
            // 捕获并记录设置字段值时的异常
            log.error("设置Excel行号失败, field: {}", field.getName(), e);
        }
    }

    /**
     * 处理ExcelMultiColumn注解，设置多列数据到目标对象的指定字段中。
     * 该函数会检查字段是否带有ExcelMultiColumn注解，并且字段类型是否为Map。
     * 如果满足条件，则从单元格数据中提取多列数据，并将其设置到目标对象的字段中。
     *
     * @param data    目标数据对象，多列数据将被设置到该对象的字段中
     * @param field   待处理的字段，该字段应带有ExcelMultiColumn注解且类型为Map
     * @param cellMap 当前行的单元格数据，键为列索引，值为对应的单元格对象
     */
    private void processExcelMultiColumn(T data, Field field, Map<Integer, Cell> cellMap) {
        // 检查字段是否带有ExcelMultiColumn注解且类型为Map，如果不满足条件则直接返回
        if (!field.isAnnotationPresent(ExcelMultiColumn.class) || !field.getType().equals(Map.class)) {
            return;
        }

        try {
            // 设置字段可访问，以便后续操作
            field.setAccessible(true);
            // 获取字段上的ExcelMultiColumn注解
            ExcelMultiColumn annotation = field.getAnnotation(ExcelMultiColumn.class);
            // 从单元格数据中提取多列数据
            Map<String, Object> columnData = extractMultiColumnData(annotation, cellMap);
            // 将提取的多列数据设置到目标对象的字段中
            field.set(data, columnData);
        } catch (IllegalAccessException e) {
            // 捕获并记录设置多列数据时的异常
            log.error("设置Excel多列数据失败, field: {}", field.getName(), e);
        }
    }

    /**
     * 提取多列数据
     * <p>
     * 根据ExcelMultiColumn注解指定的起始和结束列索引，从单元格数据Map中提取多列数据，
     * 并将这些数据以表头名称为key，单元格值为value的形式存储在Map中返回。
     *
     * @param annotation ExcelMultiColumn注解信息，包含起始列索引和结束列索引
     * @param cellMap    单元格数据Map，key为列索引，value为对应的单元格对象
     * @return 多列数据Map，key为表头名称，value为单元格值
     */
    private Map<String, Object> extractMultiColumnData(ExcelMultiColumn annotation, Map<Integer, Cell> cellMap) {
        // 获取起始列索引和结束列索引，确保结束列索引不超过表头Map的大小
        int start = annotation.start();
        int end = Math.min(annotation.end(), headMap.size());

        // 初始化多列数据Map，容量为结束列索引与起始列索引的差值
        Map<String, Object> multiColumnMap = new HashMap<>(end - start);

        // 遍历指定范围内的列索引，提取表头名称和对应的单元格值
        for (int i = start; i < end; i++) {
            // 如果表头Map和单元格Map中都包含当前列索引，则提取数据
            if (headMap.containsKey(i) && cellMap.containsKey(i)) {
                multiColumnMap.put(headMap.get(i), extractCellValue(cellMap.get(i)));
            }
        }

        // 返回提取的多列数据Map
        return multiColumnMap;
    }

    /**
     * 提取单元格值
     * <p>
     * 该方法用于从给定的单元格对象中提取值，并根据单元格的数据类型返回相应的Java对象。
     * 支持的数据类型包括字符串、布尔值和数值。对于其他类型，返回单元格的原始数据。
     *
     * @param cell 单元格对象，需要从中提取值。如果单元格不是ReadCellData类型，则返回null。
     * @return 转换后的Java对象，具体类型取决于单元格的数据类型。如果单元格为null或不是ReadCellData类型，则返回null。
     */
    private Object extractCellValue(Cell cell) {
        // 检查单元格是否为ReadCellData类型，如果不是则返回null
        if (!(cell instanceof ReadCellData<?> readCellData)) {
            return null;
        }

        // 根据单元格的数据类型返回相应的值
        return switch (readCellData.getType()) {
            case STRING -> readCellData.getStringValue();
            case BOOLEAN -> readCellData.getBooleanValue();
            case NUMBER -> readCellData.getNumberValue();
            default -> readCellData.getData();
        };
    }

    /**
     * 执行数据校验并收集错误信息
     * <p>
     * 该函数接收一个数据对象和其所在的行号，使用ValidationUtil工具对数据进行校验。
     * 如果校验成功，则直接返回；如果校验失败，则将错误信息收集到errors集合中。
     * 每个错误信息包括行号、字段名、错误消息和错误值。
     *
     * @param data     待校验的数据对象，类型为泛型T
     * @param rowIndex 数据所在的行号，用于标识错误发生的位置
     */
    private void validateData(T data, int rowIndex) {
        // 使用ValidationUtil工具对数据进行校验，并获取校验结果
        BeanValidationResult result = ValidationUtil.warpValidate(data);

        // 如果校验成功，直接返回
        if (result.isSuccess()) {
            return;
        }

        // 遍历校验结果中的错误信息，并将每个错误信息添加到errors集合中
        result.getErrorMessages().forEach(error ->
                errors.add(new ExcelErrorMessage()
                        .setLine(rowIndex)
                        .setFieldName(error.getPropertyName())
                        .setErrorMessage(error.getMessage())
                        .setErrorValue(error.getValue()))
        );
    }

    /**
     * 处理Excel文件中的额外信息，如批注、合并单元格等。
     * 该方法是接口或父类方法的实现，用于在解析Excel文件时处理非单元格数据。
     *
     * @param extra   包含Excel文件中额外信息的对象，如批注、合并单元格等。
     * @param context 当前解析上下文对象，包含解析过程中的状态信息。
     */
    @Override
    public void extra(CellExtra extra, AnalysisContext context) {
        // 记录解析到的额外数据，用于调试或日志记录
        log.debug("解析到额外数据: {}", extra);
    }

    /**
     * Excel解析完成后的回调方法
     * 该方法在Excel文件解析完成后被调用，用于输出解析的统计信息。
     * 统计信息包括成功处理的数据条数和错误数据的条数。
     *
     * @param context 解析上下文对象，包含解析过程中的上下文信息
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 输出解析完成后的统计信息，包括成功处理的数据条数和错误数据的条数
        log.info("Excel解析完成, 共处理{}条数据, {}条错误数据", list.size(), errors.size());
    }

}
