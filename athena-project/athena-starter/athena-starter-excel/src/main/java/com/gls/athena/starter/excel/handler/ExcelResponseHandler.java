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
     * <p>
     * 该方法用于判断当前方法的返回类型是否支持Excel响应处理。通过检查方法是否带有@ExcelResponse注解来确定。
     *
     * @param returnType 方法的返回类型参数，包含方法的元数据信息
     * @return 如果方法带有@ExcelResponse注解，则返回true，否则返回false
     */
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        // 检查方法是否带有@ExcelResponse注解
        return returnType.hasMethodAnnotation(ExcelResponse.class);
    }

    /**
     * 处理Excel导出响应
     * <p>
     * 将返回值转换为Excel文件并写入响应流。该方法首先验证返回值类型，然后将返回值转换为Excel格式，
     * 并通过输出流将生成的Excel文件写入响应中。
     *
     * @param returnValue  控制器方法的返回值，通常是一个列表，包含要导出到Excel的数据
     * @param returnType   控制器方法的返回类型信息，用于确定如何生成Excel文件
     * @param mavContainer 模型和视图容器，用于标记请求是否已处理
     * @param webRequest   当前的Web请求，用于获取输出流
     * @throws Exception 如果在处理过程中发生错误，抛出异常
     */
    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        // 验证返回值类型是否符合预期
        validateReturnType(returnType, returnValue);

        // 将返回值转换为列表形式，准备写入Excel
        List<?> data = (List<?>) returnValue;
        // 获取Excel响应的配置信息
        ExcelResponse excelResponse = getExcelResponse(returnType);

        // 获取输出流并创建Excel写入器
        try (OutputStream outputStream = getOutputStream(webRequest, excelResponse)) {
            ExcelWriter excelWriter = getExcelWriter(outputStream, excelResponse);
            try {
                // 获取Excel工作表和表格的配置信息
                List<WriteSheet> writeSheetList = getExcelWriterSheet(excelResponse);
                Map<Integer, List<WriteTable>> writeTableMap = getExcelWriterTable(excelResponse);
                // 将数据写入Excel文件
                writeData(excelWriter, writeSheetList, writeTableMap, data);
            } finally {
                // 确保Excel写入器完成写入操作
                excelWriter.finish();
            }
        }

        // 标记请求已处理
        mavContainer.setRequestHandled(true);
    }

    /**
     * 校验返回值类型是否符合要求。
     * <p>
     * 该方法用于检查方法的返回值类型是否为List类型，并且返回值是否为空。
     * 如果返回值类型不是List类型，或者返回值为空，则抛出IllegalArgumentException异常。
     *
     * @param returnType  方法返回值的类型信息，包含返回值的Class类型等。
     * @param returnValue 方法的实际返回值，需要校验其类型和内容。
     * @throws IllegalArgumentException 当返回值类型不是List类型，或者返回值为空时抛出该异常。
     */
    private void validateReturnType(MethodParameter returnType, Object returnValue) {
        // 检查返回值类型是否为List类型
        if (!List.class.isAssignableFrom(returnType.getParameterType())) {
            throw new IllegalArgumentException(ERROR_PREFIX + "@ExcelResponse只支持List类型返回值");
        }

        // 检查返回值是否为空或空列表
        if (returnValue == null || ((List<?>) returnValue).isEmpty()) {
            throw new IllegalArgumentException(ERROR_PREFIX + "数据不能为空");
        }
    }

    /**
     * 写入Excel数据
     * <p>
     * 该方法用于将数据写入Excel文件，支持多sheet和多table的数据写入，并自动处理数据分组。
     * 根据传入的WriteSheet列表和WriteTable映射，将数据写入对应的sheet和table中。
     *
     * @param excelWriter    ExcelWriter对象，用于执行Excel写入操作
     * @param writeSheetList WriteSheet列表，包含所有需要写入的sheet信息
     * @param writeTableMap  WriteTable映射，key为sheet编号，value为该sheet下的WriteTable列表
     * @param data           待写入的数据列表
     */
    private void writeData(ExcelWriter excelWriter, List<WriteSheet> writeSheetList, Map<Integer, List<WriteTable>> writeTableMap, List<?> data) {
        // 遍历所有WriteSheet，逐个处理每个sheet的数据写入
        writeSheetList.forEach(writeSheet -> {
            // 获取当前sheet对应的数据
            List<?> sheetData = getSheetData(writeSheet, data, writeSheetList.size());
            if (sheetData.isEmpty()) {
                return;
            }

            // 获取当前sheet对应的WriteTable列表
            List<WriteTable> writeTableList = writeTableMap.get(writeSheet.getSheetNo());
            if (CollUtil.isNotEmpty(writeTableList)) {
                // 如果存在WriteTable列表，则将数据写入对应的table中
                writeTableData(excelWriter, writeSheet, writeTableList, sheetData);
            } else {
                // 如果不存在WriteTable列表，则将数据直接写入sheet中
                writeSheetData(excelWriter, writeSheet, sheetData);
            }
        });
    }

    /**
     * 获取sheet数据
     *
     * @param writeSheet 包含sheet信息的对象，用于获取sheet的序号
     * @param data       包含所有sheet数据的列表，每个元素可能是一个列表，表示单个sheet的数据
     * @param sheetCount 指定返回数据的sheet数量。如果为1，则返回全部数据；否则返回对应sheet序号的数据
     * @return 返回指定sheet的数据列表。如果sheetCount为1，则返回整个数据列表；否则返回对应sheet序号的数据列表
     */
    private List<?> getSheetData(WriteSheet writeSheet, List<?> data, int sheetCount) {
        // 根据sheetCount的值决定返回全部数据还是指定sheet的数据
        return sheetCount == 1 ? data : (List<?>) data.get(writeSheet.getSheetNo());
    }

    /**
     * 写入table数据
     * <p>
     * 该方法用于处理单个sheet中的多个table数据写入。通过遍历writeTableList，将每个table对应的数据写入到指定的sheet中。
     *
     * @param excelWriter    ExcelWriter对象，用于执行写入操作
     * @param writeSheet     WriteSheet对象，表示要写入的sheet
     * @param writeTableList List<WriteTable>对象，包含所有要写入的table信息
     * @param sheetData      List<?>对象，包含所有sheet中的数据，每个元素对应一个table的数据
     */
    private void writeTableData(ExcelWriter excelWriter, WriteSheet writeSheet, List<WriteTable> writeTableList, List<?> sheetData) {
        // 遍历writeTableList，处理每个table的数据写入
        writeTableList.forEach(writeTable -> {
            // 获取当前table对应的数据
            List<?> tableData = (List<?>) sheetData.get(writeTable.getTableNo());
            // 如果数据不为空，则设置table的class类型并执行写入操作
            if (!tableData.isEmpty()) {
                writeTable.setClazz(tableData.getFirst().getClass());
                excelWriter.write(tableData, writeSheet, writeTable);
            }
        });
    }

    /**
     * 写入sheet数据
     * <p>
     * 该方法用于将数据写入Excel的单个sheet中。首先设置sheet的数据类型，然后将数据写入指定的sheet。
     *
     * @param excelWriter Excel写入器，用于执行实际的写入操作。
     * @param writeSheet  要写入的sheet对象，包含sheet的配置信息。
     * @param sheetData   要写入的数据列表，列表中的元素类型应与sheet的数据类型一致。
     */
    private void writeSheetData(ExcelWriter excelWriter, WriteSheet writeSheet, List<?> sheetData) {
        // 设置sheet的数据类型为列表中第一个元素的类型
        writeSheet.setClazz(sheetData.getFirst().getClass());

        // 将数据写入指定的sheet
        excelWriter.write(sheetData, writeSheet);
    }

    /**
     * 根据Excel响应对象生成一个包含所有表格的映射，其中键为工作表编号，值为该工作表中所有表格的列表。
     *
     * @param excelResponse Excel响应对象，包含多个工作表及其表格信息。
     * @return 一个Map，其中键为工作表的编号（Integer类型），值为该工作表中所有表格的列表（List<WriteTable>类型）。
     */
    private Map<Integer, List<WriteTable>> getExcelWriterTable(ExcelResponse excelResponse) {
        // 遍历Excel响应中的所有工作表，并将其转换为一个Map，键为工作表编号，值为该工作表中所有表格的列表
        return Arrays.stream(excelResponse.sheets()).collect(Collectors.toMap(ExcelSheet::sheetNo, excelSheet ->
                // 遍历当前工作表中的所有表格，并将其转换为WriteTable对象
                Arrays.stream(excelSheet.tables()).map(excelTable -> {
                    // 创建ExcelWriterTableBuilder对象，用于构建WriteTable
                    ExcelWriterTableBuilder excelWriterTableBuilder = EasyExcel.writerTable(excelTable.tableNo());
                    // 创建ExcelWriterTableBuilderCustomizer对象，用于自定义表格构建过程
                    ExcelWriterTableBuilderCustomizer excelWriterTableBuilderCustomizer = new ExcelWriterTableBuilderCustomizer(excelTable);
                    // 应用自定义逻辑到表格构建器
                    excelWriterTableBuilderCustomizer.customize(excelWriterTableBuilder);
                    // 构建并返回WriteTable对象
                    return excelWriterTableBuilder.build();
                }).toList()));
    }

    /**
     * 根据Excel响应对象生成对应的WriteSheet列表。
     * <p>
     * 该方法接收一个ExcelResponse对象，遍历其中的所有sheet配置，为每个sheet生成一个WriteSheet对象。
     * 每个WriteSheet对象通过EasyExcel的writerSheet方法创建，并使用自定义的ExcelWriterSheetBuilderCustomizer
     * 进行配置。最终返回包含所有WriteSheet对象的列表。
     *
     * @param excelResponse 包含Excel sheet配置的响应对象，不能为null
     * @return 返回一个包含所有WriteSheet对象的列表，列表中的每个WriteSheet对象对应一个Excel sheet
     */
    private List<WriteSheet> getExcelWriterSheet(ExcelResponse excelResponse) {
        // 遍历excelResponse中的所有sheet配置，为每个sheet生成一个WriteSheet对象
        return Arrays.stream(excelResponse.sheets()).map(excelSheet -> {
            // 使用EasyExcel创建WriteSheetBuilder，并设置sheet编号和名称
            ExcelWriterSheetBuilder excelWriterSheetBuilder = EasyExcel.writerSheet(excelSheet.sheetNo(), excelSheet.sheetName());

            // 使用自定义的ExcelWriterSheetBuilderCustomizer对WriteSheetBuilder进行配置
            ExcelWriterSheetBuilderCustomizer excelWriterSheetBuilderCustomizer = new ExcelWriterSheetBuilderCustomizer(excelSheet);
            excelWriterSheetBuilderCustomizer.customize(excelWriterSheetBuilder);

            // 构建并返回WriteSheet对象
            return excelWriterSheetBuilder.build();
        }).toList();
    }

    /**
     * 构建Excel写入器
     * <p>
     * 根据注解配置创建ExcelWriter实例。该方法通过给定的输出流和Excel响应对象，配置并构建一个Excel写入器。
     *
     * @param outputStream  输出流，用于指定Excel文件的写入位置。
     * @param excelResponse Excel响应对象，包含Excel文件的配置信息。
     * @return 返回一个配置好的ExcelWriter实例，用于写入Excel文件。
     */
    private ExcelWriter getExcelWriter(OutputStream outputStream, ExcelResponse excelResponse) {
        // 创建ExcelWriterBuilder实例，用于配置Excel写入器
        ExcelWriterBuilder excelWriterBuilder = EasyExcel.write(outputStream);

        // 创建ExcelWriterBuilderCustomizer实例，用于根据excelResponse自定义ExcelWriterBuilder
        ExcelWriterBuilderCustomizer excelWriterBuilderCustomizer = new ExcelWriterBuilderCustomizer(excelResponse);

        // 自定义ExcelWriterBuilder配置
        excelWriterBuilderCustomizer.customize(excelWriterBuilder);

        // 构建并返回ExcelWriter实例
        return excelWriterBuilder.build();
    }

    /**
     * 获取方法参数上的 {@link ExcelResponse} 注解。
     * <p>
     * 该方法通过传入的 {@link MethodParameter} 对象，获取该方法上的 {@link ExcelResponse} 注解。
     * 如果该方法上没有该注解，则返回 null。
     *
     * @param returnType 方法参数对象，表示要检查的方法参数
     * @return 返回方法参数上的 {@link ExcelResponse} 注解，如果不存在则返回 null
     */
    private ExcelResponse getExcelResponse(MethodParameter returnType) {
        return returnType.getMethodAnnotation(ExcelResponse.class);
    }

    /**
     * 获取输出流
     * <p>
     * 该方法用于设置HTTP响应头信息，并返回输出流以便将Excel文件写入响应中。
     * 方法首先从NativeWebRequest中获取HttpServletResponse对象，如果获取失败则抛出异常。
     * 然后设置响应的内容类型、字符编码，并根据ExcelResponse对象生成文件名，设置Content-Disposition头信息，
     * 最后返回响应对象的输出流。
     *
     * @param webRequest    NativeWebRequest对象，用于获取HttpServletResponse
     * @param excelResponse ExcelResponse对象，包含文件名和Excel类型信息
     * @return OutputStream 返回响应对象的输出流，用于写入Excel文件
     * @throws IOException              如果获取输出流失败时抛出
     * @throws IllegalArgumentException 如果HttpServletResponse为空时抛出
     */
    private OutputStream getOutputStream(NativeWebRequest webRequest, ExcelResponse excelResponse) throws IOException {
        // 从NativeWebRequest中获取HttpServletResponse对象
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        if (response == null) {
            throw new IllegalArgumentException(ERROR_PREFIX + "HttpServletResponse为空");
        }

        // 设置响应头信息
        response.setContentType(EXCEL_CONTENT_TYPE);
        response.setCharacterEncoding(UTF_8);
        String filename = URLUtil.encode(excelResponse.filename(), StandardCharsets.UTF_8) + excelResponse.excelType().getValue();
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename);
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

        // 返回响应对象的输出流
        return response.getOutputStream();
    }

}
