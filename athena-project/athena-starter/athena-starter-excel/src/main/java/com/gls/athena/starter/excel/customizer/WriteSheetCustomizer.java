package com.gls.athena.starter.excel.customizer;

import cn.idev.excel.write.builder.ExcelWriterSheetBuilder;
import cn.idev.excel.write.metadata.WriteSheet;
import com.gls.athena.starter.excel.annotation.ExcelSheet;

/**
 * Excel写入Sheet构建器自定义器
 * 用于自定义配置Excel写入时的Sheet相关参数
 *
 * @author george
 * @since 1.0.0
 */
public class WriteSheetCustomizer extends BaseWriterCustomizer<ExcelWriterSheetBuilder> {

    /**
     * Sheet配置注解
     * 包含sheet相关的配置信息，如sheet编号、名称等
     */
    private final ExcelSheet sheet;

    /**
     * 构造Excel Sheet构建器自定义器
     *
     * @param sheet Sheet配置注解，不能为null
     */
    private WriteSheetCustomizer(ExcelSheet sheet) {
        super(sheet.config());
        this.sheet = sheet;
    }

    /**
     * 构建并配置WriteSheet对象
     * <p>
     * 该方法根据给定的ExcelSheet配置，通过自定义器对ExcelWriterSheetBuilder进行配置，
     * 最终构建并返回一个配置完成的WriteSheet实例。
     *
     * @param excelSheet 包含工作表配置信息的ExcelSheet对象
     * @return 配置完成的WriteSheet实例
     */
    public static WriteSheet build(ExcelSheet excelSheet) {
        // 初始化Excel的Sheet构建器，设置基础属性
        ExcelWriterSheetBuilder builder = new ExcelWriterSheetBuilder();

        // 通过自定义配置器对Sheet构建器进行个性化配置
        WriteSheetCustomizer customizer = new WriteSheetCustomizer(excelSheet);
        customizer.customize(builder);
        return builder.build();
    }

    /**
     * 自定义配置Sheet构建器
     * 该方法用于设置Excel工作表的编号和名称等参数。
     * 首先调用父类的customize方法进行基础配置，然后通过传入的Sheet构建器实例设置工作表的编号和名称。
     *
     * @param builder ExcelWriterSheetBuilder实例，用于构建和配置Excel工作表
     */
    @Override
    public void configure(ExcelWriterSheetBuilder builder) {
        // 设置工作表的编号和名称
        builder.sheetNo(sheet.sheetNo())
                .sheetName(sheet.sheetName());
    }

}
