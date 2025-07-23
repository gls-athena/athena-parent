package com.gls.athena.starter.word.generator;

import cn.hutool.core.util.StrUtil;
import com.gls.athena.starter.word.annotation.WordResponse;
import lombok.extern.slf4j.Slf4j;
import org.docx4j.convert.in.xhtml.XHTMLImporter;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.springframework.stereotype.Component;

import java.io.OutputStream;

/**
 * 默认Word文档生成器（无模板）。
 * <p>
 * 该生成器将数据以表格形式导出到Word文档，适用于无模板的简单导出场景。
 * </p>
 *
 * @author george
 */
@Slf4j
@Component
public class DefaultWordGenerator implements WordGenerator {

    /**
     * 生成Word文档。
     * <p>
     * 本方法将传入的数据转换为HTML，并使用docx4j库将其转换为Word文档。适用于无特定模板的场景。
     * </p>
     *
     * @param data         要写入Word文档的数据，这里假定为HTML格式的字符串。
     * @param wordResponse WordResponse注解实例，用于提供生成Word文档所需的附加信息。
     * @param outputStream Word文档的输出流，用于保存生成的Word文档。
     * @throws Exception 如果文档生成过程中发生错误，则抛出异常。
     */
    @Override
    public void generate(Object data, WordResponse wordResponse, OutputStream outputStream) throws Exception {
        // 将传入的数据转换为HTML字符串
        String html = (String) data;
        // 使用 docx4j 将 HTML 转换为 docx
        WordprocessingMLPackage wordPackage = WordprocessingMLPackage.createPackage();
        MainDocumentPart mainDocumentPart = wordPackage.getMainDocumentPart();
        XHTMLImporter xhtmlImporter = new XHTMLImporterImpl(wordPackage);
        // 将HTML内容转换为Word文档内容
        mainDocumentPart.getContent().addAll(xhtmlImporter.convert(html, null));
        // 将生成的Word文档保存到输出流
        wordPackage.save(outputStream);
    }

    /**
     * 判断是否支持指定的WordResponse。
     * <p>
     * 本方法检查WordResponse中的模板是否为空，并且生成器是否未指定具体的生成器类，
     * 以决定是否支持生成相应的Word文档。
     * </p>
     *
     * @param wordResponse WordResponse注解实例，用于检查是否支持生成对应的Word文档。
     * @return 如果支持生成，则返回true；否则返回false。
     */
    @Override
    public boolean supports(WordResponse wordResponse) {
        // 检查模板是否为空并且生成器是否未指定具体的生成器类
        return StrUtil.isBlank(wordResponse.template())
                && wordResponse.generator() == WordGenerator.class;
    }
}
