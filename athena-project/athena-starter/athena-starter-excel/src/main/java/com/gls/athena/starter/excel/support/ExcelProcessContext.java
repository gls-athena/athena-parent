package com.gls.athena.starter.excel.support;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel处理上下文
 * <p>
 * 用于在责任链中传递处理状态和数据
 * 实现了上下文模式，封装处理过程中的状态信息
 *
 * @author george
 */
@Data
@Accessors(chain = true)
public class ExcelProcessContext {

    /**
     * 当前处理的数据对象
     */
    private Object data;

    /**
     * 当前行号
     */
    private Integer rowIndex;

    /**
     * 表头信息
     */
    private Map<Integer, String> headMap = new HashMap<>();

    /**
     * 单元格数据
     */
    private Map<Integer, Object> cellMap = new HashMap<>();

    /**
     * 错误信息列表
     */
    private List<ExcelErrorMessage> errors = new ArrayList<>();

    /**
     * 扩展属性
     */
    private Map<String, Object> attributes = new HashMap<>();

    /**
     * 是否继续处理标志
     */
    private boolean shouldContinue = true;

    /**
     * 添加错误信息
     *
     * @param message 错误消息
     */
    public void addError(String message) {
        addError(new ExcelErrorMessage(rowIndex, "", message, ""));
    }

    /**
     * 添加错误信息
     *
     * @param error 错误对象
     */
    public void addError(ExcelErrorMessage error) {
        this.errors.add(error);
    }

    /**
     * 是否有错误
     *
     * @return true如果有错误
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * 设置扩展属性
     *
     * @param key   属性键
     * @param value 属性值
     */
    public void setAttribute(String key, Object value) {
        this.attributes.put(key, value);
    }

    /**
     * 获取扩展属性
     *
     * @param key 属性键
     * @return 属性值
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) this.attributes.get(key);
    }

    /**
     * 停止处理
     */
    public void stopProcessing() {
        this.shouldContinue = false;
    }
}
