package com.gls.athena.starter.word.generator.render;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Word文档渲染管理器
 *
 * @author athena
 */
@Component
public class WordRenderManager {

    private List<WordElementRenderer> renderers = new ArrayList<>();

    /**
     * 注入渲染器
     */
    @Autowired(required = false)
    public void setRenderers(List<WordElementRenderer> renderers) {
        if (renderers != null) {
            this.renderers = new ArrayList<>(renderers);
            // 按优先级排序
            this.renderers.sort(Comparator.comparing(WordElementRenderer::getOrder));
        }
    }

    /**
     * 渲染文档
     *
     * @param document   Word文档
     * @param data       数据对象
     * @param parameters 参数
     * @return 渲染后的文档
     */
    public XWPFDocument render(XWPFDocument document, Object data, Map<String, Object> parameters) {
        if (parameters == null) {
            parameters = new HashMap<>();
        }

        WordRenderContext context = new WordRenderContext(document, parameters);
        renderNode(data, context);
        return document;
    }

    /**
     * 渲染数据节点
     *
     * @param data    数据节点
     * @param context 渲染上下文
     */
    private void renderNode(Object data, WordRenderContext context) {
        if (data == null) {
            return;
        }

        String currentPath = context.getCurrentPath();

        // 查找匹配的渲染器
        WordElementRenderer renderer = findRenderer(data, currentPath);
        if (renderer != null) {
            // 使用找到的渲染器渲染当前节点
            renderer.render(data, context);
        } else {
            // 如果没有找到匹配的渲染器，则根据数据类型进行处理
            if (data instanceof Map) {
                renderMapNode((Map<?, ?>) data, context);
            } else if (data instanceof Collection) {
                renderCollectionNode((Collection<?>) data, context);
            } else {
                // 简单类型节点，使用文本渲染
                renderSimpleNode(data, context);
            }
        }
    }

    /**
     * 渲染Map类型节点
     *
     * @param map     Map数据
     * @param context 渲染上下文
     */
    private void renderMapNode(Map<?, ?> map, WordRenderContext context) {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();

            if (key != null) {
                // 进入子节点
                context.enter(key.toString());

                // 如果键是字符串，而且内容不是复杂对象，可以作为键值对渲染
                boolean isSimpleKeyValue = !(value instanceof Map) &&
                        !(value instanceof Collection) &&
                        value != null;

                if (isSimpleKeyValue) {
                    // 尝试使用键值对渲染器
                    WordElementRenderer keyValueRenderer = findRenderer(
                            Map.of(key, value), context.getCurrentPath() + "/keyvalue");
                    if (keyValueRenderer != null) {
                        keyValueRenderer.render(Map.of(key, value), context);
                    } else {
                        // 否则作为普通节点处理
                        renderNode(value, context);
                    }
                } else {
                    // 复杂对象，先渲染键作为标题
                    WordElementRenderer titleRenderer = findRenderer(key.toString(), context.getCurrentPath() + "/title");
                    if (titleRenderer != null) {
                        titleRenderer.render(key.toString(), context);
                    }

                    // 然后递归渲染值
                    renderNode(value, context);
                }

                // 退出子节点
                context.exit();
            }
        }
    }

    /**
     * 渲染集合类型节点
     *
     * @param collection 集合数据
     * @param context    渲染上下文
     */
    private void renderCollectionNode(Collection<?> collection, WordRenderContext context) {
        if (collection.isEmpty()) {
            return;
        }

        // 首先尝试使用表格渲染器
        WordElementRenderer tableRenderer = findRenderer(collection, context.getCurrentPath() + "/table");
        if (tableRenderer != null) {
            tableRenderer.render(collection, context);
            return;
        }

        // 如果不适合表格，尝试使用列表渲染器
        WordElementRenderer listRenderer = findRenderer(collection, context.getCurrentPath() + "/list");
        if (listRenderer != null) {
            listRenderer.render(collection, context);
            return;
        }

        // 如果没有特定的渲染器，则逐个渲染集合元素
        int index = 0;
        for (Object item : collection) {
            context.enter("item[" + index + "]");
            renderNode(item, context);
            context.exit();
            index++;
        }
    }

    /**
     * 渲染简单类型节点
     *
     * @param data    简单数据
     * @param context 渲染上下文
     */
    private void renderSimpleNode(Object data, WordRenderContext context) {
        // 使用文本渲染器
        WordElementRenderer textRenderer = findRenderer(data, context.getCurrentPath() + "/text");
        if (textRenderer != null) {
            textRenderer.render(data, context);
        } else {
            // 如果找不到特定的渲染器，使用默认的文本渲染
            XWPFDocument document = context.getDocument();
            document.createParagraph().createRun().setText(data.toString());
        }
    }

    /**
     * 查找适合的渲染器
     *
     * @param data 数据对象
     * @param path 数据路径
     * @return 渲染器或null
     */
    private WordElementRenderer findRenderer(Object data, String path) {
        for (WordElementRenderer renderer : renderers) {
            if (renderer.supports(data, path)) {
                return renderer;
            }
        }
        return null;
    }
}
