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

        try (OutputStream outputStream = createOutputStream(webRequest, excelResponse)) {
            exportExcel(returnValue, outputStream, excelResponse);
        } catch (IOException e) {
            log.error("导出Excel文件时发生错误", e);
            throw e;
        }
    }

    private OutputStream createOutputStream(NativeWebRequest webRequest, ExcelResponse excelResponse) throws IOException {
        HttpServletResponse response = Optional.ofNullable(webRequest.getNativeResponse(HttpServletResponse.class))
                .orElseThrow(() -> new IllegalArgumentException("无法获取HttpServletResponse"));

        String fileName = excelResponse.filename();
        String excelType = excelResponse.excelType().getValue();

        // 验证文件名
        if (StrUtil.isBlank(fileName)) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        if (fileName.length() > MAX_FILENAME_LENGTH - excelType.length()) {
            throw new IllegalArgumentException("文件名过长");
        }

        // 设置响应头
        response.setContentType(EXCEL_CONTENT_TYPE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String sanitizedFileName = fileName.replaceAll(ILLEGAL_FILENAME_CHARS, "_");
        String encodedFileName = URLUtil.encode(sanitizedFileName, StandardCharsets.UTF_8);
        String fullFileName = encodedFileName + excelType;

        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format(CONTENT_DISPOSITION_FORMAT, fullFileName));
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

        return response.getOutputStream();
    }

    private void exportExcel(Object data, OutputStream outputStream, ExcelResponse excelResponse) {
        try (ExcelWriter excelWriter = WriteWorkbookCustomizer.getExcelWriter(excelResponse, outputStream)) {
            List<ExcelSheet> sheets = getValidatedSheets(excelResponse);

            if (StrUtil.isNotEmpty(excelResponse.template())) {
                fillTemplateExcel(data, excelWriter, sheets);
            } else {
                writeDataExcel(data, excelWriter, sheets);
            }
        } catch (Exception e) {
            log.error("Excel导出失败: {}", e.getMessage(), e);
            throw new RuntimeException("Excel导出失败", e);
        }
    }

    private void fillTemplateExcel(Object data, ExcelWriter excelWriter, List<ExcelSheet> sheets) {
        for (ExcelSheet sheet : sheets) {
            Object sheetData = getDataAtIndex(data, sheets, sheet.sheetNo());
            WriteSheet writeSheet = WriteSheetCustomizer.getWriteSheet(sheet);

            if (sheetData instanceof Collection) {
                excelWriter.fill(sheetData, DEFAULT_FILL_CONFIG, writeSheet);
                continue;
            }

            // 处理复杂对象填充
            Map<String, Object> dataMap = BeanUtil.beanToMap(sheetData);
            Map<String, Object> simpleData = new HashMap<>();

            for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                if (entry.getValue() instanceof Collection<?> collection) {
                    excelWriter.fill(new FillWrapper(entry.getKey(), collection), DEFAULT_FILL_CONFIG, writeSheet);
                } else {
                    simpleData.put(entry.getKey(), entry.getValue());
                }
            }

            if (!simpleData.isEmpty()) {
                excelWriter.fill(simpleData, DEFAULT_FILL_CONFIG, writeSheet);
            }
        }
    }

    private void writeDataExcel(Object data, ExcelWriter excelWriter, List<ExcelSheet> sheets) {
        List<?> dataList = normalizeToList(data);

        for (ExcelSheet sheet : sheets) {
            List<?> sheetData = normalizeToList(getDataAtIndex(dataList, sheets, sheet.sheetNo()));
            WriteSheet writeSheet = WriteSheetCustomizer.getWriteSheet(sheet);
            List<WriteTable> writeTables = WriteTableCustomizer.getWriteTables(sheet.tables());

            if (writeTables.isEmpty()) {
                writeDataToSheet(sheetData, excelWriter, writeSheet, null);
            } else {
                for (WriteTable writeTable : writeTables) {
                    List<?> tableData = normalizeToList(getDataAtIndex(sheetData, writeTables, writeTable.getTableNo()));
                    writeDataToSheet(tableData, excelWriter, writeSheet, writeTable);
                }
            }
        }
    }

    private void writeDataToSheet(List<?> data, ExcelWriter excelWriter, WriteSheet writeSheet, WriteTable writeTable) {
        if (data.isEmpty()) {
            throw new IllegalArgumentException("数据列表不能为空");
        }

        Class<?> clazz = data.getFirst().getClass();
        for (Object item : data) {
            if (item == null || !clazz.equals(item.getClass())) {
                throw new IllegalArgumentException("数据列表元素类型不一致或包含null元素");
            }
        }

        if (writeTable != null) {
            writeTable.setClazz(clazz);
            excelWriter.write(data, writeSheet, writeTable);
        } else {
            writeSheet.setClazz(clazz);
            excelWriter.write(data, writeSheet);
        }
    }

    private List<ExcelSheet> getValidatedSheets(ExcelResponse excelResponse) {
        ExcelSheet[] sheets = excelResponse.sheets();
        if (sheets == null || sheets.length == 0) {
            throw new IllegalArgumentException("Excel工作表配置不能为空");
        }
        return List.of(sheets);
    }

    private Object getDataAtIndex(Object data, List<?> containers, int index) {
        if (containers.size() == 1) {
            return data;
        }
        List<?> dataList = normalizeToList(data);
        if (index < 0 || index >= dataList.size()) {
            throw new IllegalArgumentException("索引 " + index + " 超出数据范围(0-" + (dataList.size() - 1) + ")");
        }
        return dataList.get(index);
    }

    private List<?> normalizeToList(Object data) {
        return switch (data) {
            case null -> Collections.emptyList();
            case List<?> list -> list;
            case Collection<?> collection -> new ArrayList<>(collection);
            default -> List.of(data);
        };
    }
}
