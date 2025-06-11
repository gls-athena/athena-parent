package com.gls.athena.starter.excel.support;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.URLUtil;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteTable;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.excel.write.metadata.fill.FillWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.NativeWebRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
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
    /**
     * Excel文件内容类型
     */
    private static final String EXCEL_CONTENT_TYPE = "application/vnd.ms-excel";

    /**
     * 将表格数据写入Excel文件。
     * <p>
     * 该方法根据提供的表格信息列表和数据列表，将数据写入指定的Excel工作表。
     * 处理逻辑如下：
     * 1. 如果数据列表为空，则跳过写入操作；
     * 2. 如果表格信息列表为空，则将所有数据作为一个表格写入；
     * 3. 如果只有一个表格信息，则将所有数据写入该表格；
     * 4. 如果有多个表格信息，则根据每个表格的编号从数据列表中获取对应数据并分别写入。
     * </p>
     *
     * @param excelWriter    Excel写入工具实例，用于执行实际的写入操作
     * @param writeSheet     目标工作表对象，指定数据写入的位置和样式
     * @param writeTableList 表格信息列表，包含每个表格的编号、样式等配置信息
     * @param sheetData      待写入的数据列表，每个元素可能对应一个子表格的数据
     */
    public void writeSheetData(ExcelWriter excelWriter, WriteSheet writeSheet, List<WriteTable> writeTableList, List<?> sheetData) {
        // 检查数据列表是否为空
        if (CollUtil.isEmpty(sheetData)) {
            log.warn("写入Excel数据时，数据列表为空，跳过写入操作");
            return;
        }

        // 处理无表格信息或单个表格信息的情况
        if (CollUtil.isEmpty(writeTableList)) {
            writeTableData(excelWriter, writeSheet, null, sheetData);
            return;
        }
        if (writeTableList.size() == 1) {
            writeTableData(excelWriter, writeSheet, writeTableList.getFirst(), sheetData);
            return;
        }

        // 处理多个表格信息的情况：按表格编号分别写入对应数据
        for (WriteTable writeTable : writeTableList) {
            List<?> tableData = Convert.toList(sheetData.get(writeTable.getTableNo()));
            writeTableData(excelWriter, writeSheet, writeTable, tableData);
        }
    }

    /**
     * 将数据列表写入Excel表格
     *
     * @param excelWriter Excel写入工具实例，用于执行实际的写入操作
     * @param writeSheet  工作表配置对象，包含工作表级别的设置
     * @param writeTable  表格配置对象（可选），包含表格级别的特殊设置，若为null则使用工作表配置
     * @param data        要写入的数据列表，列表元素类型决定了Excel的字段映射
     * @throws NullPointerException 如果data列表的第一个元素为null时抛出（通过日志警告而非异常）
     */
    public void writeTableData(ExcelWriter excelWriter, WriteSheet writeSheet, WriteTable writeTable, List<?> data) {
        // 空数据检查：跳过空列表或首元素为null的情况
        if (CollUtil.isEmpty(data)) {
            log.warn("写入Excel数据时，数据列表为空，跳过写入操作");
            return;
        }
        if (data.getFirst() == null) {
            log.warn("写入Excel数据时，数据列表的第一个元素为null，跳过写入操作");
            return;
        }

        // 根据数据对象的类型动态设置写入配置
        Class<?> clazz = data.getFirst().getClass();
        if (writeTable != null) {
            // 优先使用表格级配置写入
            writeTable.setClazz(clazz);
            excelWriter.write(data, writeSheet, writeTable);
        } else {
            // 无表格配置时使用工作表级配置写入
            writeSheet.setClazz(clazz);
            excelWriter.write(data, writeSheet);
        }
    }

    /**
     * 获取用于Excel文件下载的输出流
     *
     * @param webRequest NativeWebRequest对象，用于获取HttpServletResponse
     * @param fileName   下载文件的名称(不含扩展名)
     * @param excelType  Excel文件扩展名(如.xlsx)
     * @return OutputStream 响应输出流，用于写入Excel数据
     * @throws IOException              如果获取输出流失败
     * @throws IllegalArgumentException 如果参数校验失败或HttpServletResponse为空
     */
    public OutputStream getOutputStream(NativeWebRequest webRequest, String fileName, String excelType) throws IOException {
        // 参数校验
        if (fileName == null || excelType == null) {
            throw new IllegalArgumentException("文件名或文件类型不能为空");
        }

        // 获取并验证HttpServletResponse对象
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        if (response == null) {
            throw new IllegalArgumentException("HttpServletResponse为空");
        }

        // 设置响应头：内容类型、编码、文件名和跨域头
        response.setContentType(EXCEL_CONTENT_TYPE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String encodedFileName = URLUtil.encode(fileName, StandardCharsets.UTF_8);
        String name = encodedFileName + excelType;
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + name);
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

        return response.getOutputStream();
    }

    /**
     * 填充Excel工作表数据
     * <p>
     * 该方法根据传入的数据对象类型，采用不同的方式将数据填充到Excel工作表中：
     * 1. 如果数据为集合类型，直接填充整个集合
     * 2. 如果数据为普通JavaBean对象，将其转换为Map后分别处理：
     * - 集合类型的属性使用FillWrapper单独填充
     * - 其他属性统一填充
     *
     * @param excelWriter Excel写入工具对象，用于执行实际的填充操作
     * @param writeSheet  要填充的Excel工作表对象
     * @param sheetData   要填充的数据，可以是集合或JavaBean对象
     */
    public void fillSheetData(ExcelWriter excelWriter, WriteSheet writeSheet, Object sheetData) {
        // 空数据检查
        if (sheetData == null) {
            log.warn("填充Excel数据时，数据为空，跳过填充操作");
            return;
        }

        // 处理集合类型数据
        if (sheetData instanceof Collection) {
            excelWriter.fill(sheetData, writeSheet);
            return;
        }

        // 处理JavaBean对象
        Map<String, Object> dataMap = BeanUtil.beanToMap(sheetData);
        // 创建填充数据Map
        Map<String, Object> fillMap = new HashMap<>();
        // 创建填充配置
        FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
        // 遍历Bean属性，区分集合类型和普通属性
        for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Collection<?> collection) {
                // 集合类型属性单独填充
                FillWrapper fillWrapper = new FillWrapper(key, collection);
                excelWriter.fill(fillWrapper, fillConfig, writeSheet);
            } else {
                // 普通属性暂存到fillMap
                fillMap.put(key, value);
            }
        }

        // 填充剩余的普通属性
        if (CollUtil.isNotEmpty(fillMap)) {
            excelWriter.fill(fillMap, fillConfig, writeSheet);
        }
    }
}
