package com.gls.athena.starter.excel.chain.processor;

import com.gls.athena.starter.excel.annotation.ExcelLine;
import com.gls.athena.starter.excel.chain.AbstractExcelProcessor;
import com.gls.athena.starter.excel.support.ExcelProcessContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

/**
 * 行号处理器
 * <p>
 * 负责处理带有@ExcelLine注解的字段，设置当前行号
 *
 * @author george
 */
@Slf4j
public class RowNumberProcessor extends AbstractExcelProcessor {

    @Override
    protected boolean doProcess(ExcelProcessContext context) {
        Object data = context.getData();
        Integer rowIndex = context.getRowIndex();

        if (data == null || rowIndex == null) {
            return true;
        }

        Field[] fields = data.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(ExcelLine.class) && field.getType().equals(Integer.class)) {
                try {
                    field.setAccessible(true);
                    field.set(data, rowIndex);
                    log.debug("设置行号: {} -> {}", field.getName(), rowIndex);
                } catch (IllegalAccessException e) {
                    log.warn("设置行号失败: {}", field.getName(), e);
                    context.addError("设置行号失败: " + field.getName());
                }
            }
        }

        return true;
    }

    @Override
    public String getProcessorName() {
        return "RowNumberProcessor";
    }
}
