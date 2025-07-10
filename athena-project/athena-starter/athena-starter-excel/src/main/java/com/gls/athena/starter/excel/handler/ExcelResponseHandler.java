package com.gls.athena.starter.excel.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
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
 * <p>
 * 该类实现了Spring MVC的HandlerMethodReturnValueHandler接口，
 * 用于处理标注了@ExcelResponse注解的控制器方法的返回值，
 * 将返回的数据自动导出为Excel文件并通过HTTP响应返回给客户端。
 *
 * @author george
 */
@Slf4j
public class ExcelResponseHandler implements HandlerMethodReturnValueHandler {

    private static final String EXCEL_CONTENT_TYPE = "application/vnd.ms-excel";
    private static final String CONTENT_DISPOSITION_FORMAT = "attachment;filename=%s";
    private static final int MAX_FILENAME_LENGTH = 255;

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.hasMethodAnnotation(ExcelResponse.class);
    }

    @Override
    public void handleReturnValue(Object returnValue, @NonNull MethodParameter returnType,
                                  @NonNull ModelAndViewContainer mavContainer, @NonNull NativeWebRequest webRequest) throws Exception {
        mavContainer.setRequestHandled(true);

        ExcelResponse excelResponse = returnType.getMethodAnnotation(ExcelResponse.class);
        if (excelResponse == null) {
            throw new IllegalArgumentException("方法返回值必须使用@ExcelResponse注解标记");
        }

        try (OutputStream outputStream = getOutputStream(webRequest, excelResponse.filename(), excelResponse.excelType().getValue())) {
            exportToExcel(returnValue, outputStream, excelResponse);
        } catch (IOException e) {
            log.error("导出Excel文件时发生错误", e);
            throw e;
        }
    }

    private OutputStream getOutputStream(NativeWebRequest webRequest, String fileName, String excelType) throws IOException {
        validateFileName(fileName, excelType);

        HttpServletResponse response = Optional.ofNullable(webRequest.getNativeResponse(HttpServletResponse.class))
                .orElseThrow(() -> new IllegalArgumentException("无法获取HttpServletResponse"));

        setupResponseHeaders(response, fileName, excelType);
        return response.getOutputStream();
    }

    private void validateFileName(String fileName, String excelType) {
        if (StrUtil.isEmpty(fileName) || StrUtil.isEmpty(excelType)) {
            throw new IllegalArgumentException("文件名和文件类型不能为空");
        }
        if (fileName.length() > MAX_FILENAME_LENGTH - excelType.length()) {
            throw new IllegalArgumentException("文件名过长");
        }
    }

    private void setupResponseHeaders(HttpServletResponse response, String fileName, String excelType) {
        response.setContentType(EXCEL_CONTENT_TYPE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String sanitizedFileName = fileName.replaceAll("[\\x00-\\x1F\\x7F\"\\\\/:*?<>|]", "_");
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
        ExcelSheet[] sheets = validateAndGetSheets(excelResponse);

        if (sheets.length == 1) {
            fillToSheet(data, excelWriter, sheets[0]);
        } else {
            List<?> dataList = convertToList(data);
            for (ExcelSheet sheet : sheets) {
                validateIndex(sheet.sheetNo(), dataList.size(), "sheetNo");
                fillToSheet(dataList.get(sheet.sheetNo()), excelWriter, sheet);
            }
        }
    }

    private void fillToSheet(Object data, ExcelWriter excelWriter, ExcelSheet excelSheet) {
        WriteSheet writeSheet = WriteSheetCustomizer.getWriteSheet(excelSheet);
        FillConfig fillConfig = FillConfig.builder().forceNewRow(true).build();

        if (data instanceof Collection) {
            excelWriter.fill(data, fillConfig, writeSheet);
            return;
        }

        Map<String, Object> dataMap = BeanUtil.beanToMap(data);
        Map<String, Object> fillMap = new HashMap<>();

        dataMap.forEach((key, value) -> {
            if (value instanceof Collection<?> collection) {
                excelWriter.fill(new FillWrapper(key, collection), fillConfig, writeSheet);
            } else {
                fillMap.put(key, value);
            }
        });

        if (!fillMap.isEmpty()) {
            excelWriter.fill(fillMap, fillConfig, writeSheet);
        }
    }

    private void writeToExcel(List<?> data, ExcelWriter excelWriter, ExcelResponse excelResponse) {
        if (CollUtil.isEmpty(data)) {
            throw new IllegalArgumentException("数据列表不能为空");
        }

        ExcelSheet[] sheets = validateAndGetSheets(excelResponse);

        if (sheets.length == 1) {
            writeToSheet(data, excelWriter, sheets[0]);
        } else {
            for (ExcelSheet sheet : sheets) {
                validateIndex(sheet.sheetNo(), data.size(), "sheetNo");
                writeToSheet(convertToList(data.get(sheet.sheetNo())), excelWriter, sheet);
            }
        }
    }

    private void writeToSheet(List<?> data, ExcelWriter excelWriter, ExcelSheet excelSheet) {
        WriteSheet writeSheet = WriteSheetCustomizer.getWriteSheet(excelSheet);
        List<WriteTable> writeTables = WriteTableCustomizer.getWriteTables(excelSheet.tables());

        if (writeTables.isEmpty()) {
            writeData(data, excelWriter, writeSheet, null);
        } else if (writeTables.size() == 1) {
            writeData(data, excelWriter, writeSheet, writeTables.getFirst());
        } else {
            for (WriteTable writeTable : writeTables) {
                int tableNo = writeTable.getTableNo();
                validateIndex(tableNo, data.size(), "tableNo");
                writeData(convertToList(data.get(tableNo)), excelWriter, writeSheet, writeTable);
            }
        }
    }

    private void writeData(List<?> data, ExcelWriter excelWriter, WriteSheet writeSheet, WriteTable writeTable) {
        if (CollUtil.isEmpty(data)) {
            throw new IllegalArgumentException("数据列表不能为空");
        }

        Class<?> clazz = validateDataConsistency(data);

        if (writeTable != null) {
            writeTable.setClazz(clazz);
            excelWriter.write(data, writeSheet, writeTable);
        } else {
            writeSheet.setClazz(clazz);
            excelWriter.write(data, writeSheet);
        }
    }

    private Class<?> validateDataConsistency(List<?> data) {
        Class<?> clazz = data.getFirst().getClass();
        for (Object item : data) {
            if (item == null || !clazz.equals(item.getClass())) {
                throw new IllegalArgumentException("数据列表元素类型不一致或包含null元素");
            }
        }
        return clazz;
    }

    private ExcelSheet[] validateAndGetSheets(ExcelResponse excelResponse) {
        ExcelSheet[] sheets = excelResponse.sheets();
        if (sheets == null || sheets.length == 0) {
            throw new IllegalArgumentException("Excel工作表配置不能为空");
        }
        return sheets;
    }

    private void validateIndex(int index, int dataSize, String indexName) {
        if (index < 0 || index >= dataSize) {
            throw new IllegalArgumentException(indexName + " " + index + "超出数据范围(0-" + (dataSize - 1) + ")");
        }
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
