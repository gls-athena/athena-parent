package com.gls.athena.starter.excel.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.builder.ExcelWriterTableBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteTable;
import com.gls.athena.starter.excel.annotation.ExcelResponse;
import com.gls.athena.starter.excel.annotation.ExcelSheet;
import com.gls.athena.starter.excel.customizer.ExcelWriterBuilderCustomizer;
import com.gls.athena.starter.excel.customizer.ExcelWriterSheetBuilderCustomizer;
import com.gls.athena.starter.excel.customizer.ExcelWriterTableBuilderCustomizer;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Excel响应处理器
 * <p>
 * 处理带有 @ExcelResponse 注解的控制器方法返回值，将数据导出为Excel文件。
 * 支持多sheet、多table的导出，支持自定义导出样式。
 * <p>
 * 使用示例：
 * <pre>{@code
 * @GetMapping("/export")
 * @ExcelResponse(filename = "测试导出", sheets = {
 *     @ExcelSheet(sheetNo = 0, sheetName = "sheet1")
 * })
 * public List<UserDTO> export() {
 *     return userService.list();
 * }
 * }</pre>
 *
 * @author george
 */
@Slf4j
public class ExcelResponseHandler implements HandlerMethodReturnValueHandler {

    private static final String EXCEL_CONTENT_TYPE = "application/vnd.ms-excel";
    private static final String UTF_8 = "utf-8";
    private static final String ERROR_PREFIX = "Excel响应处理器错误: ";

