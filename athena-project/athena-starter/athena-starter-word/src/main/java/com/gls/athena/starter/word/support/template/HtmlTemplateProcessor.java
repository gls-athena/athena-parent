package com.gls.athena.starter.word.support.template;

import cn.hutool.extra.template.TemplateUtil;
import com.gls.athena.starter.word.annotation.WordResponse;
import com.gls.athena.starter.word.config.WordProperties;
import com.gls.athena.starter.word.config.WordTemplateType;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.util.Map;

/**
 * HTML模板处理器
 * 负责处理HTML格式的模板文件并转换为Word文档
 *
 * @author lizy19
 */
@Slf4j
@Component
public class HtmlTemplateProcessor implements TemplateProcessor {

    @Resource
    private WordProperties wordProperties;

    @Override
    public void processTemplate(Map<String, Object> data, OutputStream outputStream, WordResponse wordResponse) {
        try {
            // 使用模板引擎根据数据渲染HTML内容
            String html = TemplateUtil.createEngine(wordProperties.getTemplateConfig())
                    .getTemplate(wordResponse.template())
                    .render(data);

            // 将渲染后的HTML内容转换为DOCX格式，并写入输出流
            htmlToDocx(html, outputStream);

            log.info("成功处理HTML模板: {}", wordResponse.template());
        } catch (Exception e) {
            // 记录错误日志，并抛出运行时异常，便于上层处理
            log.error("处理HTML模板失败: {}", wordResponse.template(), e);
            throw new RuntimeException("处理HTML模板失败", e);
        }
    }

    @Override
    public boolean supports(WordTemplateType templateType) {
        return WordTemplateType.HTML.equals(templateType);
    }

    /**
     * 将HTML内容转换为Docx格式文件
     * 此方法用于接收HTML格式的字符串，并将其转换为Docx格式的文件输出
     * 主要解决了从HTML到Docx的格式转换问题，使得可以方便地将网页内容保存为Word文档
     *
     * @param html         HTML格式的字符串，包含需要转换的内容
     * @param outputStream 输出流，用于保存转换后的Docx文件
     */
    private void htmlToDocx(String html, OutputStream outputStream) {
        // TODO: 实现将HTML转换为Docx的逻辑
    }
}
