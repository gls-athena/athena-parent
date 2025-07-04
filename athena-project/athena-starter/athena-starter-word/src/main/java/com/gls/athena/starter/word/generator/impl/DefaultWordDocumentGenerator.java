package com.gls.athena.starter.word.generator.impl;

import com.gls.athena.starter.word.annotation.WordResponse;
import com.gls.athena.starter.word.generator.WordDocumentGenerator;
import com.gls.athena.starter.word.generator.render.WordElementRenderer;
import com.gls.athena.starter.word.generator.render.WordRenderContext;
import com.gls.athena.starter.word.generator.render.impl.KeyValueRenderer;
import com.gls.athena.starter.word.generator.render.impl.ListRenderer;
import com.gls.athena.starter.word.generator.render.impl.TableRenderer;
import com.gls.athena.starter.word.generator.render.impl.TitleRenderer;
import lombok.NoArgsConstructor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * 基础Word文档生成器实现
 *
 * @author athena
 */
@NoArgsConstructor
public class DefaultWordDocumentGenerator implements WordDocumentGenerator {

    private List<WordElementRenderer> renderers = new ArrayList<>(Arrays.asList(
            new TitleRenderer(),
            new TableRenderer(),
            new ListRenderer(),
            new KeyValueRenderer()
    ));

    /**
     * 注入自定义渲染器
     */
    @Autowired(required = false)
    public void setRenderers(List<WordElementRenderer> customRenderers) {
        if (customRenderers != null && !customRenderers.isEmpty()) {
            // 优先使用自定义渲染器
            this.renderers.addAll(0, customRenderers);
        }
    }

    @Override
    public XWPFDocument generate(Object data, WordResponse wordResponse) {
        XWPFDocument document = new XWPFDocument();
        Map<String, Object> context = new HashMap<>();

        // 处理标题
        if (!wordResponse.title().isEmpty()) {
            // 使用标题渲染器渲染文档标题
            context.put("level", 1);  // 一级标题
            WordRenderContext titleContext = new WordRenderContext(document, context);
            findRenderer(wordResponse.title(), TitleRenderer.class)
                    .render(wordResponse.title(), titleContext);
        }

        // 根据数据类型选择合适的渲染器渲染内容
        if (data instanceof Map) {
            renderMapData(document, (Map<?, ?>) data, context);
        } else if (data instanceof Collection) {
            renderCollectionData(document, (Collection<?>) data, context);
        } else {
            // 简单对象，直接使用toString()展示
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText(data.toString());
        }

        return document;
    }

    @Override
    public boolean supports(Class<?> dataClass) {
        // 默认支持所有类型的数据
        return true;
    }

    /**
     * 渲染Map类型数据
     */
    private void renderMapData(XWPFDocument document, Map<?, ?> data, Map<String, Object> context) {
        // 使用KeyValueRenderer渲染简单键值对
        WordElementRenderer keyValueRenderer = findRenderer(data, KeyValueRenderer.class);
        WordRenderContext renderContext = new WordRenderContext(document, context);
        keyValueRenderer.render(data, renderContext);

        // 处理Map中的集合值，为每个集合添加子标题并渲染
        for (Map.Entry<?, ?> entry : data.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Collection && !((Collection<?>) value).isEmpty()) {
                Collection<?> collection = (Collection<?>) value;

                // 为集合添加子标题
                context.put("level", 2);  // 二级标题
                WordRenderContext titleContext = new WordRenderContext(document, context);
                findRenderer(key.toString(), TitleRenderer.class)
                        .render(key.toString(), titleContext);

                // 根据集合类型选择合适的渲染器
                renderCollectionData(document, collection, context);
            }
        }
    }

    /**
     * 渲染Collection类型数据
     */
    private void renderCollectionData(XWPFDocument document, Collection<?> data, Map<String, Object> context) {
        String path = "/collection"; // 默认集合路径

        // 首先尝试使用表格渲��器
        WordElementRenderer tableRenderer = findRenderer(data, TableRenderer.class);
        if (tableRenderer.supports(data, path)) {
            WordRenderContext renderContext = new WordRenderContext(document, context);
            tableRenderer.render(data, renderContext);
            return;
        }

        // 如果不支持表格，使用列表渲染器
        WordElementRenderer listRenderer = findRenderer(data, ListRenderer.class);
        if (listRenderer.supports(data, path)) {
            WordRenderContext renderContext = new WordRenderContext(document, context);
            listRenderer.render(data, renderContext);
            return;
        }

        // 如果都不支持，使用简单文本输出
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        for (Object item : data) {
            run.setText(item.toString());
            run.addBreak();
        }
    }

    /**
     * 查找适合的渲染器
     */
    private WordElementRenderer findRenderer(Object data, Class<? extends WordElementRenderer> preferredType) {
        String path = "/"; // 默认路径

        // 首先尝试查找指定类型的渲染器
        for (WordElementRenderer renderer : renderers) {
            if (preferredType.isInstance(renderer) && renderer.supports(data, path)) {
                return renderer;
            }
        }

        // 如果没有找到指定类型的，则查找任何支持的渲染器
        for (WordElementRenderer renderer : renderers) {
            if (renderer.supports(data, path)) {
                return renderer;
            }
        }

        // 如果仍然没有找到，使用preferredType的一个实例（如果可能）
        for (WordElementRenderer renderer : renderers) {
            if (preferredType.isInstance(renderer)) {
                return renderer;
            }
        }

        // 最后的fallback，返回第一个渲染器
        return renderers.get(0);
    }
}
