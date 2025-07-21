package com.gls.athena.starter.json.support;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 默认日期格式类，继承自SimpleDateFormat
 * 用于统一定义和处理日期格式
 *
 * @author george
 */
public class DefaultDateFormat extends SimpleDateFormat {

    /**
     * 构造函数，初始化日期格式为标准日期时间格式
     * 标准日期时间格式为：yyyy-MM-dd HH:mm:ss
     */
    public DefaultDateFormat() {
        super(DatePattern.NORM_DATETIME_PATTERN);
    }

    /**
     * 重写parse方法，使用DateUtil解析字符串日期
     * 此方法不抛出异常，适用于需要容错处理的场景
     *
     * @param text 日期字符串
     * @param pos  解析位置
     * @return 解析后的日期对象
     */
    @Override
    public Date parse(String text, ParsePosition pos) {
        return DateUtil.parse(text);
    }

    /**
     * 重写parse方法，使用DateUtil解析字符串日期
     * 此方法可能抛出ParseException异常，适用于需要精确控制解析过程的场景
     *
     * @param source 日期字符串
     * @return 解析后的日期对象
     * @throws ParseException 如果解析失败，抛出此异常
     */
    @Override
    public Date parse(String source) throws ParseException {
        return DateUtil.parse(source);
    }

}
