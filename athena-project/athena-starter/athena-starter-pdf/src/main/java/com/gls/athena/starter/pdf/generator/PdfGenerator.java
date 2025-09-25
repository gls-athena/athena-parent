package com.gls.athena.starter.pdf.generator;

import com.gls.athena.starter.file.generator.FileGenerator;
import com.gls.athena.starter.pdf.annotation.PdfResponse;

/**
 * PDF文档生成器接口
 *
 * @author george
 */
public interface PdfGenerator extends FileGenerator<PdfResponse> {

    /**
     * 判断是否支持当前注解配置。
     * <p>
     * 用于适配不同的生成器实现（如模板导出、无模板导出等）。
     * </p>
     *
     * @param pdfResponse PDF导出注解信息
     * @return 是否支持该配置
     */
    @Override
    default boolean supports(PdfResponse pdfResponse) {
        return pdfResponse.generator().equals(this.getClass());
    }
}
