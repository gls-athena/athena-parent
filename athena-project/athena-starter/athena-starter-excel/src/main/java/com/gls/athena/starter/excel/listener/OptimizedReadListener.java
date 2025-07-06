package com.gls.athena.starter.excel.listener;

import cn.hutool.core.util.StrUtil;
import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.metadata.Cell;
import cn.idev.excel.metadata.data.ReadCellData;
import cn.idev.excel.util.ConverterUtils;
import com.gls.athena.starter.excel.chain.ExcelProcessorChain;
import com.gls.athena.starter.excel.support.ExcelErrorMessage;
import com.gls.athena.starter.excel.support.ExcelProcessContext;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 优化后的Excel读取监听器
 * <p>
 * 使用责任链模式重构数据处理逻辑
 * 实现了模板方法模式，提供清晰的处理流程
 *
 * @param <T> 数据对象类型
 * @author george
 */
@Slf4j
@Getter
public class OptimizedReadListener<T> implements IReadListener<T> {

    /**
     * 存储解析成功的数据对象集合
     */
    private final List<T> list = new ArrayList<>();

    /**
     * 存储数据校验错误信息集合
     */
    private final List<ExcelErrorMessage> errors = new ArrayList<>();

    /**
     * 存储Excel表头信息
     */
    private final Map<Integer, String> headMap = new HashMap<>();

    /**
     * Excel处理器链
     */
    private final ExcelProcessorChain processorChain;

    /**
     * 构造函数
     */
    public OptimizedReadListener() {
        this(ExcelProcessorChain.buildDefaultChain());
    }

    /**
     * 构造函数 - 支持自定义处理链
     */
    public OptimizedReadListener(ExcelProcessorChain processorChain) {
        this.processorChain = processorChain;
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) {
        int rowIndex = context.readRowHolder().getRowIndex();
        log.error("Excel解析发生异常，行号: {}", rowIndex, exception);

        // 创建错误信息
        ExcelErrorMessage error = new ExcelErrorMessage(
                rowIndex,
                "",
                "解析异常: " + exception.getMessage(),
                ""
        );
        errors.add(error);
    }

    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        // 模板方法：处理表头
        processHeader(headMap, context);
    }

    @Override
    public void invoke(T data, AnalysisContext context) {
        // 模板方法：处理数据行
        processDataRow(data, context);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 所有数据解析完成后的回调
        log.info("Excel数据解析完成，共解析 {} 行数据，发现 {} 个错误", list.size(), errors.size());

        // 可以在这里进行一些后处理工作
        postProcessData();
    }

    /**
     * 模板方法：处理表头数据
     */
    protected void processHeader(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        this.headMap.putAll(ConverterUtils.convertToStringMap(headMap, context));
        log.debug("解析到表头数据: {}", this.headMap);
    }

    /**
     * 模板方法：处理数据行
     */
    protected void processDataRow(T data, AnalysisContext context) {
        int rowIndex = context.readRowHolder().getRowIndex();
        Map<Integer, Cell> cellMap = context.readRowHolder().getCellMap();

        // 构建处理上下文
        ExcelProcessContext processContext = buildProcessContext(data, rowIndex, cellMap);

        // 执行责任链处理
        boolean success = processorChain.process(processContext);

        // 收集错误信息
        if (processContext.hasErrors()) {
            errors.addAll(processContext.getErrors());
        }

        // 添加到结果列表（即使有错误也添加，让业务层决定如何处理）
        if (success || !hasBlockingErrors(processContext)) {
            list.add(data);
        }

        log.debug("处理第{}行数据完成，是否成功: {}, 错误数: {}",
                rowIndex, success, processContext.getErrors().size());
    }

    /**
     * 构建处理上下文
     */
    protected ExcelProcessContext buildProcessContext(T data, int rowIndex, Map<Integer, Cell> cellMap) {
        ExcelProcessContext context = new ExcelProcessContext()
                .setData(data)
                .setRowIndex(rowIndex)
                .setHeadMap(this.headMap);

        // 转换单元格数据
        Map<Integer, Object> convertedCellMap = new HashMap<>();
        cellMap.forEach((index, cell) -> {
            if (cell instanceof ReadCellData<?> readCellData) {
                convertedCellMap.put(index, extractCellValue(readCellData));
            } else {
                convertedCellMap.put(index, cell);
            }
        });
        context.setCellMap(convertedCellMap);

        return context;
    }

    /**
     * 提取单元格值
     */
    private Object extractCellValue(ReadCellData<?> readCellData) {
        return switch (readCellData.getType()) {
            case STRING -> readCellData.getStringValue();
            case BOOLEAN -> readCellData.getBooleanValue();
            case NUMBER -> readCellData.getNumberValue();
            default -> readCellData.getData();
        };
    }

    /**
     * 判断是否有阻塞性错误
     */
    protected boolean hasBlockingErrors(ExcelProcessContext context) {
        return context.getErrors().stream()
                .anyMatch(error -> StrUtil.contains(error.getMessage(), "阻塞") ||
                        StrUtil.contains(error.getMessage(), "严重"));
    }

    /**
     * 后处理数据方法，子类可重写
     */
    protected void postProcessData() {
        // 默认实现：可以进行数据去重、排序等后处理工作
        log.debug("执行数据后处理，当前数据量: {}", list.size());

        // 可以在这里添加一些通用的后处理逻辑
        // 例如：数据去重、数据排序、统计信息等
    }
}
