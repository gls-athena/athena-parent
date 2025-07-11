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
 * 实现Spring MVC的HandlerMethodReturnValueHandler接口，用于处理标记了@ExcelResponse注解的方法返回值
 * 支持Excel文件的导出，包括模板填充和数据写入两种模式
 *
 * @author george
 */
@Slf4j
public class ExcelResponseHandler implements HandlerMethodReturnValueHandler {

    /**
     * Excel文件的MIME类型
     */
    private static final String EXCEL_CONTENT_TYPE = "application/vnd.ms-excel";
    /**
     * HTTP响应头Content-Disposition的格式模板
     */
    private static final String CONTENT_DISPOSITION_FORMAT = "attachment;filename=%s";
    /**
     * 文件名最大长度限制
     */
    private static final int MAX_FILENAME_LENGTH = 255;
    /**
     * 文件名中的非法字符正则表达式
     */
    private static final String ILLEGAL_FILENAME_CHARS = "[\\x00-\\x1F\\x7F\"\\\\/:*?<>|]";
    /**
     * 默认的填充配置，设置强制新行
     */
    private static final FillConfig DEFAULT_FILL_CONFIG = FillConfig.builder().forceNewRow(true).build();

    /**
     * 判断是否支持处理指定的返回值类型
     *
     * @param returnType 方法返回值参数信息
     * @return 如果方法标记了@ExcelResponse注解则返回true，否则返回false
     */
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.hasMethodAnnotation(ExcelResponse.class);
    }

    /**
     * 处理方法返回值，将数据导出为Excel文件
     *
     * @param returnValue  方法的返回值数据
     * @param returnType   方法返回值参数信息
     * @param mavContainer ModelAndView容器
     * @param webRequest   Web请求对象
     * @throws Exception 处理过程中可能发生的异常
     */
    @Override
    public void handleReturnValue(Object returnValue, @NonNull MethodParameter returnType,
                                  @NonNull ModelAndViewContainer mavContainer, @NonNull NativeWebRequest webRequest) throws Exception {
        // 标记请求已被处理，防止其他处理器继续处理
        mavContainer.setRequestHandled(true);

        // 获取@ExcelResponse注解配置
        ExcelResponse excelResponse = Optional.ofNullable(returnType.getMethodAnnotation(ExcelResponse.class))
                .orElseThrow(() -> new IllegalArgumentException("方法返回值必须使用@ExcelResponse注解标记"));

        // 创建输出流并导出Excel文件
        try (OutputStream outputStream = createOutputStream(webRequest, excelResponse)) {
            exportExcel(returnValue, outputStream, excelResponse);
        } catch (IOException e) {
            log.error("导出Excel文件时发生错误", e);
            throw e;
        }
    }

    /**
     * 创建输出流并设置HTTP响应头
     *
     * @param webRequest    Web请求对象
     * @param excelResponse Excel响应配置注解
     * @return 用于写入Excel数据的输出流
     * @throws IOException 创建输出流时可能发生的IO异常
     */
    private OutputStream createOutputStream(NativeWebRequest webRequest, ExcelResponse excelResponse) throws IOException {
        // 获取HTTP响应对象
        HttpServletResponse response = Optional.ofNullable(webRequest.getNativeResponse(HttpServletResponse.class))
                .orElseThrow(() -> new IllegalArgumentException("无法获取HttpServletResponse"));

        String fileName = excelResponse.filename();
        String excelType = excelResponse.excelType().getValue();

        // 验证文件名合法性
        if (StrUtil.isBlank(fileName)) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        if (fileName.length() > MAX_FILENAME_LENGTH - excelType.length()) {
            throw new IllegalArgumentException("文件名过长");
        }

        // 设置响应头信息
        response.setContentType(EXCEL_CONTENT_TYPE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // 清理文件名中的非法字符并进行URL编码
        String sanitizedFileName = fileName.replaceAll(ILLEGAL_FILENAME_CHARS, "_");
        String encodedFileName = URLUtil.encode(sanitizedFileName, StandardCharsets.UTF_8);
        String fullFileName = encodedFileName + excelType;

        // 设置文件下载相关的响应头
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format(CONTENT_DISPOSITION_FORMAT, fullFileName));
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

        return response.getOutputStream();
    }

    /**
     * 导出Excel文件的核心方法
     * 根据配置决定使用模板填充还是直接写入数据
     *
     * @param data          要导出的数据
     * @param outputStream  输出流
     * @param excelResponse Excel响应配置注解
     */
    private void exportExcel(Object data, OutputStream outputStream, ExcelResponse excelResponse) {
        try (ExcelWriter excelWriter = WriteWorkbookCustomizer.getExcelWriter(excelResponse, outputStream)) {
            // 获取并验证工作表配置
            List<ExcelSheet> sheets = getValidatedSheets(excelResponse);

            // 根据是否配置模板选择不同的导出方式
            if (StrUtil.isNotEmpty(excelResponse.template())) {
                // 使用模板填充方式
                fillTemplateExcel(data, excelWriter, sheets);
            } else {
                // 直接写入数据方式
                writeDataExcel(data, excelWriter, sheets);
            }
        } catch (Exception e) {
            log.error("Excel导出失败: {}", e.getMessage(), e);
            throw new RuntimeException("Excel导出失败", e);
        }
    }

    /**
     * 使用模板填充方式导出Excel
     * 支持简单数据填充和集合数据填充
     *
     * @param data        要填充的数据
     * @param excelWriter Excel写入器
     * @param sheets      工作表配置列表
     */
    private void fillTemplateExcel(Object data, ExcelWriter excelWriter, List<ExcelSheet> sheets) {
        for (ExcelSheet sheet : sheets) {
            // 根据工作表索引获取对应的数据
            Object sheetData = getDataAtIndex(data, sheets, sheet.sheetNo());
            WriteSheet writeSheet = WriteSheetCustomizer.getWriteSheet(sheet);

            // 如果数据是集合类型，直接填充
            if (sheetData instanceof Collection) {
                excelWriter.fill(sheetData, DEFAULT_FILL_CONFIG, writeSheet);
                continue;
            }

            // 处理复杂对象填充：将对象转换为Map并分别处理简单数据和集合数据
            Map<String, Object> dataMap = BeanUtil.beanToMap(sheetData);
            Map<String, Object> simpleData = new HashMap<>();

            for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                if (entry.getValue() instanceof Collection<?> collection) {
                    // 集合数据使用FillWrapper包装后填充
                    excelWriter.fill(new FillWrapper(entry.getKey(), collection), DEFAULT_FILL_CONFIG, writeSheet);
                } else {
                    // 简单数据暂存，稍后一次性填充
                    simpleData.put(entry.getKey(), entry.getValue());
                }
            }

            // 填充简单数据
            if (!simpleData.isEmpty()) {
                excelWriter.fill(simpleData, DEFAULT_FILL_CONFIG, writeSheet);
            }
        }
    }

    /**
     * 直接写入数据方式导出Excel
     * 支持多工作表和多表格的数据写入
     *
     * @param data        要写入的数据
     * @param excelWriter Excel写入器
     * @param sheets      工作表配置列表
     */
    private void writeDataExcel(Object data, ExcelWriter excelWriter, List<ExcelSheet> sheets) {
        // 将数据标准化为列表格式
        List<?> dataList = normalizeToList(data);

        for (ExcelSheet sheet : sheets) {
            // 根据工作表索引获取对应的数据
            List<?> sheetData = normalizeToList(getDataAtIndex(dataList, sheets, sheet.sheetNo()));
            WriteSheet writeSheet = WriteSheetCustomizer.getWriteSheet(sheet);
            List<WriteTable> writeTables = WriteTableCustomizer.getWriteTables(sheet.tables());

            if (writeTables.isEmpty()) {
                // 没有配置表格，直接写入工作表
                writeDataToSheet(sheetData, excelWriter, writeSheet, null);
            } else {
                // 有配置表格，按表格分别写入数据
                for (WriteTable writeTable : writeTables) {
                    List<?> tableData = normalizeToList(getDataAtIndex(sheetData, writeTables, writeTable.getTableNo()));
                    writeDataToSheet(tableData, excelWriter, writeSheet, writeTable);
                }
            }
        }
    }

    /**
     * 将数据写入指定的工作表或表格
     *
     * @param data        要写入的数据列表
     * @param excelWriter Excel写入器
     * @param writeSheet  工作表配置
     * @param writeTable  表格配置，可为null
     */
    private void writeDataToSheet(List<?> data, ExcelWriter excelWriter, WriteSheet writeSheet, WriteTable writeTable) {
        if (data.isEmpty()) {
            throw new IllegalArgumentException("数据列表不能为空");
        }

        // 验证数据类型一致性
        Class<?> clazz = data.getFirst().getClass();
        for (Object item : data) {
            if (item == null || !clazz.equals(item.getClass())) {
                throw new IllegalArgumentException("数据列表元素类型不一致或包含null元素");
            }
        }

        // 根据是否有表格配置选择写入方式
        if (writeTable != null) {
            writeTable.setClazz(clazz);
            excelWriter.write(data, writeSheet, writeTable);
        } else {
            writeSheet.setClazz(clazz);
            excelWriter.write(data, writeSheet);
        }
    }

    /**
     * 获取并验证工作表配置
     *
     * @param excelResponse Excel响应配置注解
     * @return 工作表配置列表
     * @throws IllegalArgumentException 如果工作表配置为空
     */
    private List<ExcelSheet> getValidatedSheets(ExcelResponse excelResponse) {
        ExcelSheet[] sheets = excelResponse.sheets();
        if (sheets == null || sheets.length == 0) {
            throw new IllegalArgumentException("Excel工作表配置不能为空");
        }
        return List.of(sheets);
    }

    /**
     * 根据索引从数据中获取指定位置的数据
     *
     * @param data       原始数据
     * @param containers 容器列表（用于确定是否需要按索引获取）
     * @param index      要获取的索引
     * @return 指定索引位置的数据
     * @throws IllegalArgumentException 如果索引超出范围
     */
    private Object getDataAtIndex(Object data, List<?> containers, int index) {
        // 如果只有一个容器，直接返回原数据
        if (containers.size() == 1) {
            return data;
        }

        List<?> dataList = normalizeToList(data);
        if (index < 0 || index >= dataList.size()) {
            throw new IllegalArgumentException("索引 " + index + " 超出数据范围(0-" + (dataList.size() - 1) + ")");
        }
        return dataList.get(index);
    }

    /**
     * 将数据标准化为List格式
     * 统一处理各种数据类型，确保后续处理的一致性
     *
     * @param data 原始数据
     * @return 标准化后的List
     */
    private List<?> normalizeToList(Object data) {
        return switch (data) {
            case null -> Collections.emptyList();
            case List<?> list -> list;
            case Collection<?> collection -> new ArrayList<>(collection);
            default -> List.of(data);
        };
    }
}
