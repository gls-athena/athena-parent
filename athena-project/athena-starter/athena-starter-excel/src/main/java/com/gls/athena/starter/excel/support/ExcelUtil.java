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

    public void exportToExcel(Object data, OutputStream outputStream, ExcelResponse excelResponse) {
        try (ExcelWriter excelWriter = ExcelWriterCustomizer.build(outputStream, excelResponse)) {
            if (StrUtil.isEmpty(excelResponse.template())) {
                writeToExcel(data, excelWriter, excelResponse);
            } else {
                fillToExcel(data, excelWriter, excelResponse);
            }
        } catch (Exception e) {
            log.error("Excel导出失败: {}", e.getMessage(), e);
            throw new RuntimeException("Excel导出失败", e);
        }
    }

    private void fillToExcel(Object data, ExcelWriter excelWriter, ExcelResponse excelResponse) {
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

    private void fillToSheet(Object data, ExcelWriter excelWriter, ExcelSheet excelSheet) {
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

    private void writeToExcel(Object data, ExcelWriter excelWriter, ExcelResponse excelResponse) {
        ExcelSheet[] excelSheets = excelResponse.sheets();
        if (excelSheets.length == 1) {
            writeToSheet(data, excelWriter, excelSheets[0]);
            return;
        }
        List<?> dataList = Convert.toList(data);
        for (ExcelSheet excelSheet : excelSheets) {
            int sheetNo = excelSheet.sheetNo();
            Object sheetData = dataList.get(sheetNo);
            writeToSheet(sheetData, excelWriter, excelSheet);
        }
    }

    private void writeToSheet(Object data, ExcelWriter excelWriter, ExcelSheet excelSheet) {
        List<?> dataList = Convert.toList(data);
        WriteSheet writeSheet = WriteSheetCustomizer.build(excelSheet);
        List<WriteTable> writeTables = WriteTableCustomizer.build(CollUtil.toList(excelSheet.tables()));
        writeToTables(excelWriter, writeSheet, writeTables, dataList);
    }

    private void writeToTables(ExcelWriter excelWriter, WriteSheet writeSheet, List<WriteTable> writeTableList, List<?> sheetData) {
        if (writeTableList.isEmpty()) {
            writeToTable(excelWriter, writeSheet, null, sheetData);
            return;
        }
        if (writeTableList.size() == 1) {
            writeToTable(excelWriter, writeSheet, writeTableList.getFirst(), sheetData);
            return;
        }
        for (WriteTable writeTable : writeTableList) {
            int tableNo = writeTable.getTableNo();
            List<?> tableData = Convert.toList(sheetData.get(tableNo));
            writeToTable(excelWriter, writeSheet, writeTable, tableData);
        }
    }

    private void writeToTable(ExcelWriter excelWriter, WriteSheet writeSheet, WriteTable writeTable, List<?> data) {
        Class<?> clazz = data.getFirst().getClass();
        if (writeTable != null) {
            writeTable.setClazz(clazz);
            excelWriter.write(data, writeSheet, writeTable);
        } else {
            writeSheet.setClazz(clazz);
            excelWriter.write(data, writeSheet);
        }
    }

}
