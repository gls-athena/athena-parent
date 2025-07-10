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
import com.gls.athena.starter.excel.customizer.ExcelWriterCustomizer;
import com.gls.athena.starter.excel.customizer.WriteSheetCustomizer;
import com.gls.athena.starter.excel.customizer.WriteTableCustomizer;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel响应处理器
 * 处理带有@ExcelResponse注解的方法返回值，将数据写入Excel文件并返回给客户端。
 *
 * @author george
 */
@Slf4j
public class ExcelResponseHandler implements HandlerMethodReturnValueHandler {

    private static final String EXCEL_CONTENT_TYPE = "application/vnd.ms-excel";
    private static final String CONTENT_DISPOSITION_FORMAT = "attachment;filename=%s";
    private static final int MAX_FILENAME_LENGTH = 255;

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
        // 标记请求已处理
        mavContainer.setRequestHandled(true);
        // 获取Excel响应的配置信息
        ExcelResponse excelResponse = returnType.getMethodAnnotation(ExcelResponse.class);
        if (excelResponse == null) {
            throw new IllegalArgumentException("方法返回值必须使用@ExcelResponse注解标记");
        }

        // 创建Excel输出流并写入数据
        try (OutputStream outputStream = getOutputStream(webRequest, excelResponse.filename(), excelResponse.excelType().getValue())) {
            exportToExcel(returnValue, outputStream, excelResponse);
        } catch (IOException e) {
            log.error("导出Excel文件时发生错误", e);
            throw e;
        }
    }

    /**
     * 获取用于Excel文件下载的输出流
     *
     * @param webRequest NativeWebRequest对象，用于获取HttpServletResponse
     * @param fileName   要下载的文件名（不含扩展名）
     * @param excelType  Excel文件扩展名（如".xlsx"）
     * @return OutputStream 用于写入Excel文件数据的输出流
     * @throws IOException              如果获取输出流失败
     * @throws IllegalArgumentException 如果参数无效或无法获取HttpServletResponse
     */
    private OutputStream getOutputStream(NativeWebRequest webRequest, String fileName, String excelType) throws IOException {
        // 参数校验
        if (StrUtil.isEmpty(fileName)) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        if (StrUtil.isEmpty(excelType)) {
            throw new IllegalArgumentException("文件类型不能为空");
        }
        if (fileName.length() > MAX_FILENAME_LENGTH - excelType.length()) {
            throw new IllegalArgumentException("文件名过长");
        }

        // 获取并验证响应对象
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        if (response == null) {
            throw new IllegalArgumentException("无法获取HttpServletResponse");
        }

        // 设置响应头
        response.setContentType(EXCEL_CONTENT_TYPE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // 安全编码文件名
        String sanitizedFileName = fileName.replaceAll("[\\x00-\\x1F\\x7F\"\\\\/:*?<>|]", "_");
        String encodedFileName = URLUtil.encode(sanitizedFileName, StandardCharsets.UTF_8);
        String fullFileName = encodedFileName + excelType;

        // 设置内容处置和跨域头
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format(CONTENT_DISPOSITION_FORMAT, fullFileName));
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
        return response.getOutputStream();
    }

    /**
     * 将数据导出到Excel文件
     *
     * @param data          要导出的数据对象，非空
     * @param outputStream  输出流，用于写入Excel文件，非空
     * @param excelResponse Excel导出配置对象，包含导出模板、样式等信息，非空
     * @throws RuntimeException 当导出过程中发生异常时抛出
     */
    private void exportToExcel(Object data, OutputStream outputStream, ExcelResponse excelResponse) {
        // 使用try-with-resources确保ExcelWriter正确关闭
        try (ExcelWriter excelWriter = ExcelWriterCustomizer.build(outputStream, excelResponse)) {
            // 根据是否使用模板选择不同的导出方式
            if (StrUtil.isEmpty(excelResponse.template())) {
                // 无模板情况下的导出逻辑
                writeToExcel(convertToList(data), excelWriter, excelResponse);
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
     * @throws IllegalArgumentException 如果参数不合法或数据与sheet不匹配
     */
    private void fillToExcel(Object data, ExcelWriter excelWriter, ExcelResponse excelResponse) {
        // 检查sheet配置是否存在
        ExcelSheet[] excelSheets = excelResponse.sheets();
        if (excelSheets == null || excelSheets.length == 0) {
            throw new IllegalArgumentException("ExcelResponse中必须包含至少一个sheet配置");
        }

        // 单sheet处理：直接使用原始数据填充
        if (excelSheets.length == 1) {
            fillToSheet(data, excelWriter, excelSheets[0]);
            return;
        }

        // 多sheet处理：将数据按sheet顺序分配
        List<?> dataList = convertToList(data);
        for (ExcelSheet excelSheet : excelSheets) {
            int sheetNo = excelSheet.sheetNo();
            if (sheetNo < 0 || sheetNo >= dataList.size()) {
                throw new IllegalArgumentException("sheetNo " + sheetNo + "超出数据范围(0-" + (dataList.size() - 1) + ")");
            }
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
    private void fillToSheet(Object data, ExcelWriter excelWriter, ExcelSheet excelSheet) {
        // 构建可写入的工作表对象
        WriteSheet writeSheet = WriteSheetCustomizer.build(excelSheet);
        FillConfig fillConfig = FillConfig.builder().forceNewRow(true).build();

        // 处理集合类型数据
        if (data instanceof Collection) {
            excelWriter.fill(data, fillConfig, writeSheet);
            return;
        }

        // 将Bean对象转换为Map结构
        Map<String, Object> dataMap = BeanUtil.beanToMap(data);
        Map<String, Object> fillMap = new HashMap<>(dataMap.size());

        // 遍历Map处理每个字段
        dataMap.forEach((key, value) -> {
            if (value instanceof Collection<?> column) {
                // 集合类型字段单独填充（如多行数据）
                excelWriter.fill(new FillWrapper(key, column), fillConfig, writeSheet);
            } else {
                // 非集合类型字段暂存
                fillMap.put(key, value);
            }
        });

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
     * @throws IllegalArgumentException  如果参数不合法
     * @throws IndexOutOfBoundsException 如果sheetNo超出数据范围
     */
    private void writeToExcel(List<?> data, ExcelWriter excelWriter, ExcelResponse excelResponse) {
        // 参数校验
        if (CollUtil.isEmpty(data)) {
            throw new IllegalArgumentException("数据列表不能为空");
        }

        ExcelSheet[] excelSheets = excelResponse.sheets();
        if (excelSheets == null || excelSheets.length == 0) {
            throw new IllegalArgumentException("Excel工作表配置不能为空");
        }

        // 如果只有一个工作表，直接将所有数据写入该工作表
        if (excelSheets.length == 1) {
            writeToSheet(data, excelWriter, excelSheets[0]);
            return;
        }

        // 多个工作表时，根据sheetNo从数据中获取对应分页数据并写入
        for (ExcelSheet excelSheet : excelSheets) {
            int sheetNo = excelSheet.sheetNo();
            if (sheetNo < 0 || sheetNo >= data.size()) {
                throw new IndexOutOfBoundsException("Sheet number " + sheetNo + " is out of data bounds");
            }
            List<?> sheetData = convertToList(data.get(sheetNo));
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
    private void writeToSheet(List<?> data, ExcelWriter excelWriter, ExcelSheet excelSheet) {
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
            // 添加边界检查
            if (tableNo < 0 || tableNo >= data.size()) {
                log.warn("表格编号超出范围，将忽略该表格：{}", tableNo);
                throw new IllegalArgumentException("表格编号超出数据范围: " + tableNo);
            }
            List<?> tableData = convertToList(data.get(tableNo));
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
     * @throws IllegalArgumentException 如果数据列表为空或元素类型不一致
     */
    private void writeToTable(List<?> data, ExcelWriter excelWriter, WriteSheet writeSheet, WriteTable writeTable) {
        // 检查数据列表是否为空
        if (data.isEmpty()) {
            throw new IllegalArgumentException("数据列表不能为空");
        }

        // 获取列表第一个元素的Class类型作为写入模板
        Class<?> clazz = data.getFirst().getClass();

        // 验证所有元素类型是否一致
        for (Object item : data) {
            if (item == null || !clazz.equals(item.getClass())) {
                throw new IllegalArgumentException("数据列表元素类型不一致或包含null元素");
            }
        }

        // 根据writeTable是否存在决定不同的写入策略
        if (writeTable != null) {
            writeTable.setClazz(clazz);
            excelWriter.write(data, writeSheet, writeTable);
        } else {
            writeSheet.setClazz(clazz);
            excelWriter.write(data, writeSheet);
        }
    }

    /**
     * 将输入对象转换为List类型
     *
     * @param value 需要转换的对象，不能为null且必须为List类型
     * @return 转换后的List对象
     * @throws NullPointerException     当输入对象为null时抛出
     * @throws IllegalArgumentException 当输入对象不是List类型，或List为空时抛出
     */
    private List<?> convertToList(Object value) {
        // 检查输入对象是否为null
        if (value == null) {
            throw new NullPointerException("数据不能为空");
        }

        // 检查并处理List类型输入
        if (value instanceof List<?> list) {
            // 检查List是否为空
            if (CollUtil.isEmpty(list)) {
                throw new IllegalArgumentException("数据列表不能为空");
            }
            return list;
        }

        // 非List类型输入处理
        throw new IllegalArgumentException("数据类型错误");
    }
}
