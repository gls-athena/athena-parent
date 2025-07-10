package com.gls.athena.starter.excel.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.idev.excel.ExcelWriter;
import cn.idev.excel.write.metadata.WriteSheet;
import cn.idev.excel.write.metadata.WriteTable;
import cn.idev.excel.write.metadata.fill.FillConfig;
import cn.idev.excel.write.metadata.fill.FillWrapper;
import com.gls.athena.starter.excel.annotation.ExcelResponse;
import com.gls.athena.starter.excel.annotation.ExcelSheet;
import com.gls.athena.starter.excel.customizer.WriteSheetCustomizer;
import com.gls.athena.starter.excel.customizer.WriteTableCustomizer;
import com.gls.athena.starter.excel.customizer.WriteWorkbookCustomizer;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Excel响应处理器
 *
 * @author george
 */
@Slf4j
public class ExcelResponseHandler implements HandlerMethodReturnValueHandler {

    private static final String EXCEL_CONTENT_TYPE = "application/vnd.ms-excel";
    private static final String CONTENT_DISPOSITION_FORMAT = "attachment;filename=%s";
    private static final int MAX_FILENAME_LENGTH = 255;
    private static final String ILLEGAL_FILENAME_CHARS = "[\\x00-\\x1F\\x7F\"\\\\/:*?<>|]";
    private static final FillConfig DEFAULT_FILL_CONFIG = FillConfig.builder().forceNewRow(true).build();

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.hasMethodAnnotation(ExcelResponse.class);
    }

    @Override
    public void handleReturnValue(Object returnValue, @NonNull MethodParameter returnType,
                                  @NonNull ModelAndViewContainer mavContainer, @NonNull NativeWebRequest webRequest) throws Exception {
        mavContainer.setRequestHandled(true);

        ExcelResponse excelResponse = Optional.ofNullable(returnType.getMethodAnnotation(ExcelResponse.class))
                .orElseThrow(() -> new IllegalArgumentException("方法返回值必须使用@ExcelResponse注解标记"));

        try (OutputStream outputStream = getOutputStream(webRequest, excelResponse.filename(), excelResponse.excelType().getValue())) {
            exportToExcel(returnValue, outputStream, excelResponse);
        } catch (IOException e) {
            log.error("导出Excel文件时发生错误", e);
            throw e;
        }
    }

    private OutputStream getOutputStream(NativeWebRequest webRequest, String fileName, String excelType) throws IOException {
        assert StrUtil.isNotBlank(fileName) : "文件名不能为空";
        assert StrUtil.isNotBlank(excelType) : "Excel类型不能为空";
        assert fileName.length() <= MAX_FILENAME_LENGTH - excelType.length() : "文件名过长";

        HttpServletResponse response = Optional.ofNullable(webRequest.getNativeResponse(HttpServletResponse.class))
                .orElseThrow(() -> new IllegalArgumentException("无法获取HttpServletResponse"));

        setupResponseHeaders(response, fileName, excelType);
        return response.getOutputStream();
    }

    private void setupResponseHeaders(HttpServletResponse response, String fileName, String excelType) {
        response.setContentType(EXCEL_CONTENT_TYPE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String sanitizedFileName = fileName.replaceAll(ILLEGAL_FILENAME_CHARS, "_");
        String encodedFileName = URLUtil.encode(sanitizedFileName, StandardCharsets.UTF_8);
        String fullFileName = encodedFileName + excelType;

        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format(CONTENT_DISPOSITION_FORMAT, fullFileName));
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
    }

    private void exportToExcel(Object data, OutputStream outputStream, ExcelResponse excelResponse) {
        try (ExcelWriter excelWriter = WriteWorkbookCustomizer.getExcelWriter(excelResponse, outputStream)) {
            if (StrUtil.isEmpty(excelResponse.template())) {
                writeToExcel(convertToList(data), excelWriter, excelResponse);
            } else {
                fillToExcel(data, excelWriter, excelResponse);
            }
        } catch (Exception e) {
            log.error("Excel导出失败: {}", e.getMessage(), e);
            throw new RuntimeException("Excel导出失败", e);
        }
    }

    private void fillToExcel(Object data, ExcelWriter excelWriter, ExcelResponse excelResponse) {
        List<ExcelSheet> sheets = validateAndGetSheets(excelResponse);
        sheets.forEach(sheet -> {
            Object sheetData = sheets.size() == 1 ? data : getDataByIndex(convertToList(data), sheet.sheetNo());
            fillToSheet(sheetData, excelWriter, sheet);
        });
    }

    private void fillToSheet(Object data, ExcelWriter excelWriter, ExcelSheet excelSheet) {
        WriteSheet writeSheet = WriteSheetCustomizer.getWriteSheet(excelSheet);

        if (data instanceof Collection) {
            excelWriter.fill(data, DEFAULT_FILL_CONFIG, writeSheet);
            return;
        }

        Map<String, Object> dataMap = BeanUtil.beanToMap(data);
        Map<String, Object> fillMap = new HashMap<>();

        dataMap.forEach((key, value) -> {
            if (value instanceof Collection<?> collection) {
                excelWriter.fill(new FillWrapper(key, collection), DEFAULT_FILL_CONFIG, writeSheet);
            } else {
                fillMap.put(key, value);
            }
        });

        if (!fillMap.isEmpty()) {
            excelWriter.fill(fillMap, DEFAULT_FILL_CONFIG, writeSheet);
        }
    }

    private void writeToExcel(List<?> data, ExcelWriter excelWriter, ExcelResponse excelResponse) {
        List<ExcelSheet> sheets = validateAndGetSheets(excelResponse);
        sheets.forEach(sheet -> {
            List<?> sheetData = sheets.size() == 1 ? data : convertToList(getDataByIndex(data, sheet.sheetNo()));
            writeToSheet(sheetData, excelWriter, sheet);
        });
    }

    private void writeToSheet(List<?> data, ExcelWriter excelWriter, ExcelSheet excelSheet) {
        WriteSheet writeSheet = WriteSheetCustomizer.getWriteSheet(excelSheet);
        List<WriteTable> writeTables = WriteTableCustomizer.getWriteTables(excelSheet.tables());

        if (writeTables.isEmpty()) {
            writeData(data, excelWriter, writeSheet, null);
        } else {
            writeTables.forEach(writeTable -> {
                List<?> tableData = writeTables.size() == 1 ? data : convertToList(getDataByIndex(data, writeTable.getTableNo()));
                writeData(tableData, excelWriter, writeSheet, writeTable);
            });
        }
    }

    private void writeData(List<?> data, ExcelWriter excelWriter, WriteSheet writeSheet, WriteTable writeTable) {
        Class<?> clazz = validateDataConsistency(data);
        if (writeTable != null) {
            writeTable.setClazz(clazz);
            excelWriter.write(data, writeSheet, writeTable);
        } else {
            writeSheet.setClazz(clazz);
            excelWriter.write(data, writeSheet);
        }
    }

    private Object getDataByIndex(List<?> data, int index) {
        if (index < 0 || index >= data.size()) {
            throw new IllegalArgumentException("索引" + " " + index + "超出数据范围(0-" + (data.size() - 1) + ")");
        }
        return data.get(index);
    }

    private Class<?> validateDataConsistency(List<?> data) {
        Class<?> clazz = data.getFirst().getClass();
        if (data.stream().anyMatch(item -> item == null || !clazz.equals(item.getClass()))) {
            throw new IllegalArgumentException("数据列表元素类型不一致或包含null元素");
        }
        return clazz;
    }

    private List<ExcelSheet> validateAndGetSheets(ExcelResponse excelResponse) {
        ExcelSheet[] sheets = excelResponse.sheets();
        if (sheets == null || sheets.length == 0) {
            throw new IllegalArgumentException("Excel工作表配置不能为空");
        }
        return List.of(sheets);
    }

    private List<?> convertToList(Object data) {
        return switch (data) {
            case List<?> list -> list;
            case Collection<?> collection -> new ArrayList<>(collection);
            case null -> Collections.emptyList();
            default -> List.of(data);
        };
    }
}
