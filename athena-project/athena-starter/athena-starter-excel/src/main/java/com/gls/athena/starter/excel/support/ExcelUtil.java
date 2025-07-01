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
import org.springframework.lang.NonNull;

import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel工具类
 *
 * @author lizy19
 */
@Slf4j
@UtilityClass
public class ExcelUtil {

    /**
     * 将数据导出到Excel文件
     *
     * @param data          要导出的数据对象，非空
     * @param outputStream  输出流，用于写入Excel文件，非空
     * @param excelResponse Excel导出配置对象，包含导出模板、样式等信息，非空
     * @throws RuntimeException 当导出过程中发生异常时抛出
     */
    public void exportToExcel(@NonNull Object data, @NonNull OutputStream outputStream, @NonNull ExcelResponse excelResponse) {
        // 使用try-with-resources确保ExcelWriter正确关闭
        try (ExcelWriter excelWriter = ExcelWriterCustomizer.build(outputStream, excelResponse)) {
            // 根据是否使用模板选择不同的导出方式
            if (StrUtil.isEmpty(excelResponse.template())) {
                // 无模板情况下的导出逻辑
                writeToExcel(Convert.toList(data), excelWriter, excelResponse);
            } else {
                // 使用模板填充的导出逻辑
                fillToExcel(data, excelWriter, excelResponse);
            }
        } catch (Exception e) {
            log.error("Excel导出失败: {}", e.getMessage(), e);
            throw new RuntimeException("Excel导出失败", e);
        }
    }

    /**
     * 将数据填充到Excel文件中
     *
     * @param data          要填充的数据，可以是单个对象或对象列表
     * @param excelWriter   Excel写入工具，用于实际写入Excel文件
     * @param excelResponse Excel响应对象，包含Excel的配置信息（如sheet配置）
     */
    private void fillToExcel(@NonNull Object data, @NonNull ExcelWriter excelWriter, @NonNull ExcelResponse excelResponse) {
        ExcelSheet[] excelSheets = excelResponse.sheets();

        // 单sheet情况直接处理
        if (excelSheets.length == 1) {
            fillToSheet(data, excelWriter, excelSheets[0]);
            return;
        }

        // 多sheet处理：将数据按sheet顺序分配
        List<?> dataList = Convert.toList(data);
        for (ExcelSheet excelSheet : excelSheets) {
            int sheetNo = excelSheet.sheetNo();
            Object sheetData = dataList.get(sheetNo);
            fillToSheet(sheetData, excelWriter, excelSheet);
        }
    }

    /**
     * 将数据填充到Excel工作表中
     *
     * @param data        要填充的数据，支持集合类型或普通Java对象
     * @param excelWriter Excel写入工具实例，用于执行填充操作
     * @param excelSheet  Excel工作表配置信息
     */
    private void fillToSheet(@NonNull Object data, @NonNull ExcelWriter excelWriter, @NonNull ExcelSheet excelSheet) {
        // 构建可写入的工作表对象
        WriteSheet writeSheet = WriteSheetCustomizer.build(excelSheet);

        // 处理集合类型数据
        if (data instanceof Collection) {
            excelWriter.fill(data, writeSheet);
            return;
        }

        // 将Bean对象转换为Map结构
        Map<String, Object> dataMap = BeanUtil.beanToMap(data);
        Map<String, Object> fillMap = new HashMap<>();
        FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();

        // 遍历Map处理每个字段
        for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // 集合类型字段单独填充（如多行数据）
            if (value instanceof Collection<?> column) {
                excelWriter.fill(new FillWrapper(key, column), fillConfig, writeSheet);
            } else {
                // 非集合类型字段暂存
                fillMap.put(key, value);
            }
        }

        // 填充剩余的非集合类型字段
        if (!fillMap.isEmpty()) {
            excelWriter.fill(fillMap, fillConfig, writeSheet);
        }
    }

    /**
     * 将数据写入Excel文件
     *
     * @param data          要写入的数据列表，可以是任意类型的对象列表
     * @param excelWriter   Excel写入工具，用于实际执行Excel写入操作
     * @param excelResponse Excel响应对象，包含Excel工作表配置信息
     */
    private void writeToExcel(@NonNull List<?> data, @NonNull ExcelWriter excelWriter, @NonNull ExcelResponse excelResponse) {
        ExcelSheet[] excelSheets = excelResponse.sheets();

        // 如果只有一个工作表，直接将所有数据写入该工作表
        if (excelSheets.length == 1) {
            writeToSheet(data, excelWriter, excelSheets[0]);
            return;
        }

        // 多个工作表时，根据sheetNo从数据中获取对应分页数据并写入
        for (ExcelSheet excelSheet : excelSheets) {
            int sheetNo = excelSheet.sheetNo();
            List<?> sheetData = Convert.toList(data.get(sheetNo));
            writeToSheet(sheetData, excelWriter, excelSheet);
        }
    }

    /**
     * 将数据写入Excel工作表
     *
     * @param data        待写入的数据列表，支持泛型
     * @param excelWriter Excel写入工具实例
     * @param excelSheet  Excel工作表配置信息
     */
    private void writeToSheet(@NonNull List<?> data, @NonNull ExcelWriter excelWriter, @NonNull ExcelSheet excelSheet) {
        // 构建基础工作表配置
        WriteSheet writeSheet = WriteSheetCustomizer.build(excelSheet);

        // 获取工作表内所有表格配置
        List<WriteTable> writeTables = WriteTableCustomizer.build(CollUtil.toList(excelSheet.tables()));

        // 处理无表格或单表格的特殊情况
        if (writeTables.isEmpty()) {
            writeToTable(data, excelWriter, writeSheet, null);
            return;
        }
        if (writeTables.size() == 1) {
            writeToTable(data, excelWriter, writeSheet, writeTables.getFirst());
            return;
        }

        // 多表格情况：按表格编号匹配数据分区写入
        for (WriteTable writeTable : writeTables) {
            int tableNo = writeTable.getTableNo();
            List<?> tableData = Convert.toList(data.get(tableNo));
            writeToTable(tableData, excelWriter, writeSheet, writeTable);
        }
    }

    /**
     * 将数据列表写入Excel表格
     *
     * @param data        要写入的数据列表，列表元素类型必须一致且非空
     * @param excelWriter Excel写入工具实例，用于执行实际写入操作
     * @param writeSheet  工作表配置对象，定义写入的目标工作表
     * @param writeTable  表格配置对象（可选），若存在则用于定义表格样式和结构；若为null则直接使用工作表配置
     */
    private void writeToTable(@NonNull List<?> data, @NonNull ExcelWriter excelWriter, @NonNull WriteSheet writeSheet, WriteTable writeTable) {
        // 获取列表第一个元素的Class类型作为写入模板
        Class<?> clazz = data.getFirst().getClass();

        // 根据writeTable是否存在决定不同的写入策略
        if (writeTable != null) {
            writeTable.setClazz(clazz);
            excelWriter.write(data, writeSheet, writeTable);
        } else {
            writeSheet.setClazz(clazz);
            excelWriter.write(data, writeSheet);
        }
    }
}
