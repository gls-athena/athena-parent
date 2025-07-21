package com.gls.athena.starter.excel.generator;

import com.gls.athena.starter.excel.annotation.ExcelResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.util.List;

/**
 * Excel生成管理器类，负责根据不同的Excel响应类型委托给相应的Excel生成器
 *
 * @author george
 */
@Component
@RequiredArgsConstructor
public class ExcelGeneratorManager {

    /**
     * 一个包含多个Excel生成器的列表，用于处理不同类型的Excel生成任务
     */
    private final List<ExcelGenerator> generators;

    /**
     * 根据提供的Excel响应对象，选择合适的Excel生成器来生成Excel文件
     *
     * @param data          要写入Excel的数据对象
     * @param excelResponse 描述Excel响应的注解对象，用于选择合适的生成器
     * @param outputStream  用于输出生成的Excel文件的输出流
     * @throws Exception 如果生成过程中发生错误或找不到适配的生成器，将抛出异常
     */
    public void generate(Object data, ExcelResponse excelResponse, OutputStream outputStream) throws Exception {
        // 通过流式处理找到第一个支持当前excelResponse的生成器
        generators.stream()
                .filter(generator -> generator.supports(excelResponse))
                .findFirst()
                // 如果找不到适配的生成器，抛出非法参数异常
                .orElseThrow(() -> new IllegalArgumentException("未找到适配的excelResponse实现"))
                // 使用找到的生成器生成Excel
                .generate(data, excelResponse, outputStream);
    }
}
