package com.gls.athena.starter.word.processor;

import java.util.Map;

/**
 * 占位符处理器接口
 * 使用策略模式处理不同类型的占位符
 *
 * @author athena
 */
public interface PlaceholderProcessor {

    /**
     * 判断是否支持处理指定的占位符
     *
     * @param placeholder 占位符内容
     * @return 是否支持
     */
    boolean supports(String placeholder);

    /**
     * 处理占位符
     *
     * @param placeholder 占位符内容
     * @param data        数据上下文
     * @return 处理后的文本
     */
    String process(String placeholder, Map<String, Object> data);

    /**
     * 获取处理器优先级，数值越小优先级越高
     *
     * @return 优先级
     */
    default int getPriority() {
        return 100;
    }
}