    /**
     * 检查方法是否支持Excel响应处理
     *
     * @param returnType 方法返回类型参数
     * @return 如果方法带有@ExcelResponse注解则返回true
     */
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.hasMethodAnnotation(ExcelResponse.class);
    }

    /**
     * 处理Excel导出响应
     * <p>
     * 将返回值转换为Excel文件并写入响应流
     */
    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        validateReturnType(returnType, returnValue);

        List<?> data = (List<?>) returnValue;
        ExcelResponse excelResponse = getExcelResponse(returnType);

        try (OutputStream outputStream = getOutputStream(webRequest, excelResponse)) {
            ExcelWriter excelWriter = getExcelWriter(outputStream, excelResponse);
            try {
                List<WriteSheet> writeSheetList = getExcelWriterSheet(excelResponse);
                Map<Integer, List<WriteTable>> writeTableMap = getExcelWriterTable(excelResponse);
                writeData(excelWriter, writeSheetList, writeTableMap, data);
            } finally {
                excelWriter.finish();
            }
        }

        mavContainer.setRequestHandled(true);
    }

    /**
     * 校验返回值类型
     *
     * @throws IllegalArgumentException 当返回值不是List类型或数据为空时
     */
    private void validateReturnType(MethodParameter returnType, Object returnValue) {
        if (!List.class.isAssignableFrom(returnType.getParameterType())) {
            throw new IllegalArgumentException(ERROR_PREFIX + "@ExcelResponse只支持List类型返回值");
        }
        if (returnValue == null || ((List<?>) returnValue).isEmpty()) {
            throw new IllegalArgumentException(ERROR_PREFIX + "数据不能为空");
        }
    }

    /**
     * 写入Excel数据
     * <p>
     * 支持多sheet、多table的数据写入，自动处理数据分组
     */
    private void writeData(ExcelWriter excelWriter, List<WriteSheet> writeSheetList, Map<Integer, List<WriteTable>> writeTableMap, List<?> data) {
        writeSheetList.forEach(writeSheet -> {
            List<?> sheetData = getSheetData(writeSheet, data, writeSheetList.size());
            if (sheetData.isEmpty()) {
                return;
            }

            List<WriteTable> writeTableList = writeTableMap.get(writeSheet.getSheetNo());
            if (CollUtil.isNotEmpty(writeTableList)) {
                writeTableData(excelWriter, writeSheet, writeTableList, sheetData);
            } else {
                writeSheetData(excelWriter, writeSheet, sheetData);
            }
        });
    }

    /**
     * 获取sheet数据
     *
     * @param sheetCount 为1时返回全部数据，否则返回对应sheet序号的数据
     */
    private List<?> getSheetData(WriteSheet writeSheet, List<?> data, int sheetCount) {
        return sheetCount == 1 ? data : (List<?>) data.get(writeSheet.getSheetNo());
    }

    /**
     * 写入table数据
     * <p>
     * 处理单个sheet中的多个table数据写入
     */
    private void writeTableData(ExcelWriter excelWriter, WriteSheet writeSheet, List<WriteTable> writeTableList, List<?> sheetData) {
        writeTableList.forEach(writeTable -> {
            List<?> tableData = (List<?>) sheetData.get(writeTable.getTableNo());
            if (!tableData.isEmpty()) {
                writeTable.setClazz(tableData.getFirst().getClass());
                excelWriter.write(tableData, writeSheet, writeTable);
            }
        });
    }

    /**
     * 写入sheet数据
     * <p>
     * 处理单个sheet的数据写入
     */
    private void writeSheetData(ExcelWriter excelWriter, WriteSheet writeSheet, List<?> sheetData) {
        writeSheet.setClazz(sheetData.getFirst().getClass());
        excelWriter.write(sheetData, writeSheet);
    }

    /**
     * 获取ExcelWriterTable
     *
     * @param excelResponse Excel响应
     */
    private Map<Integer, List<WriteTable>> getExcelWriterTable(ExcelResponse excelResponse) {
        return Arrays.stream(excelResponse.sheets()).collect(Collectors.toMap(ExcelSheet::sheetNo, excelSheet -> Arrays.stream(excelSheet.tables()).map(excelTable -> {
            ExcelWriterTableBuilder excelWriterTableBuilder = EasyExcel.writerTable(excelTable.tableNo());
            ExcelWriterTableBuilderCustomizer excelWriterTableBuilderCustomizer = new ExcelWriterTableBuilderCustomizer(excelTable);
            excelWriterTableBuilderCustomizer.customize(excelWriterTableBuilder);
            return excelWriterTableBuilder.build();
        }).toList()));
    }

    /**
     * 获取ExcelWriterSheet
     *
     * @param excelResponse Excel响应
     * @return ExcelWriterSheet
     */
    private List<WriteSheet> getExcelWriterSheet(ExcelResponse excelResponse) {
        return Arrays.stream(excelResponse.sheets()).map(excelSheet -> {
            ExcelWriterSheetBuilder excelWriterSheetBuilder = EasyExcel.writerSheet(excelSheet.sheetNo(), excelSheet.sheetName());
            ExcelWriterSheetBuilderCustomizer excelWriterSheetBuilderCustomizer = new ExcelWriterSheetBuilderCustomizer(excelSheet);
            excelWriterSheetBuilderCustomizer.customize(excelWriterSheetBuilder);
            return excelWriterSheetBuilder.build();
        }).toList();

    }

    /**
     * 构建Excel写入器
     * <p>
     * 根据注解配置创建ExcelWriter实例
     */
    private ExcelWriter getExcelWriter(OutputStream outputStream, ExcelResponse excelResponse) {
        ExcelWriterBuilder excelWriterBuilder = EasyExcel.write(outputStream);
        ExcelWriterBuilderCustomizer excelWriterBuilderCustomizer = new ExcelWriterBuilderCustomizer(excelResponse);
        excelWriterBuilderCustomizer.customize(excelWriterBuilder);
        return excelWriterBuilder.build();
    }

    private ExcelResponse getExcelResponse(MethodParameter returnType) {
        return returnType.getMethodAnnotation(ExcelResponse.class);
    }

    /**
     * 获取输出流
     * <p>
     * 设置响应头信息并返回输出流
     *
     * @throws IOException 获取输出流失败时抛出
     */
    private OutputStream getOutputStream(NativeWebRequest webRequest, ExcelResponse excelResponse) throws IOException {
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        if (response == null) {
            throw new IllegalArgumentException(ERROR_PREFIX + "HttpServletResponse为空");
        }

        response.setContentType(EXCEL_CONTENT_TYPE);
        response.setCharacterEncoding(UTF_8);
        String filename = URLUtil.encode(excelResponse.filename(), StandardCharsets.UTF_8) + excelResponse.excelType().getValue();
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename);
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

        return response.getOutputStream();
    }
}
