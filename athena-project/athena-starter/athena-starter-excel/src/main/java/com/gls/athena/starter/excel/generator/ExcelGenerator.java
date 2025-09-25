/**
 * Excel生成器接口，用于定义生成Excel文件的标准方式
 * 此接口的主要作用是提供一个统一的方法来生成Excel文件，以及一个默认方法来判断当前生成器是否支持处理给定的Excel响应
 */
package com.gls.athena.starter.excel.generator;

import com.gls.athena.starter.excel.annotation.ExcelResponse;
import com.gls.athena.starter.file.generator.FileGenerator;

/**
 * Excel生成器接口
 *
 * @author george
 */
public interface ExcelGenerator extends FileGenerator<ExcelResponse> {

    /**
     * 判断当前Excel生成器是否支持处理给定的Excel响应的默认方法
     *
     * @param excelResponse 要检查的ExcelResponse对象
     * @return 如果当前生成器支持处理给定的Excel响应，则返回true；否则返回false
     */
    @Override
    default boolean supports(ExcelResponse excelResponse) {
        return excelResponse.generator().equals(this.getClass());
    }
}
