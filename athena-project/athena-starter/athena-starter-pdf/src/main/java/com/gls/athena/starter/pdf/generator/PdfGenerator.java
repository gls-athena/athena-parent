package com.gls.athena.starter.pdf.generator;

import com.gls.athena.starter.pdf.annotation.PdfResponse;

import java.io.OutputStream;

/**
 * PDF文档生成器接口
 *
 * @author george
 */
public interface PdfGenerator {

    /**
     * 生成PDF文档。
     * <p>
     * 根据注解配置和数据对象，将内容导出为PDF文档并写入输出流。
     * </p>
     *
     * @param data         需要导出的数据对象
     * @param pdfResponse  PDF导出注解信息（如模板路径、文件名等）
     * @param outputStream PDF文档输出流
     * @throws Exception 生成或导出过程中发生的异常
     */
    void generate(Object data, PdfResponse pdfResponse, OutputStream outputStream) throws Exception;

    /**
     * 判断是否支持当前注解配置。
     * <p>
     * 用于适配不同的生成器实现（如模板导出、无模板导出等）。
     * </p>
     *
     * @param pdfResponse PDF导出注解信息
     * @return 是否支持该配置
     */
    default boolean supports(PdfResponse pdfResponse) {
        return pdfResponse.generator().equals(this.getClass());
    }
}
