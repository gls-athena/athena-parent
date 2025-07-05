package com.gls.athena.starter.word.generator.render;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.util.Map;

/**
 * Word文档渲染上下文
 *
 * @author athena
 */
@Data
@RequiredArgsConstructor
public class WordRenderContext {

    /**
     * Word文档对象
     */
    private final XWPFDocument document;

    /**
     * 渲染参数
     */
    private final Map<String, Object> parameters;

    /**
     * 当前渲染路径
     */
    private String currentPath = "/";

    /**
     * 当前渲染深度
     */
    private int depth = 0;

    /**
     * 进入子节点
     *
     * @param nodeName 节点名称
     * @return 上下文对象
     */
    public WordRenderContext enter(String nodeName) {
        this.currentPath = this.currentPath.endsWith("/") ?
                this.currentPath + nodeName : this.currentPath + "/" + nodeName;
        this.depth++;
        return this;
    }

    /**
     * 退出当前节点
     *
     * @return 上下文对象
     */
    public WordRenderContext exit() {
        int lastSlashIndex = this.currentPath.lastIndexOf('/');
        if (lastSlashIndex > 0) {
            this.currentPath = this.currentPath.substring(0, lastSlashIndex);
        } else {
            this.currentPath = "/";
        }
        this.depth = Math.max(0, this.depth - 1);
        return this;
    }

    /**
     * 获取参数
     *
     * @param key 参数键
     * @param <T> 参数类型
     * @return 参数值
     */
    @SuppressWarnings("unchecked")
    public <T> T getParameter(String key) {
        return (T) parameters.get(key);
    }

    /**
     * 获取参数，不存在时返回默认值
     *
     * @param key          参数键
     * @param defaultValue 默认值
     * @param <T>          参数类型
     * @return 参数值
     */
    @SuppressWarnings("unchecked")
    public <T> T getParameter(String key, T defaultValue) {
        return (T) parameters.getOrDefault(key, defaultValue);
    }

    /**
     * 设置参数
     *
     * @param key   参数键
     * @param value 参数值
     * @return 上下文对象
     */
    public WordRenderContext setParameter(String key, Object value) {
        parameters.put(key, value);
        return this;
    }
}
