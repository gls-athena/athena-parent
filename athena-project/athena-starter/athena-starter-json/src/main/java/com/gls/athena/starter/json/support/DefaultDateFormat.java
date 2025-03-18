package com.gls.athena.starter.json.support;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 默认日期格式化类
 *
 * @author george
 */
public class DefaultDateFormat extends SimpleDateFormat {

    /**
     * 构造
     */
    public DefaultDateFormat() {
        super(DatePattern.NORM_DATETIME_PATTERN);
    }

    /**
     * 将给定的日期字符串解析为 {@link Date} 对象。
     * 该方法重写了父类的 {@code parse} 方法，调用 {@link DateUtil#parse(String)} 进行实际的日期解析操作。
     *
     * @param text 需要解析的日期字符串。该字符串应符合 {@link DateUtil} 的解析格式要求。
     * @param pos  {@link ParsePosition} 对象，用于指定解析的起始位置。虽然在此方法中未直接使用，但保留以符合接口规范。
     * @return 解析后的 {@link Date} 对象。如果解析失败，则返回 {@code null}。
     */
    @Override
    public Date parse(String text, ParsePosition pos) {
        return DateUtil.parse(text);
    }

    /**
     * 解析日期字符串并返回对应的Date对象。
     * <p>
     * 该方法通过调用DateUtil工具类的parse方法，将传入的日期字符串解析为Date对象。
     * 如果解析过程中发生异常，将抛出ParseException。
     *
     * @param source 需要解析的日期字符串，格式应符合DateUtil工具类的解析要求。
     * @return 解析成功后的Date对象。
     * @throws ParseException 如果日期字符串无法被解析为有效的Date对象，则抛出此异常。
     */
    @Override
    public Date parse(String source) throws ParseException {
        return DateUtil.parse(source);
    }

}
