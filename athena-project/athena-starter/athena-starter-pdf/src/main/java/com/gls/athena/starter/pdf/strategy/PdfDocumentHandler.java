package com.gls.athena.starter.pdf.strategy;

import cn.hutool.core.util.StrUtil;
import com.gls.athena.starter.pdf.annotation.PdfResponse;
import com.gls.athena.starter.pdf.config.PdfTemplateType;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * PDF文档模板处理策略
 *
 * @author george
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PdfDocumentHandler implements ITemplateHandler {

    @Override
    public void handle(Map<String, Object> data, OutputStream outputStream, PdfResponse pdfResponse) throws IOException {
        // 加载模板
        InputStream template = new ClassPathResource(pdfResponse.template()).getInputStream();
        log.debug("加载PDF模板: {}", pdfResponse.template());
        // 填充数据到PDF模板
        fillPdfTemplate(template, data, outputStream);
    }

    @Override
    public boolean supports(PdfTemplateType templateType) {
        return PdfTemplateType.PDF.equals(templateType);
    }

    /**
     * 填充PDF模板表单字段并输出结果
     *
     * @param inputStream  包含PDF模板的输入流，必须是一个可读的PDF文件
     * @param data         包含字段名和对应值的映射，将用于填充PDF表单字段
     * @param outputStream 用于输出填充后PDF文档的输出流
     * @throws IOException 如果读取输入流或写入输出流时发生I/O错误
     */
    private void fillPdfTemplate(InputStream inputStream, Map<String, Object> data,
                                 OutputStream outputStream) throws IOException {
        // 初始化PDF文档处理器
        PdfReader reader = new PdfReader(inputStream);
        PdfStamper stamper = new PdfStamper(reader, outputStream);
        // 获取PDF表单字段并填充数据
        AcroFields fields = stamper.getAcroFields();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            String value = StrUtil.toString(entry.getValue());
            try {
                fields.setField(key, value);
            } catch (IOException e) {
                log.warn("填充字段失败: {}", key, e);
            }
        }
        // 扁平化表单字段并关闭资源
        stamper.setFormFlattening(true);
        stamper.close();
        reader.close();
    }
}
