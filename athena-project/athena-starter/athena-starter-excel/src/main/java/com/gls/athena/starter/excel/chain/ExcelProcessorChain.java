package com.gls.athena.starter.excel.chain;

import com.gls.athena.starter.excel.chain.processor.MultiColumnProcessor;
import com.gls.athena.starter.excel.chain.processor.RowNumberProcessor;
import com.gls.athena.starter.excel.chain.processor.ValidationProcessor;
import com.gls.athena.starter.excel.support.ExcelProcessContext;
import lombok.extern.slf4j.Slf4j;

/**
 * Excel处理器链管理器
 * <p>
 * 负责构建和管理Excel数据处理的责任链
 * 实现了建造者模式来构建处理链
 *
 * @author george
 */
@Slf4j
public class ExcelProcessorChain {

    private ExcelProcessor head;

    /**
     * 构建默认处理链
     * 处理顺序：行号处理 -> 多列数据处理 -> 数据校验
     */
    public static ExcelProcessorChain buildDefaultChain() {
        ExcelProcessorChain chain = new ExcelProcessorChain();

        // 构建处理链
        ExcelProcessor rowNumberProcessor = new RowNumberProcessor();
        ExcelProcessor multiColumnProcessor = new MultiColumnProcessor();
        ExcelProcessor validationProcessor = new ValidationProcessor();

        // 设置链式关系
        rowNumberProcessor
                .setNext(multiColumnProcessor)
                .setNext(validationProcessor);

        chain.head = rowNumberProcessor;

        log.debug("构建默认Excel处理链: {} -> {} -> {}",
                rowNumberProcessor.getProcessorName(),
                multiColumnProcessor.getProcessorName(),
                validationProcessor.getProcessorName());

        return chain;
    }

    /**
     * 自定义构建处理链
     */
    public static ExcelProcessorChain buildCustomChain(ExcelProcessor... processors) {
        ExcelProcessorChain chain = new ExcelProcessorChain();

        if (processors.length > 0) {
            chain.head = processors[0];

            // 链接处理器
            for (int i = 0; i < processors.length - 1; i++) {
                processors[i].setNext(processors[i + 1]);
            }

            log.debug("构建自定义Excel处理链，处理器数量: {}", processors.length);
        }

        return chain;
    }

    /**
     * 执行处理链
     */
    public boolean process(ExcelProcessContext context) {
        if (head == null) {
            log.warn("处理链为空，跳过处理");
            return true;
        }

        try {
            return head.process(context);
        } catch (Exception e) {
            log.error("处理链执行异常", e);
            context.addError("处理链执行异常: " + e.getMessage());
            return false;
        }
    }

    /**
     * 添加处理器到链尾
     */
    public ExcelProcessorChain addProcessor(ExcelProcessor processor) {
        if (head == null) {
            head = processor;
        } else {
            // 找到链尾并添加
            ExcelProcessor current = head;
            while (current instanceof AbstractExcelProcessor &&
                    ((AbstractExcelProcessor) current).next != null) {
                current = ((AbstractExcelProcessor) current).next;
            }
            current.setNext(processor);
        }

        log.debug("添加处理器到链尾: {}", processor.getProcessorName());
        return this;
    }
}
