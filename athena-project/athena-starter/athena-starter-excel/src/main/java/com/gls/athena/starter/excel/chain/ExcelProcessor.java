package com.gls.athena.starter.excel.chain;

import com.gls.athena.starter.excel.support.ExcelProcessContext;

/**
 * Excel处理器接口
 * <p>
 * 使用责任链模式处理Excel数据
 * 每个处理器负责特定的处理逻辑
 *
 * @author george
 */
public interface ExcelProcessor {

    /**
     * 设置下一个处理器
     *
     * @param next 下一个处理器
     * @return 当前处理器(支持链式调用)
     */
    ExcelProcessor setNext(ExcelProcessor next);

    /**
     * 处理Excel数据
     *
     * @param context 处理上下文
     * @return 是否继续处理
     */
    boolean process(ExcelProcessContext context);

    /**
     * 获取处理器名称
     *
     * @return 处理器名称
     */
    String getProcessorName();
}
