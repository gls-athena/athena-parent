package com.gls.athena.starter.excel.support;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.idev.excel.ExcelWriter;
import cn.idev.excel.write.metadata.WriteSheet;
import cn.idev.excel.write.metadata.WriteTable;
import cn.idev.excel.write.metadata.fill.FillConfig;
import cn.idev.excel.write.metadata.fill.FillWrapper;
import com.gls.athena.starter.excel.annotation.ExcelResponse;
import com.gls.athena.starter.excel.annotation.ExcelSheet;
import com.gls.athena.starter.excel.customizer.ExcelWriterCustomizer;
import com.gls.athena.starter.excel.customizer.WriteSheetCustomizer;
import com.gls.athena.starter.excel.customizer.WriteTableCustomizer;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author george
 */
@Slf4j
@UtilityClass
public class ExcelUtil {

    /**
     * 根据传入的数据和响应对象，将数据导出到Excel
     *
     * @param data          要导出到Excel的数据
     * @param outputStream  输出流，用于写入Excel数据
     * @param excelResponse 包含Excel导出配置的响应对象
     */
    public void exportToExcel(Object data, OutputStream outputStream, ExcelResponse excelResponse) {
        try (ExcelWriter excelWriter = ExcelWriterCustomizer.build(outputStream, excelResponse)) {
            // 根据是否使用模板选择不同的Excel写入方式
            if (StrUtil.isEmpty(excelResponse.template())) {
                // 如果没有模板，则直接写入数据
                writeDataToExcel(data, excelWriter, excelResponse);
            } else {
                // 如果使用了模板，则填充数据
                fillDataToExcel(data, excelWriter, excelResponse);
            }
        } catch (Exception e) {
            log.error("ExcelResponseHandler: {}", e.getMessage(), e);
        }
    }

    /**
     * 使用模板将数据填充到Excel中
     *
     * @param data          要填充到Excel的数据
     * @param excelWriter   用于写入Excel数据的写入器
     * @param excelResponse 包含Excel导出配置的响应对象
     */
    private void fillDataToExcel(Object data, ExcelWriter excelWriter, ExcelResponse excelResponse) {
        ExcelSheet[] excelSheets = excelResponse.sheets();

        // 单工作表处理：直接填充整个数据对象到唯一工作表
        if (excelSheets.length == 1) {
            ExcelSheet excelSheet = excelSheets[0];
            fillSingleSheet(data, excelWriter, excelSheet);
            return;
        }

        // 多工作表处理：将数据转换为列表后，按工作表编号分配对应数据
        List<?> dataList = Convert.toList(data);
        for (ExcelSheet excelSheet : excelSheets) {
            Object sheetData = dataList.get(excelSheet.sheetNo());
            fillSingleSheet(sheetData, excelWriter, excelSheet);
        }
    }

    /**
     * 填充单个工作表的数据
     *
     * @param sheetData   要填充到工作表的数据
     * @param excelWriter 用于写入Excel数据的写入器
     * @param excelSheet  包含工作表配置的注解对象
     */
    private void fillSingleSheet(Object sheetData, ExcelWriter excelWriter, ExcelSheet excelSheet) {
        // 根据配置创建Excel所需的WriteSheet对象
        WriteSheet writeSheet = WriteSheetCustomizer.build(excelSheet);

        // 使用ExcelWriter将数据填充到指定工作表
        fillSheetData(excelWriter, writeSheet, sheetData);
    }

    /**
     * 将数据写入Excel中
     *
     * @param data          要写入Excel的数据
     * @param excelWriter   用于写入Excel数据的写入器
     * @param excelResponse 包含Excel导出配置的响应对象
     */
    private void writeDataToExcel(Object data, ExcelWriter excelWriter, ExcelResponse excelResponse) {
        ExcelSheet[] excelSheets = excelResponse.sheets();

        // 处理单工作表情况
        if (excelSheets.length == 1) {
            ExcelSheet excelSheet = excelSheets[0];
            writeSingleSheet(data, excelWriter, excelSheet);
            return;
        }

        // 处理多工作表情况：将数据转换为列表并按工作表编号分配数据
        List<?> dataList = Convert.toList(data);
        for (ExcelSheet excelSheet : excelSheets) {
            Object sheetData = dataList.get(excelSheet.sheetNo());
            writeSingleSheet(sheetData, excelWriter, excelSheet);
        }
    }

