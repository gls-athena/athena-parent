package com.gls.athena.starter.word.generator.render;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Word文档渲染管理器
 * 负责管理和协调各种渲染器，提供统一的渲染入口
 *
 * @author athena
 */
@Slf4j
@Component
public class WordRenderManager {

    /**
     * 渲染器列表，按优先级排序
     */
    @Resource
    private List<WordElementRender> renders;

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
            parameters = new HashMap<>(4);
        }

        WordRenderContext context = new WordRenderContext(document, parameters);
        renderNode(data, context);
        return document;
    }

    /**
     * 渲染数据节点（公共方法，供外部调用）
     *
     * @param data    数据节点
     * @param context 渲染上下文
     */
    public void renderNode(Object data, WordRenderContext context) {
        if (data == null) {
            return;
        }

        String currentPath = context.getCurrentPath();

        // 查找匹配的渲染器
        WordElementRender renderer = findRender(data, currentPath);
        if (renderer != null) {
            // 使用找到的渲染器渲染当前节点
            renderer.render(data, context);
        } else {
            // 如果没有匹配的渲染器，则使用默认的渲染器
            defaultRender(data, context);
        }
    }

    /**
     * 默认数据渲染方法，当没有找到匹配的数据渲染器时使用
     *
     * @param data    需要渲染的数据对象，可以是任意类型
     * @param context Word渲染上下文对象，包含文档操作相关方法
     * @implNote 该方法会：
     * 1. 输出警告日志提示使用默认渲染方式
     * 2. 将数据对象转为字符串后写入Word文档新段落
     */
    private void defaultRender(Object data, WordRenderContext context) {
        log.warn("没有找到适合的数据渲染器，使用默认渲染方式处理数据: {}", data);

        // 默认渲染逻辑：创建新段落并写入数据对象的字符串表示
        context.getDocument().createParagraph().createRun().setText(data.toString());
    }

    /**
     * 查找适合的渲染器
     *
     * @param data 数据对象
     * @param path 数据路径
     * @return 渲染器或null
     */
    public WordElementRender findRender(Object data, String path) {
        return renders.stream().filter(renderer -> renderer.supports(data, path)).findFirst().orElse(null);
    }

}
