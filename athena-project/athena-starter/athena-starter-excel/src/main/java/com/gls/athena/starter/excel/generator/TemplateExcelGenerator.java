package com.gls.athena.starter.excel.generator;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.idev.excel.ExcelWriter;
import cn.idev.excel.write.metadata.WriteSheet;
import cn.idev.excel.write.metadata.fill.FillConfig;
import cn.idev.excel.write.metadata.fill.FillWrapper;
import com.gls.athena.starter.excel.annotation.ExcelResponse;
import com.gls.athena.starter.excel.annotation.ExcelSheet;
import com.gls.athena.starter.excel.customizer.WriteSheetCustomizer;
import com.gls.athena.starter.excel.customizer.WriteWorkbookCustomizer;
import com.gls.athena.starter.excel.support.ExcelDataUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 实现Excel生成器接口，使用模板方式生成Excel
 *
 * @author george
 */
@Slf4j
@Component
public class TemplateExcelGenerator implements ExcelGenerator {

    /**
     * 默认的填充配置，强制插入新行
     */
    private static final FillConfig DEFAULT_FILL_CONFIG = FillConfig.builder().forceNewRow(true).build();

    /**
     * 根据提供的数据和导出响应配置，生成Excel并输出到指定流
     *
     * @param data          用于填充Excel模板的数据
     * @param excelResponse 描述Excel导出的响应对象，包含导出配置
     * @param outputStream  Excel数据的输出流
     * @throws Exception 如果Excel生成过程中发生错误
     */
    @Override
    public void generate(Object data, ExcelResponse excelResponse, OutputStream outputStream) throws Exception {
        try (ExcelWriter excelWriter = WriteWorkbookCustomizer.getExcelWriter(excelResponse, outputStream)) {
            // 获取并验证工作表配置
            List<ExcelSheet> sheets = ExcelDataUtil.getValidatedSheets(excelResponse);

            // 使用模板填充方式
            fillTemplateExcel(data, excelWriter, sheets);

        } catch (Exception e) {
            log.error("Excel导出失败: {}", e.getMessage(), e);
            throw new RuntimeException("Excel导出失败", e);
        }
    }

    /**
     * 填充Excel模板
     *
     * @param data        模板填充数据
     * @param excelWriter ExcelWriter对象，用于写入Excel
     * @param sheets      工作表配置列表
     */
    private void fillTemplateExcel(Object data, ExcelWriter excelWriter, List<ExcelSheet> sheets) {
        for (ExcelSheet sheet : sheets) {
            // 根据工作表索引获取对应的数据
            Object sheetData = ExcelDataUtil.getDataAtIndex(data, sheets, sheet.sheetNo());
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
     * 判断当前生成器是否支持指定的Excel导出响应配置
     *
     * @param excelResponse 描述Excel导出的响应对象，包含导出配置
     * @return 如果当前生成器支持指定的响应配置，则返回true，否则返回false
     */
    @Override
    public boolean supports(ExcelResponse excelResponse) {
        return StrUtil.isNotEmpty(excelResponse.template()) && excelResponse.generator() == ExcelGenerator.class;
    }
}
