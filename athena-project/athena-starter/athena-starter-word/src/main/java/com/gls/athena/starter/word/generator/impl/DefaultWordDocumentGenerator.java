package com.gls.athena.starter.word.generator.impl;

import com.gls.athena.starter.word.annotation.WordResponse;
import com.gls.athena.starter.word.generator.WordDocumentGenerator;
import com.gls.athena.starter.word.generator.render.WordRenderContext;
import com.gls.athena.starter.word.generator.render.WordRenderManager;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基础Word文档生成器实现
 *
 * @author athena
 */
@Slf4j
@Component
public class DefaultWordDocumentGenerator implements WordDocumentGenerator {

    /**
     * 渲染管理器
     */
    @Resource
    private WordRenderManager renderManager;

    @Override
    public XWPFDocument generate(Object data, WordResponse wordResponse) {
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if (wordResponse == null) {
            throw new IllegalArgumentException("WordResponse annotation cannot be null");
        }

        log.debug("Generating Word document for data type: {}", data.getClass().getSimpleName());

        XWPFDocument document = new XWPFDocument();
        Map<String, Object> parameters = createBaseParameters(wordResponse);

        try {
            // 渲染文档标题
            renderDocumentTitle(document, wordResponse, parameters);

            // 使用渲染管理器渲染文档内容
            renderManager.render(document, data, parameters);

            log.debug("Word document generated successfully");
            return document;
        } catch (Exception e) {
            log.error("Failed to generate Word document", e);
            // 确保资源清理
            try {
                document.close();
            } catch (Exception closeException) {
                log.warn("Failed to close document after generation error", closeException);
            }
            throw new RuntimeException("Word document generation failed", e);
        }
    }

    @Override
    public boolean supports(Class<?> dataClass) {
        return List.class.isAssignableFrom(dataClass);
    }

    /**
     * 创建基础参数
     */
    private Map<String, Object> createBaseParameters(WordResponse wordResponse) {
        Map<String, Object> parameters = new HashMap<>(8);
        parameters.put("timestamp", new Date());
        parameters.put("title", wordResponse.title());
        return parameters;
    }

    /**
     * 渲染文档标题
     */
    private void renderDocumentTitle(XWPFDocument document, WordResponse wordResponse, Map<String, Object> parameters) {
        if (StringUtils.hasText(wordResponse.title())) {
            log.debug("Rendering document title: {}", wordResponse.title());

            // 创建标题渲染上下文
            WordRenderContext titleContext = new WordRenderContext(document, parameters);
            titleContext.enter("title");

            // 使用渲染管理器渲染标题
            renderManager.renderNode(wordResponse.title(), titleContext);

            titleContext.exit();
        }
    }
}
