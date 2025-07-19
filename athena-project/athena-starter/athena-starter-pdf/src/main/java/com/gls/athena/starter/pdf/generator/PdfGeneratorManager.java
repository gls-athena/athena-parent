package com.gls.athena.starter.pdf.generator;

import com.gls.athena.starter.pdf.annotation.PdfResponse;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.util.List;

/**
 * 简化的PDF生成器管理服务
 *
 * @author athena
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PdfGeneratorManager {

    /**
     * 所有可用的PDF生成器实现，由Spring自动注入。
     * 这里使用@Resource注解，确保可以通过类型自动装配。
     */
    @Resource
    private List<PdfGenerator> pdfGenerators;

    /**
     * 根据@PdfResponse注解选择合适的PDF生成器并生成PDF文档。
     *
     * @param returnValue  控制器返回的数据对象
     * @param pdfResponse  PDF导出注解信息
     * @param outputStream PDF文档输出流
     * @throws Exception 生成PDF文档时发生的异常
     */
    public void generate(Object returnValue, PdfResponse pdfResponse, OutputStream outputStream) throws Exception {
        pdfGenerators.stream()
                .filter(pdfGenerator -> pdfGenerator.supports(pdfResponse))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("未找到适配的PDF生成器实现"))
                .generate(returnValue, pdfResponse, outputStream);
    }
}
