package com.gls.athena.starter.excel.chain;

import com.gls.athena.starter.excel.support.ExcelProcessContext;
import lombok.extern.slf4j.Slf4j;

/**
 * Excel处理器抽象基类
 * <p>
 * 实现责任链模式的基础结构
 * 提供通用的链式调用逻辑
 *
 * @author george
 */
@Slf4j
public abstract class AbstractExcelProcessor implements ExcelProcessor {

    /**
     * 下一个处理器
     */
    protected ExcelProcessor next;

    @Override
    public ExcelProcessor setNext(ExcelProcessor next) {
        this.next = next;
        return next;
    }

    @Override
    public boolean process(ExcelProcessContext context) {
        try {
            // 执行当前处理器的逻辑
            boolean shouldContinue = doProcess(context);

            if (shouldContinue && next != null) {
                // 继续执行下一个处理器
                return next.process(context);
            }

            return shouldContinue;
        } catch (Exception e) {
            log.error("处理器 {} 执行失败", getProcessorName(), e);
            handleException(context, e);
            return false;
        }
    }

    /**
     * 具体的处理逻辑，由子类实现
     *
     * @param context 处理上下文
     * @return 是否继续执行后续处理器
     */
    protected abstract boolean doProcess(ExcelProcessContext context);

    /**
     * 异常处理，子类可重写
     *
     * @param context 处理上下文
     * @param e       异常信息
     */
    protected void handleException(ExcelProcessContext context, Exception e) {
        context.addError("处理器异常: " + getProcessorName() + " - " + e.getMessage());
    }
}
