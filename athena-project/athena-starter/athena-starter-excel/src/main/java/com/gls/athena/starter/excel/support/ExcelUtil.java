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
 * @author george
 */
@Slf4j
@UtilityClass
public class ExcelUtil {

    public void exportToExcel(@NonNull Object data, @NonNull OutputStream outputStream, @NonNull ExcelResponse excelResponse) {
        try (ExcelWriter excelWriter = ExcelWriterCustomizer.build(outputStream, excelResponse)) {
            if (StrUtil.isEmpty(excelResponse.template())) {
                writeToExcel(Convert.toList(data), excelWriter, excelResponse);
            } else {
                fillToExcel(data, excelWriter, excelResponse);
            }
        } catch (Exception e) {
            log.error("Excel导出失败: {}", e.getMessage(), e);
            throw new RuntimeException("Excel导出失败", e);
        }
    }

    private void fillToExcel(@NonNull Object data, @NonNull ExcelWriter excelWriter, @NonNull ExcelResponse excelResponse) {
        ExcelSheet[] excelSheets = excelResponse.sheets();
        if (excelSheets.length == 1) {
            fillToSheet(data, excelWriter, excelSheets[0]);
            return;
        }
        List<?> dataList = Convert.toList(data);
        for (ExcelSheet excelSheet : excelSheets) {
            int sheetNo = excelSheet.sheetNo();
            Object sheetData = dataList.get(sheetNo);
            fillToSheet(sheetData, excelWriter, excelSheet);
        }
    }

    private void fillToSheet(@NonNull Object data, @NonNull ExcelWriter excelWriter, @NonNull ExcelSheet excelSheet) {
        WriteSheet writeSheet = WriteSheetCustomizer.build(excelSheet);
        if (data instanceof Collection) {
            excelWriter.fill(data, writeSheet);
            return;
        }
        Map<String, Object> dataMap = BeanUtil.beanToMap(data);
        Map<String, Object> fillMap = new HashMap<>();
        FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
        for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Collection<?> column) {
                excelWriter.fill(new FillWrapper(key, column), fillConfig, writeSheet);
            } else {
                fillMap.put(key, value);
            }
        }
        if (!fillMap.isEmpty()) {
            excelWriter.fill(fillMap, fillConfig, writeSheet);
        }
    }

    private void writeToExcel(@NonNull List<?> data, @NonNull ExcelWriter excelWriter, @NonNull ExcelResponse excelResponse) {
        ExcelSheet[] excelSheets = excelResponse.sheets();
        if (excelSheets.length == 1) {
            writeToSheet(data, excelWriter, excelSheets[0]);
            return;
        }
        for (ExcelSheet excelSheet : excelSheets) {
            int sheetNo = excelSheet.sheetNo();
            List<?> sheetData = Convert.toList(data.get(sheetNo));
            writeToSheet(sheetData, excelWriter, excelSheet);
        }
    }

    private void writeToSheet(@NonNull List<?> data, @NonNull ExcelWriter excelWriter, @NonNull ExcelSheet excelSheet) {
        WriteSheet writeSheet = WriteSheetCustomizer.build(excelSheet);
        List<WriteTable> writeTables = WriteTableCustomizer.build(CollUtil.toList(excelSheet.tables()));
        if (writeTables.isEmpty()) {
            writeToTable(data, excelWriter, writeSheet, null);
            return;
        }
        if (writeTables.size() == 1) {
            writeToTable(data, excelWriter, writeSheet, writeTables.getFirst());
            return;
        }
        for (WriteTable writeTable : writeTables) {
            int tableNo = writeTable.getTableNo();
            List<?> tableData = Convert.toList(data.get(tableNo));
            writeToTable(tableData, excelWriter, writeSheet, writeTable);
        }
    }

    /**
     * 将数据写入Excel表格
     *
     * @param data        需要写入Excel的 数据列表，不能为空
     * @param excelWriter Excel写入器，用于执行写入操作
     * @param writeSheet  描述写入工作表的相关信息
     * @param writeTable  可选参数，描述写入表格的相关信息如果未提供，则默认使用writeSheet中的配置
     */
    private void writeToTable(@NonNull List<?> data, @NonNull ExcelWriter excelWriter, @NonNull WriteSheet writeSheet, WriteTable writeTable) {
        // 检查数据列表是否为空，如果为空则抛出异常
        if (data.isEmpty()) {
            throw new IllegalArgumentException("数据列表不能为空");
        }

        // 获取数据列表中第一个元素的类类型
        Class<?> clazz = data.getFirst().getClass();

        // 根据writeTable参数是否存在，选择合适的写入方式
        if (writeTable != null) {
            writeTable.setClazz(clazz);
            excelWriter.write(data, writeSheet, writeTable);
        } else {
            writeSheet.setClazz(clazz);
            excelWriter.write(data, writeSheet);
        }
    }

}
