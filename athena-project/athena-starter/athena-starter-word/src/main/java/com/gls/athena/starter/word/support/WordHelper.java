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

    public void handleDocxTemplate(Map<String, Object> data, OutputStream outputStream, WordResponse wordResponse) {
    }

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
