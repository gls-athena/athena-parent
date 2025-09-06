package com.gls.athena.starter.excel.listener;

import cn.idev.excel.read.listener.ReadListener;
import com.gls.athena.starter.excel.web.domain.ExcelErrorMessage;

import java.util.List;

/**
 * 读取监听器
 *
 * @author george
 */
public interface IReadListener<T> extends ReadListener<T> {

    /**
     * 获取对象列表
     *
     * @return 对象列表
     */
    List<T> getList();

    /**
     * 获取异常校验结果
     *
     * @return 异常校验结果
     */
    List<ExcelErrorMessage> getErrors();
}
