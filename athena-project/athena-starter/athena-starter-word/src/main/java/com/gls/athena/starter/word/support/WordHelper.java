package com.gls.athena.starter.word.support;

import cn.hutool.extra.template.TemplateUtil;
import com.gls.athena.starter.word.annotation.WordResponse;
import com.gls.athena.starter.word.config.WordProperties;
import com.gls.athena.starter.word.converter.HtmlTagConverterManager;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.util.Map;

/**
 * word处理帮助类
 *
 * @author lizy19
 */
@Slf4j
@Component
public class WordHelper {

    @Resource
    private WordProperties wordProperties;

    @Resource
    private HtmlTagConverterManager converterManager;

    /**
     * 处理docx模板并替换变量
     * <p>
     * 该方法接收一个数据映射、一个输出流和一个Word响应对象，用于替换模板中的变量
     * 它首先从模板路径加载文档，然后在文档的各个部分（段落、表格、页眉、页脚）中替换变量，
     * 最后将处理后的文档写入输出流
     *
     * @param data         包含变量及其对应值的映射，用于替换模板中的占位符
     * @param outputStream 用于输出处理后文档的流（调用方负责关闭）
     * @param wordResponse 包含模板路径的响应对象
     */
    public void handleDocxTemplate(Map<String, Object> data, OutputStream outputStream, WordResponse wordResponse) {
    }

    /**
     * 处理HTML模板并转换为DOCX
     * <p>
     * 该方法使用给定的数据填充HTML模板，并将生成的HTML内容转换为DOCX格式的文件
     *
     * @param data         包含模板所需数据的映射，键是模板中的占位符，值是替换占位符的实际数据
     * @param outputStream 输出流，用于��收转换后的DOCX文件
     * @param wordResponse 包含模板信息的响应对象��用��获��模板内容和���信息
     */
    public void handleHtmlTemplate(Map<String, Object> data, OutputStream outputStream, WordResponse wordResponse) {
        try {
            // 使用模板引擎根据数据渲染HTML内容
            String html = TemplateUtil.createEngine(wordProperties.getTemplateConfig())
                    .getTemplate(wordResponse.template())
                    .render(data);

            // 将渲染后的HTML内容转换为DOCX格式，并写入输出流
            htmlToDocx(html, outputStream);
        } catch (Exception e) {
            log.error("处理HTML模板失败: {}", wordResponse.template(), e);
            throw new RuntimeException("处理HTML模板失败", e);
        }
    }

    private void htmlToDocx(String html, OutputStream outputStream) {
    }

}