    /**
     * 写入单个工作表的数据
     *
     * @param data        要写入工作表的数据
     * @param excelWriter 用于写入Excel数据的写入器
     * @param excelSheet  包含工作表配置的注解对象
     */
    private void writeSingleSheet(Object data, ExcelWriter excelWriter, ExcelSheet excelSheet) {
        // 将输入数据统一转换为List格式
        List<?> dataList = Convert.toList(data);

        // 获取工作表配置并生成写入对象
        WriteSheet writeSheet = WriteSheetCustomizer.build(excelSheet);

        // 获取表格配置并生成多个写入表格对象
        List<WriteTable> writeTables = WriteTableCustomizer.build(CollUtil.toList(excelSheet.tables()));

        // 执行实际的数据写入操作
        writeSheetData(excelWriter, writeSheet, writeTables, dataList);
    }

    /**
     * 将数据写入到工作表中
     *
     * @param excelWriter    用于写入Excel数据的写入器
     * @param writeSheet     代表工作表的写入对象
     * @param writeTableList 代表多个表格的写入对象列表
     * @param sheetData      要写入工作表的数据
     */
    private void writeSheetData(ExcelWriter excelWriter, WriteSheet writeSheet, List<WriteTable> writeTableList, List<?> sheetData) {
        // 检查数据列表是否为空
        if (CollUtil.isEmpty(sheetData)) {
            log.warn("写入Excel数据时，数据列表为空，跳过写入操作");
            return;
        }

        // 处理无表格信息或单个表格信息的情况
        if (CollUtil.isEmpty(writeTableList)) {
            writeTableData(excelWriter, writeSheet, null, sheetData);
            return;
        }
        if (writeTableList.size() == 1) {
            writeTableData(excelWriter, writeSheet, writeTableList.getFirst(), sheetData);
            return;
        }

        // 处理多个表格信息的情况：按表格编号分别写入对应数据
        for (WriteTable writeTable : writeTableList) {
            List<?> tableData = Convert.toList(sheetData.get(writeTable.getTableNo()));
            writeTableData(excelWriter, writeSheet, writeTable, tableData);
        }
    }

    /**
     * 将数据写入到表格中
     *
     * @param excelWriter 用于写入Excel数据的写入器
     * @param writeSheet  代表工作表的写入对象
     * @param writeTable  代表表格的写入对象
     * @param data        要写入表格的数据
     */
    private void writeTableData(ExcelWriter excelWriter, WriteSheet writeSheet, WriteTable writeTable, List<?> data) {
        // 空数据检查：跳过空列表或首元素为null的情况
        if (CollUtil.isEmpty(data)) {
            log.warn("写入Excel数据时，数据列表为空，跳过写入操作");
            return;
        }
        if (data.getFirst() == null) {
            log.warn("写入Excel数据时，数据列表的第一个元素为null，跳过写入操作");
            return;
        }

        // 根据数据对象的类型动态设置写入配置
        Class<?> clazz = data.getFirst().getClass();
        if (writeTable != null) {
            // 优先使用表格级配置写入
            writeTable.setClazz(clazz);
            excelWriter.write(data, writeSheet, writeTable);
        } else {
            // 无表格配置时使用工作表级配置写入
            writeSheet.setClazz(clazz);
            excelWriter.write(data, writeSheet);
        }
    }

    /**
     * 将数据填充到工作表中
     *
     * @param excelWriter 用于写入Excel数据的写入器
     * @param writeSheet  代表工作表的写入对象
     * @param sheetData   要填充到工作表的数据
     */
    private void fillSheetData(ExcelWriter excelWriter, WriteSheet writeSheet, Object sheetData) {
        // 空数据检查
        if (sheetData == null) {
            log.warn("填充Excel数据时，数据为空，跳过填充操作");
            return;
        }

        // 处理集合类型数据
        if (sheetData instanceof Collection) {
            excelWriter.fill(sheetData, writeSheet);
            return;
        }

        // 处理JavaBean对象
        Map<String, Object> dataMap = BeanUtil.beanToMap(sheetData);
        // 创建填充数据Map
        Map<String, Object> fillMap = new HashMap<>();
        // 创建填充配置
        FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
        // 遍历Bean属性，区分集合类型和普通属性
        dataMap.forEach((key, value) -> {
            if (value instanceof Collection<?> collection) {
                // 集合类型属性单独填充
                FillWrapper fillWrapper = new FillWrapper(key, collection);
                excelWriter.fill(fillWrapper, fillConfig, writeSheet);
            } else {
                // 普通属性暂存到fillMap
                fillMap.put(key, value);
            }
        });

        // 填充剩余的普通属性
        if (CollUtil.isNotEmpty(fillMap)) {
            excelWriter.fill(fillMap, fillConfig, writeSheet);
        }
    }
}
