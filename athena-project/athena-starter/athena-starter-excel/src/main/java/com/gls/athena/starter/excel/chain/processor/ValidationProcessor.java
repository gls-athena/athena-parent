package com.gls.athena.starter.excel.chain.processor;

import cn.hutool.extra.validation.BeanValidationResult;
import cn.hutool.extra.validation.ValidationUtil;
import com.gls.athena.starter.excel.chain.AbstractExcelProcessor;
import com.gls.athena.starter.excel.support.ExcelErrorMessage;
import com.gls.athena.starter.excel.support.ExcelProcessContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 数据校验处理器
 * <p>
 * 负责对Excel数据进行Bean Validation校验
 *
 * @author george
 */
@Slf4j
public class ValidationProcessor extends AbstractExcelProcessor {

    @Override
    protected boolean doProcess(ExcelProcessContext context) {
        Object data = context.getData();

        if (data == null) {
            return true;
        }

        try {
            BeanValidationResult validationResult = ValidationUtil.warpValidate(data);

            if (!validationResult.isSuccess()) {
                // 将校验错误转换为Excel错误信息
                validationResult.getErrorMessages().forEach(errorMsg -> {
                    ExcelErrorMessage excelError = new ExcelErrorMessage(
                            context.getRowIndex(),
                            errorMsg.getPropertyName(),
                            errorMsg.getMessage(),
                            String.valueOf(errorMsg.getValue())
                    );
                    context.addError(excelError);
                });

                log.debug("第{}行数据校验失败: {}", context.getRowIndex(), validationResult.getErrorMessages());
            }
        } catch (Exception e) {
            log.error("数据校验异常", e);
            context.addError("数据校验异常: " + e.getMessage());
        }

        return true;
    }

    @Override
    public String getProcessorName() {
        return "ValidationProcessor";
    }
}
