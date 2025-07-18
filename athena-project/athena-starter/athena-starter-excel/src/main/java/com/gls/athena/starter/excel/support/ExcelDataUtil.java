package com.gls.athena.starter.excel.support;

import com.gls.athena.starter.excel.annotation.ExcelResponse;
import com.gls.athena.starter.excel.annotation.ExcelSheet;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Excel数据处理工具类
 */
@UtilityClass
public class ExcelDataUtil {

    /**
     * 根据索引获取数据
     *
     * @param data       原始数据
     * @param containers 数据容器列表
     * @param index      目标数据索引
     * @return 索引对应的数据
     * @throws IllegalArgumentException 如果索引超出数据范围则抛出异常
     */
    public Object getDataAtIndex(Object data, List<?> containers, int index) {
        // 如果只有一个容器，直接返回原数据
        if (containers.size() == 1) {
            return data;
        }

        // 将数据规范化为List，以便统一处理
        List<?> dataList = normalizeToList(data);
        // 确保索引在有效范围内
        if (index < 0 || index >= dataList.size()) {
            throw new IllegalArgumentException("索引 " + index + " 超出数据范围(0-" + (dataList.size() - 1) + ")");
        }
        // 返回索引对应的数据
        return dataList.get(index);
    }

    /**
     * 将数据规范化为List
     *
     * @param data 原始数据
     * @return 规范化后的List
     */
    public List<?> normalizeToList(Object data) {
        // 根据数据类型返回相应的List实现
        return switch (data) {
            case null -> Collections.emptyList();
            case List<?> list -> list;
            case Collection<?> collection -> new ArrayList<>(collection);
            default -> List.of(data);
        };
    }

    /**
     * 获取并验证Excel工作表配置
     *
     * @param excelResponse Excel响应对象，包含工作表配置
     * @return 有效的工作表配置列表
     * @throws IllegalArgumentException 如果工作表配置为空则抛出异常
     */
    public List<ExcelSheet> getValidatedSheets(ExcelResponse excelResponse) {
        // 获取工作表配置数组
        ExcelSheet[] sheets = excelResponse.sheets();
        // 验证工作表配置是否为空
        if (sheets == null || sheets.length == 0) {
            throw new IllegalArgumentException("Excel工作表配置不能为空");
        }
        // 返回工作表配置列表
        return List.of(sheets);
    }
}
