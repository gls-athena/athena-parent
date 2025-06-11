package com.gls.athena.starter.excel.handler;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.builder.ExcelWriterTableBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteTable;
import com.gls.athena.starter.excel.annotation.ExcelResponse;
import com.gls.athena.starter.excel.annotation.ExcelSheet;
import com.gls.athena.starter.excel.annotation.ExcelTable;
import com.gls.athena.starter.excel.customizer.ExcelWriterBuilderCustomizer;
import com.gls.athena.starter.excel.customizer.ExcelWriterSheetBuilderCustomizer;
import com.gls.athena.starter.excel.customizer.ExcelWriterTableBuilderCustomizer;
import com.gls.athena.starter.excel.support.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Excel响应处理器
 * 处理带有@ExcelResponse注解的方法返回值，将数据写入Excel文件并返回给客户端。
 *
 * @author george
 */
@Slf4j
public class ExcelResponseHandler implements HandlerMethodReturnValueHandler {

    /**
     * 判断当前处理器是否支持给定的方法返回类型
     *
     * @param returnType 方法返回类型参数对象，包含方法元数据信息
     * @return boolean 返回true表示支持该返回类型（方法带有@ExcelResponse注解），否则返回false
     */
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        // 通过检查方法是否包含@ExcelResponse注解来确定是否支持该返回类型
        return returnType.hasMethodAnnotation(ExcelResponse.class);
    }

    /**
     * 处理Excel响应返回值，将数据写入Excel并输出到响应流中
     *
     * @param returnValue  控制器方法返回的数据对象
     * @param returnType   方法参数信息，包含方法注解等元数据
     * @param mavContainer ModelAndView容器，用于标记请求处理状态
     * @param webRequest   原生Web请求对象，用于获取输出流
     * @throws Exception 当Excel写入过程中发生错误时抛出
     */
    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        // 获取Excel响应的配置信息
        ExcelResponse excelResponse = returnType.getMethodAnnotation(ExcelResponse.class);
        log.info("ExcelResponseHandler: {}", excelResponse);
        // 创建Excel输出流并写入数据
        try (OutputStream outputStream = ExcelUtil.getOutputStream(webRequest, excelResponse.filename(), excelResponse.excelType().getValue())) {
            ExcelWriter excelWriter = getExcelWriter(outputStream, excelResponse);
            try {
                // 根据是否使用模板选择不同的Excel写入方式
                if (StrUtil.isEmpty(excelResponse.template())) {
                    // 如果没有模板，则直接写入数据
                    writeDataToExcel(returnValue, excelWriter, excelResponse);
                } else {
                    // 如果使用了模板，则填充数据
                    fillDataToExcel(returnValue, excelWriter, excelResponse);
                }
            } catch (Exception e) {
                log.error("Error writing Excel response", e);
                throw e;
            } finally {
                // 确保Excel写入器完成写入操作
                excelWriter.finish();
            }
        }
        // 标记请求已处理
        mavContainer.setRequestHandled(true);
    }

    /**
     * 将数据填充到Excel工作表中
     *
     * @param data          待填充的数据对象，可以是单个对象或对象列表
     * @param excelWriter   Excel写入工具实例，用于操作Excel文件
     * @param excelResponse Excel响应对象，包含工作表配置信息
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
     * 填充单个Excel工作表的数据
     *
     * @param sheetData   要填充到工作表的数据对象，可以是POJO、Map或List等类型
     * @param excelWriter Excel写入工具实例，用于执行实际的填充操作
     * @param excelSheet  Excel工作表配置信息，包含工作表名称等参数
     */
    private void fillSingleSheet(Object sheetData, ExcelWriter excelWriter, ExcelSheet excelSheet) {
        // 根据配置创建EasyExcel所需的WriteSheet对象
        WriteSheet writeSheet = getWriteSheet(excelSheet);

        // 使用ExcelWriter将数据填充到指定工作表
        ExcelUtil.fillSheetData(excelWriter, writeSheet, sheetData);
    }

    /**
     * 将数据写入Excel文件
     *
     * @param data          要写入的数据对象，可以是单个对象或对象列表
     * @param excelWriter   Excel写入工具实例，用于实际写入操作
     * @param excelResponse Excel响应对象，包含工作表配置信息
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
     * 将数据写入单个Excel工作表
     *
     * @param data        待写入的数据对象，将被转换为List形式
     * @param excelWriter Excel写入工具对象，用于执行实际的写入操作
     * @param excelSheet  工作表配置对象，包含工作表名称等配置信息
     */
    private void writeSingleSheet(Object data, ExcelWriter excelWriter, ExcelSheet excelSheet) {
        // 将输入数据统一转换为List格式
        List<?> dataList = Convert.toList(data);

        // 获取工作表配置并生成写入对象
        WriteSheet writeSheet = getWriteSheet(excelSheet);

        // 获取表格配置并生成多个写入表格对象
        List<WriteTable> writeTables = getWriteTables(excelSheet.tables());

        // 执行实际的数据写入操作
        ExcelUtil.writeSheetData(excelWriter, writeSheet, writeTables, dataList);
    }

    /**
     * 根据ExcelTable配置构建WriteTable对象
     *
     * @param excelTable Excel表格配置对象，包含表格编号等配置信息
     * @return WriteTable 可写入的表格对象，用于Excel写入操作
     */
    private WriteTable getWriteTable(ExcelTable excelTable) {
        // 初始化表格构建器，设置基础表格编号
        ExcelWriterTableBuilder excelWriterTableBuilder = EasyExcel.writerTable(excelTable.tableNo());

        // 创建并应用表格构建自定义逻辑
        ExcelWriterTableBuilderCustomizer excelWriterTableBuilderCustomizer = new ExcelWriterTableBuilderCustomizer(excelTable);
        excelWriterTableBuilderCustomizer.customize(excelWriterTableBuilder);

        return excelWriterTableBuilder.build();
    }

    /**
     * 根据ExcelSheet配置创建并返回EasyExcel的WriteSheet对象
     *
     * @param excelSheet Excel表格配置对象，包含sheet编号和名称等信息
     * @return WriteSheet 构建完成的EasyExcel写入表格对象
     */
    private WriteSheet getWriteSheet(ExcelSheet excelSheet) {
        // 初始化EasyExcel的Sheet构建器，设置基础属性
        ExcelWriterSheetBuilder excelWriterSheetBuilder = EasyExcel.writerSheet(excelSheet.sheetNo(), excelSheet.sheetName());

        // 通过自定义配置器对Sheet构建器进行个性化配置
        ExcelWriterSheetBuilderCustomizer excelWriterSheetBuilderCustomizer = new ExcelWriterSheetBuilderCustomizer(excelSheet);
        excelWriterSheetBuilderCustomizer.customize(excelWriterSheetBuilder);

        return excelWriterSheetBuilder.build();
    }

    /**
     * 获取ExcelWriter对象用于写入Excel数据
     *
     * @param outputStream  输出流，用于指定Excel文件的输出位置
     * @param excelResponse Excel响应对象，包含Excel的配置信息（如样式、表头等）
     * @return ExcelWriter 实例，用于后续的Excel数据写入操作
     */
    private ExcelWriter getExcelWriter(OutputStream outputStream, ExcelResponse excelResponse) {
        // 创建ExcelWriterBuilder并初始化基础配置
        ExcelWriterBuilder excelWriterBuilder = EasyExcel.write(outputStream);

        // 应用自定义配置（如样式、表头等）
        ExcelWriterBuilderCustomizer excelWriterBuilderCustomizer = new ExcelWriterBuilderCustomizer(excelResponse);
        excelWriterBuilderCustomizer.customize(excelWriterBuilder);

        // 构建并返回ExcelWriter实例
        return excelWriterBuilder.build();
    }

    /**
     * 将ExcelTable数组转换为WriteTable列表
     *
     * @param tables ExcelTable数组，包含需要转换的Excel表格数据
     * @return List<WriteTable> 转换后的WriteTable列表，每个元素对应输入数组中的一个ExcelTable
     */
    private List<WriteTable> getWriteTables(ExcelTable[] tables) {
        // 使用流式处理将每个ExcelTable转换为WriteTable并收集为列表
        return Arrays.stream(tables).map(this::getWriteTable).collect(Collectors.toList());
    }
}
