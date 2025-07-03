package com.gls.athena.starter.word.support;

import cn.hutool.extra.template.TemplateUtil;
import com.gls.athena.starter.word.annotation.WordResponse;
import com.gls.athena.starter.word.config.WordProperties;
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

    /**
     * 处理docx模板，根据提供的数据动态填充模板内容，并将结果输出到指定的输出流
     *
     * @param data         包含填充模板所需数据的映射，键通常为模板中的占位符，值为替换占位符的实际内容
     * @param outputStream 用于输出生成的docx文件的流，调用者负责关闭此流
     * @param wordResponse 提供与Word相关的响应处理，可能包括错误处理、文档属性设置等
     */
    public void handleDocxTemplate(Map<String, Object> data, OutputStream outputStream, WordResponse wordResponse) {
        // TODO: 实现处理docx模板的逻辑
    }

    /**
     * 根据HTML模板生成Word文档
     * 此方法使用模板引擎将给定的数据和模板转换为HTML，然后将HTML转换为Word文档并输出
     *
     * @param data         包含模板所需数据的映射
     * @param outputStream 用于输出生成的Word文档的输出流
     * @param wordResponse 包含模板信息和响应处理程序的对象
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
            // 记录错误日志，并抛出运行时异常，便于上层处理
            log.error("处理HTML模板失败: {}", wordResponse.template(), e);
            throw new RuntimeException("处理HTML模板失败", e);
        }
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
