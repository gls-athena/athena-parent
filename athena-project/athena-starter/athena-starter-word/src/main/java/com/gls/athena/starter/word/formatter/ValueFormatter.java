package com.gls.athena.starter.word.formatter;

/**
 * 值格式化器接口
 * 负责将数据值格式化为字符串
 *
 * @author athena
 */
public interface ValueFormatter {

    /**
     * 格式化值
     *
     * @param value      要格式化的值
     * @param formatSpec 格式规范，可以为null
     * @return 格式化后的字符串
     */
    String format(Object value, String formatSpec);
}
