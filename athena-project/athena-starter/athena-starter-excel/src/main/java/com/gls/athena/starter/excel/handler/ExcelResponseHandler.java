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
 * <p>
 * 该类实现了Spring MVC的HandlerMethodReturnValueHandler接口，
 * 用于处理标注了@ExcelResponse注解的控制器方法的返回值，
 * 将返回的数据自动导出为Excel文件并通过HTTP响应返回给客户端。
 * <p>
 * 主要功能：
 * 1. 支持基于模板的Excel填充导出
 * 2. 支持动态生成Excel表格导出
 * 3. 支持多工作表导出
 * 4. 支持多表格导出
 * 5. 自动设置HTTP响应头，实现文件下载
 *
 * @author george
 */
@Slf4j
public class ExcelResponseHandler implements HandlerMethodReturnValueHandler {

    /**
     * Excel文件的MIME类型常量
     */
    private static final String EXCEL_CONTENT_TYPE = "application/vnd.ms-excel";
    /**
     * Content-Disposition头的格式化字符串常量
     */
    private static final String CONTENT_DISPOSITION_FORMAT = "attachment;filename=%s";
    /**
     * 最大文件名长度常量
     */
    private static final int MAX_FILENAME_LENGTH = 255;

    /**
     * 判断是否支持处理指定的返回值类型
     *
     * @param returnType 方法返回值类型参数
     * @return 如果方法标注了@ExcelResponse注解则返回true，否则返回false
     */
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.hasMethodAnnotation(ExcelResponse.class);
    }

    /**
     * 处理方法返回值，将数据导出为Excel文件
     *
     * @param returnValue  方法的返回值数据
     * @param returnType   方法返回值类型参数
     * @param mavContainer ModelAndView容器
     * @param webRequest   Web请求对象
     * @throws Exception 导出过程中可能抛出的异常
     */
    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        // 标记请求已处理，阻止Spring MVC继续处理
        mavContainer.setRequestHandled(true);

        // 获取@ExcelResponse注解配置
        ExcelResponse excelResponse = returnType.getMethodAnnotation(ExcelResponse.class);
        if (excelResponse == null) {
            throw new IllegalArgumentException("方法返回值必须使用@ExcelResponse注解标记");
        }

        // 获取输出流并导出Excel数据
        try (OutputStream outputStream = getOutputStream(webRequest, excelResponse.filename(), excelResponse.excelType().getValue())) {
            exportToExcel(returnValue, outputStream, excelResponse);
        } catch (IOException e) {
            log.error("导出Excel文件时发生错误", e);
            throw e;
        }
    }

    /**
     * 获取HTTP响应输出流，并设置相应的响应头
     *
     * @param webRequest Web请求对象
     * @param fileName   导出的文件名
     * @param excelType  Excel文件类型后缀（如.xlsx、.xls）
     * @return HTTP响应输出流
     * @throws IOException 获取输出流时可能抛出的IO异常
     */
    private OutputStream getOutputStream(NativeWebRequest webRequest, String fileName, String excelType) throws IOException {
        // 验证参数有效性
        validateParams(fileName, excelType);

        // 获取HTTP响应对象
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        if (response == null) {
            throw new IllegalArgumentException("无法获取HttpServletResponse");
        }

        // 设置响应头
        setupResponseHeaders(response, fileName, excelType);
        return response.getOutputStream();
    }

    /**
     * 验证文件名和文件类型参数的有效性
     *
     * @param fileName  文件名
     * @param excelType Excel文件类型后缀
     * @throws IllegalArgumentException 参数无效时抛出异常
     */
    private void validateParams(String fileName, String excelType) {
        if (StrUtil.isEmpty(fileName)) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        if (StrUtil.isEmpty(excelType)) {
            throw new IllegalArgumentException("文件类型不能为空");
        }
        if (fileName.length() > MAX_FILENAME_LENGTH - excelType.length()) {
            throw new IllegalArgumentException("文件名过长");
        }
    }

    /**
     * 设置HTTP响应头，配置文件下载相关信息
     *
     * @param response  HTTP响应对象
     * @param fileName  文件名
     * @param excelType Excel文件类型后缀
     */
    private void setupResponseHeaders(HttpServletResponse response, String fileName, String excelType) {
        // 设置内容类型为Excel文件
        response.setContentType(EXCEL_CONTENT_TYPE);
        // 设置字符编码为UTF-8
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // 清理文件名中的非法字符，防止文件名注入攻击
        String sanitizedFileName = fileName.replaceAll("[\\x00-\\x1F\\x7F\"\\\\/:*?<>|]", "_");
        // URL编码文件名，支持中文文件名
        String encodedFileName = URLUtil.encode(sanitizedFileName, StandardCharsets.UTF_8);
        // 拼接完整的文件名（包含扩展名）
        String fullFileName = encodedFileName + excelType;

        // 设置Content-Disposition头，指示浏览器下载文件
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format(CONTENT_DISPOSITION_FORMAT, fullFileName));
        // 设置CORS相关头，允许前端获取Content-Disposition头
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
    }

    /**
     * 将数据导出为Excel文件
     *
     * @param data          要导出的数据
     * @param outputStream  输出流
     * @param excelResponse Excel响应配置
     */
    private void exportToExcel(Object data, OutputStream outputStream, ExcelResponse excelResponse) {
        try (ExcelWriter excelWriter = WriteWorkbookCustomizer.getExcelWriter(excelResponse, outputStream)) {
            // 根据是否有模板选择不同的导出方式
            if (StrUtil.isEmpty(excelResponse.template())) {
                // 无模板：动态生成Excel表格
                writeToExcel(convertToList(data), excelWriter, excelResponse);
            } else {
                // 有模板：基于模板填充数据
                fillToExcel(data, excelWriter, excelResponse);
            }
        } catch (Exception e) {
            log.error("Excel导出失败: {}", e.getMessage(), e);
            throw new RuntimeException("Excel导出失败", e);
        }
    }

    /**
     * 基于模板填充数据到Excel
     *
     * @param data          要填充的数据
     * @param excelWriter   Excel写入器
     * @param excelResponse Excel响应配置
     */
    private void fillToExcel(Object data, ExcelWriter excelWriter, ExcelResponse excelResponse) {
        ExcelSheet[] sheets = excelResponse.sheets();
        validateSheets(sheets);

        // 单个工作表处理
        if (sheets.length == 1) {
            fillToSheet(data, excelWriter, sheets[0]);
            return;
        }

        // 多个工作表处理：将数据转换为列表，按索引分配给不同工作表
        List<?> dataList = convertToList(data);
        for (ExcelSheet sheet : sheets) {
            validateSheetNo(sheet.sheetNo(), dataList.size());
            fillToSheet(dataList.get(sheet.sheetNo()), excelWriter, sheet);
        }
    }

    /**
     * 向指定工作表填充数据
     *
     * @param data        要填充的数据
     * @param excelWriter Excel写入器
     * @param excelSheet  工作表配置
     */
    private void fillToSheet(Object data, ExcelWriter excelWriter, ExcelSheet excelSheet) {
        WriteSheet writeSheet = WriteSheetCustomizer.getWriteSheet(excelSheet);
        // 配置填充选项：强制换行
        FillConfig fillConfig = FillConfig.builder().forceNewRow(true).build();

        // 如果数据是集合类型，直接填充
        if (data instanceof Collection) {
            excelWriter.fill(data, fillConfig, writeSheet);
            return;
        }

        // 如果数据是对象，转换为Map进行填充
        Map<String, Object> dataMap = BeanUtil.beanToMap(data);
        Map<String, Object> fillMap = new HashMap<>();

        // 遍历数据Map，区分集合属性和普通属性
        dataMap.forEach((key, value) -> {
            if (value instanceof Collection<?> collection) {
                // 集合属性使用FillWrapper包装后填充
                excelWriter.fill(new FillWrapper(key, collection), fillConfig, writeSheet);
            } else {
                // 普通属性放入fillMap中
                fillMap.put(key, value);
            }
        });

        // 填充普通属性
        if (!fillMap.isEmpty()) {
            excelWriter.fill(fillMap, fillConfig, writeSheet);
        }
    }

    /**
     * 动态生成Excel表格（无模板方式）
     *
     * @param data          要写入的数据列表
     * @param excelWriter   Excel写入器
     * @param excelResponse Excel响应配置
     */
    private void writeToExcel(List<?> data, ExcelWriter excelWriter, ExcelResponse excelResponse) {
        if (CollUtil.isEmpty(data)) {
            throw new IllegalArgumentException("数据列表不能为空");
        }

        ExcelSheet[] sheets = excelResponse.sheets();
        validateSheets(sheets);

        // 单个工作表处理
        if (sheets.length == 1) {
            writeToSheet(data, excelWriter, sheets[0]);
            return;
        }

        // 多个工作表处理：按工作表索引分配数据
        for (ExcelSheet sheet : sheets) {
            validateSheetNo(sheet.sheetNo(), data.size());
            List<?> sheetData = convertToList(data.get(sheet.sheetNo()));
            writeToSheet(sheetData, excelWriter, sheet);
        }
    }

    /**
     * 向指定工作表写入数据
     *
     * @param data        要写入的数据列表
     * @param excelWriter Excel写入器
     * @param excelSheet  工作表配置
     */
    private void writeToSheet(List<?> data, ExcelWriter excelWriter, ExcelSheet excelSheet) {
        WriteSheet writeSheet = WriteSheetCustomizer.getWriteSheet(excelSheet);
        List<WriteTable> writeTables = WriteTableCustomizer.getWriteTables(excelSheet.tables());

        // 无表格配置：直接写入工作表
        if (writeTables.isEmpty()) {
            writeToTable(data, excelWriter, writeSheet, null);
            return;
        }

        // 单个表格：直接写入
        if (writeTables.size() == 1) {
            writeToTable(data, excelWriter, writeSheet, writeTables.getFirst());
            return;
        }

        // 多个表格：按表格索引分配数据
        for (WriteTable writeTable : writeTables) {
            int tableNo = writeTable.getTableNo();
            validateTableNo(tableNo, data.size());
            List<?> tableData = convertToList(data.get(tableNo));
            writeToTable(tableData, excelWriter, writeSheet, writeTable);
        }
    }

    /**
     * 向指定表格写入数据
     *
     * @param data        要写入的数据列表
     * @param excelWriter Excel写入器
     * @param writeSheet  工作表对象
     * @param writeTable  表格对象（可为null）
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
            // 有表格配置：设置数据类型并写入表格
            writeTable.setClazz(clazz);
            excelWriter.write(data, writeSheet, writeTable);
        } else {
            // 无表格配置：设置数据类型并写入工作表
            writeSheet.setClazz(clazz);
            excelWriter.write(data, writeSheet);
        }
    }

    /**
     * 验证工作表配置是否有效
     *
     * @param sheets 工作表配置数组
     * @throws IllegalArgumentException 配置无效时抛出异常
     */
    private void validateSheets(ExcelSheet[] sheets) {
        if (sheets == null || sheets.length == 0) {
            throw new IllegalArgumentException("Excel工作表配置不能为空");
        }
    }

    /**
     * 验证工作表索引是否在有效范围内
     *
     * @param sheetNo  工作表索引
     * @param dataSize 数据大小
     * @throws IllegalArgumentException 索引超出范围时抛出异常
     */
    private void validateSheetNo(int sheetNo, int dataSize) {
        if (sheetNo < 0 || sheetNo >= dataSize) {
            throw new IllegalArgumentException("sheetNo " + sheetNo + "超出数据范围(0-" + (dataSize - 1) + ")");
        }
    }

    /**
     * 验证表格索引是否在有效范围内
     *
     * @param tableNo  表格索引
     * @param dataSize 数据大小
     * @throws IllegalArgumentException 索引超出范围时抛出异常
     */
    private void validateTableNo(int tableNo, int dataSize) {
        if (tableNo < 0 || tableNo >= dataSize) {
            throw new IllegalArgumentException("tableNo " + tableNo + "超出数据范围(0-" + (dataSize - 1) + ")");
        }
    }

    /**
     * 将对象转换为列表
     * 如果对象本身就是List，直接返回；
     * 如果是Collection，转换为List；
     * 否则将单个对象包装成单元素列表
     *
     * @param data 要转换的数据对象
     * @return 转换后的列表
     */
    private List<?> convertToList(Object data) {
        if (data instanceof List<?> list) {
            return list;
        }
        if (data instanceof Collection<?> collection) {
            return collection.stream().toList();
        }
        return List.of(data);
    }

}
